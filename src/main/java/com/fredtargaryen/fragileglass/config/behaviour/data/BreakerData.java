package com.fredtargaryen.fragileglass.config.behaviour.data;

public class BreakerData {
    private double minSpeedSquared;
    private double maxSpeedSquared;
    private String[] extraData;

    public BreakerData(double minSpeedSquared, double maxSpeedSquared, String[] extraData) {
        this.minSpeedSquared = minSpeedSquared;
        this.maxSpeedSquared = maxSpeedSquared;
        this.extraData = extraData;
    }

    public double getMinSpeedSquared() { return this.minSpeedSquared; }

    public double getMaxSpeedSquared() { return this.maxSpeedSquared; }

    public String[] getExtraData() { return this.extraData; }
}
