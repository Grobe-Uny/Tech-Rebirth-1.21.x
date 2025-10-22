package com.grobe.techrebirth.block.custom.entity.bank;

import com.grobe.techrebirth.block.ModBlockEntities;
import com.grobe.techrebirth.event.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;

import java.util.*;

public class EnergyBankBlockEntity extends BlockEntity {
    public static final int BASE_CAPACITY = 100_000;
    public static final int MAX_IO = 1000;

    // Actual per-block storage
    private final ModEnergy localEnergy = new ModEnergy(BASE_CAPACITY, MAX_IO, MAX_IO, 0);

    // Network wrapper exposed via capability
    private final NetworkEnergyStorage networkEnergy = new NetworkEnergyStorage();

    public EnergyBankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_BANK.get(), pos, state);
    }

    public EnergyStorage getExposedEnergyStorage() {
        return networkEnergy;
    }

    public ModEnergy getLocalEnergy() {
        return localEnergy;
    }

    private class ModEnergy extends EnergyStorage {
        public ModEnergy(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }

        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            EnergyBankBlockEntity.this.setChanged();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) EnergyBankBlockEntity.this.setChanged();
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) EnergyBankBlockEntity.this.setChanged();
            return extracted;
        }
    }

    // Energy storage that proxies operations to the whole connected cluster
    private class NetworkEnergyStorage extends EnergyStorage {
        public NetworkEnergyStorage() {
            super(BASE_CAPACITY, MAX_IO, MAX_IO, 0);
        }

        @Override
        public int getEnergyStored() {
            List<EnergyBankBlockEntity> cluster = getCluster();
            int sum = 0;
            for (EnergyBankBlockEntity be : cluster) {
                sum += be.localEnergy.getEnergyStored();
            }
            return sum;
        }

        @Override
        public int getMaxEnergyStored() {
            List<EnergyBankBlockEntity> cluster = getCluster();
            return cluster.size() * BASE_CAPACITY;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            Level lvl = level;
            if (lvl == null || lvl.isClientSide()) return 0;
            List<EnergyBankBlockEntity> cluster = getCluster();
            int totalSpace = 0;
            for (EnergyBankBlockEntity be : cluster) {
                totalSpace += (BASE_CAPACITY - be.localEnergy.getEnergyStored());
            }
            int toAccept = Math.min(maxReceive, totalSpace);
            if (toAccept <= 0) return 0;
            if (simulate) return toAccept;

            // Distribute greedily to members with most free space
            cluster.sort(Comparator.comparingInt(be -> be.localEnergy.getEnergyStored())); // ascending energy => more free space later fill
            Collections.reverse(cluster); // descending by energy (we actually want free space; sort by free space desc instead)
            cluster.sort(Comparator.comparingInt(be -> (BASE_CAPACITY - be.localEnergy.getEnergyStored())));
            Collections.reverse(cluster); // now by free space desc

            int remaining = toAccept;
            for (EnergyBankBlockEntity be : cluster) {
                if (remaining <= 0) break;
                int space = BASE_CAPACITY - be.localEnergy.getEnergyStored();
                if (space <= 0) continue;
                int give = Math.min(space, Math.min(remaining, MAX_IO));
                int received = be.localEnergy.receiveEnergy(give, false);
                remaining -= received;
            }
            return toAccept - remaining;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            Level lvl = level;
            if (lvl == null || lvl.isClientSide()) return 0;
            List<EnergyBankBlockEntity> cluster = getCluster();
            int totalEnergy = 0;
            for (EnergyBankBlockEntity be : cluster) {
                totalEnergy += be.localEnergy.getEnergyStored();
            }
            int toGive = Math.min(maxExtract, totalEnergy);
            if (toGive <= 0) return 0;
            if (simulate) return toGive;

            // Pull greedily from members with most energy
            cluster.sort(Comparator.comparingInt(be -> be.localEnergy.getEnergyStored()));
            Collections.reverse(cluster); // now desc by stored energy

            int remaining = toGive;
            for (EnergyBankBlockEntity be : cluster) {
                if (remaining <= 0) break;
                int available = be.localEnergy.getEnergyStored();
                if (available <= 0) continue;
                int take = Math.min(available, Math.min(remaining, MAX_IO));
                int extracted = be.localEnergy.extractEnergy(take, false);
                remaining -= extracted;
            }
            return toGive - remaining;
        }
    }

    private List<EnergyBankBlockEntity> getCluster() {
        Level lvl = this.level;
        if (lvl == null) return Collections.singletonList(this);
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        List<EnergyBankBlockEntity> result = new ArrayList<>();
        BlockPos start = this.worldPosition;
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            BlockPos cur = queue.poll();
            BlockEntity be = lvl.getBlockEntity(cur);
            if (be instanceof EnergyBankBlockEntity bank) {
                result.add(bank);
                for (Direction d : Direction.values()) {
                    BlockPos np = cur.relative(d);
                    if (visited.contains(np)) continue;
                    BlockEntity nbe = lvl.getBlockEntity(np);
                    if (nbe instanceof EnergyBankBlockEntity) {
                        visited.add(np);
                        queue.add(np);
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("bank_energy", localEnergy.getEnergyStored());
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag,provider);
        localEnergy.setEnergy(tag.getInt("bank_energy"));
    }

    // Server-side tick to distribute energy from the bank network to neighbors
    public static void tick(Level level, BlockPos pos, BlockState state, EnergyBankBlockEntity be) {
        if (level.isClientSide()) return;
        EnergyStorage network = be.getExposedEnergyStorage();
        if (network.getEnergyStored() <= 0) return;
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (network.getEnergyStored() <= 0) break;
            BlockPos nPos = pos.relative(dir);
            BlockState nState = level.getBlockState(nPos);
            BlockEntity nBe = level.getBlockEntity(nPos);
        }
    }
}
