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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Math.sqrt(this.minSpeedSquared));
        sb.append(" ");
        sb.append(Math.sqrt(this.maxSpeedSquared));
        for(String s : this.extraData) {
            sb.append(" ");
            sb.append(s);
        }
        return sb.toString();
    }

    public double getMinSpeedSquared() { return this.minSpeedSquared; }

    public double getMaxSpeedSquared() { return this.maxSpeedSquared; }

    public String[] getExtraData() { return this.extraData; }
}
