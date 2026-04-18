package com.grobe.techrebirth.client;

import com.grobe.techrebirth.network.EnergyDataPayload;

public class ClientPacketHandler {
    // Statičke varijable koje HUD čita
    public static long stored = 0, max = 0, gen = 0, spend = 0;

    public static void handleEnergyData(EnergyDataPayload data) {
        stored = data.stored();
        max = data.max();
        gen = data.gen();
        spend = data.spend();
    }

}
