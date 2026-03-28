package com.grobe.techrebirth.block.custom.entity;

import com.grobe.techrebirth.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.energy.EnergyStorage;

import java.util.*;

public class EnergyCableBlockEntity extends BlockEntity {

    private class ModEnergy extends EnergyStorage {
        public ModEnergy(int capacity, int maxReceive, int maxExtract, int energy) {
            super(capacity, maxReceive, maxExtract, energy);
        }

        public void setEnergy(int energy) {
            this.energy = Math.min(energy, this.capacity);
            EnergyCableBlockEntity.this.setChanged();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate && received > 0) {
                EnergyCableBlockEntity.this.setChanged();
                EnergyCableBlockEntity.this.needsDistribution = true;
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = super.extractEnergy(maxExtract, simulate);
            if (!simulate && extracted > 0) EnergyCableBlockEntity.this.setChanged();
            return extracted;
        }
    }

    private final ModEnergy energyStorage = new ModEnergy(1000, 1024, 1024, 0);
    private boolean needsDistribution = true;
    private final Set<BlockPos> networkMembers = new HashSet<>();
    private int tickCounter = 0;

    // Dodaj ovaj record na vrh klase (iza polja)
    private record EnergyTransfer(EnergyCableBlockEntity source, IEnergyStorage target, int amount) {}
    private record EnergySource(IEnergyStorage storage, int availableEnergy) {}

    public EnergyCableBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntities.ENERGY_CABLE.get(), pPos, pState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EnergyCableBlockEntity be) {
        if (level.isClientSide()) return;

        be.tickCounter++;

        // Rebuild network svakih 20 tickova ili kad je potrebno
        if (be.tickCounter % 20 == 0 || be.needsDistribution) {
            be.rebuildNetwork(level, pos);
            be.needsDistribution = false;
        }

        // Pull iz generatora PRVO
        if (be.tickCounter % 5 == 0) {
            be.pullEnergyFromGenerators(level);
        }

        // Onda distribuiraj kroz network
        if (be.tickCounter % 5 == 2) {
            be.distributeEnergy(level);
        }

        // Na kraju push ka mašinama
        if (be.tickCounter % 5 == 4) {
            be.pushEnergyToMachines(level);
        }
    }

    /**
     * Pronalazi sve kablove povezane u mrežu
     */
    private void rebuildNetwork(Level level, BlockPos startPos) {
        networkMembers.clear();
        findConnectedCables(level, startPos);
    }

    private void findConnectedCables(Level level, BlockPos pos) {
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(pos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (networkMembers.contains(current)) continue;
            networkMembers.add(current);

            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = current.relative(dir);
                if (networkMembers.contains(neighborPos)) continue; // Optimization check
                BlockEntity be = level.getBlockEntity(neighborPos);
                if (be instanceof EnergyCableBlockEntity) {
                    queue.add(neighborPos);
                }
            }
        }
    }

    /**
     * Distribuira energiju ravnomjerno kroz cijelu mrežu
     */
    private void distributeEnergy(Level level) {
        if (networkMembers.isEmpty()) return;

        // Prvo skupi sve kablove u mreži
        List<EnergyCableBlockEntity> cables = new ArrayList<>();
        int totalEnergy = 0;
        int totalCapacity = 0;

        for (BlockPos pos : networkMembers) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EnergyCableBlockEntity cable) {
                cables.add(cable);
                totalEnergy += cable.energyStorage.getEnergyStored();
                totalCapacity += cable.energyStorage.getMaxEnergyStored();
            }
        }

        if (cables.isEmpty()) return;

        // Ravnomjerna distribucija
        int targetPerCable = totalEnergy / cables.size();
        int remainder = totalEnergy % cables.size();

        for (int i = 0; i < cables.size(); i++) {
            EnergyCableBlockEntity cable = cables.get(i);
            int targetEnergy = targetPerCable + (i < remainder ? 1 : 0);
            int currentEnergy = cable.energyStorage.getEnergyStored();
            int difference = targetEnergy - currentEnergy;

            if (difference > 0) {
                // Treba primiti energiju
                cable.energyStorage.receiveEnergy(difference, false);
            } else if (difference < 0) {
                // Treba odati energiju
                cable.energyStorage.extractEnergy(-difference, false);
            }
        }

        // Push energy ka mašinama
        pushEnergyToMachines(level);
    }

    /**
     * Gura energiju iz mreže prema mašinama (samo onima koje troše energiju)
     */
    private void pushEnergyToMachines(Level level) {
        if (networkMembers.isEmpty()) return;

        List<EnergyTransfer> transfers = new ArrayList<>();

        // Nađi sve potrošače koji TREBAJU energiju
        for (BlockPos cablePos : networkMembers) {
            BlockEntity cableBE = level.getBlockEntity(cablePos);
            if (!(cableBE instanceof EnergyCableBlockEntity cable)) continue;

            for (Direction dir : Direction.values()) {
                BlockPos machinePos = cablePos.relative(dir);

                // Preskoči druge kablove
                if (level.getBlockEntity(machinePos) instanceof EnergyCableBlockEntity) {
                    continue;
                }

                IEnergyStorage machineStorage = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK,
                        machinePos,
                        dir.getOpposite()
                );

                // OVO JE KLJUČNO: Šalji energiju SAMO ako je ovo POTROŠAČ koji TREBA energiju
                if (isEnergyConsumer(machineStorage)) {
                    int needed = machineStorage.getMaxEnergyStored() - machineStorage.getEnergyStored();
                    int canSend = Math.min(256, cable.energyStorage.getEnergyStored());
                    int toSend = Math.min(needed, canSend);

                    if (toSend > 0) {
                        transfers.add(new EnergyTransfer(cable, machineStorage, toSend));
                    }
                }
            }
        }

        // Izvrši transfere
        for (EnergyTransfer transfer : transfers) {
            if (transfer.source.energyStorage.getEnergyStored() <= 0) continue;

            int received = transfer.target.receiveEnergy(transfer.amount, false);
            if (received > 0) {
                transfer.source.energyStorage.extractEnergy(received, false);
            }
        }
    }
    /**
     * Provjerava je li storage POTROŠAČ koji TREBA energiju
     */
    private boolean isEnergyConsumer(IEnergyStorage storage) {
        if (storage == null) return false;

        // SAMO provjeri može li primiti energiju i treba li je
        return storage.canReceive() &&
                storage.getEnergyStored() < storage.getMaxEnergyStored();
    }
    /**
     * Pull energy SAMO iz generatora (blokova koji mogu davati energiju)
     */
    private void pullEnergyFromGenerators(Level level) {
        if (networkMembers.isEmpty()) return;

        // Prvo nađi sve kablove koji imaju prostor za energiju
        List<EnergyCableBlockEntity> cablesWithSpace = new ArrayList<>();
        for (BlockPos cablePos : networkMembers) {
            BlockEntity cableBE = level.getBlockEntity(cablePos);
            if (cableBE instanceof EnergyCableBlockEntity cable &&
                    cable.energyStorage.getEnergyStored() < cable.energyStorage.getMaxEnergyStored()) {
                cablesWithSpace.add(cable);
            }
        }

        if (cablesWithSpace.isEmpty()) return;

        // Nađi PRAVE generatore (ne potrošače)
        List<EnergySource> energySources = new ArrayList<>();

        for (BlockPos cablePos : networkMembers) {
            BlockEntity cableBE = level.getBlockEntity(cablePos);
            if (!(cableBE instanceof EnergyCableBlockEntity cable)) continue;


            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = cablePos.relative(dir);

                // Preskoči druge kablove
                if (level.getBlockEntity(neighborPos) instanceof EnergyCableBlockEntity) {
                    continue;
                }
                BlockEntity neighborBE = level.getBlockEntity(neighborPos);
                if (neighborBE instanceof BaseMachineBlockEntity) {
                    continue;
                }

                IEnergyStorage neighborStorage = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK,
                        neighborPos,
                        dir.getOpposite()
                );

                // OVO JE KLJUČNO: Provjeri je li ovo PRAVI generator
                if (isRealGenerator(neighborStorage)) {
                    int available = Math.min(256, neighborStorage.getEnergyStored());
                    if (available > 0) {
                        energySources.add(new EnergySource(neighborStorage, available));
                    }
                }
            }
        }

        // Podijeli energiju iz pravih generatora
        for (EnergySource source : energySources) {
            int energyPerCable = source.availableEnergy / cablesWithSpace.size();
            int remainder = source.availableEnergy % cablesWithSpace.size();

            for (int i = 0; i < cablesWithSpace.size(); i++) {
                EnergyCableBlockEntity targetCable = cablesWithSpace.get(i);
                int toReceive = energyPerCable + (i < remainder ? 1 : 0);

                if (toReceive > 0 && targetCable.energyStorage.getEnergyStored() < targetCable.energyStorage.getMaxEnergyStored()) {
                    int actuallyExtracted = source.storage.extractEnergy(toReceive, false);
                    if (actuallyExtracted > 0) {
                        targetCable.energyStorage.receiveEnergy(actuallyExtracted, false);
                    }
                }
            }
        }
    }
    /**
     * Provjerava je li storage PRAVI generator (ne potrošač)
     */
    private boolean isRealGenerator(IEnergyStorage storage) {
        if (storage == null) return false;

        // Generator = MOŽE davati energiju i IMA energiju
        return storage.canExtract() && storage.getEnergyStored() > 0;
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("energy", energyStorage.getEnergyStored());
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        this.energyStorage.setEnergy(tag.getInt("energy"));
        this.needsDistribution = true; // Force network rebuild nakon loadanja
    }

    public EnergyStorage getEnergyStorage() { return energyStorage; }

    // Metoda za vanjske pozive kada se network treba rebuildati
    public void markNetworkDirty() {
        this.needsDistribution = true;
    }

    /**
     * Obavještava susjedne kablove kada se ovaj kabel promijeni
     */
    public void notifyNeighbors(Level level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be instanceof EnergyCableBlockEntity cable) {
                cable.markNetworkDirty();
            }
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide) {
            notifyNeighbors(level, worldPosition);
        }
        super.setRemoved();
    }
}