package org.eclipse.smarthome.binding.opensensenetwork.internal;

public class OSTemperature {

    private String timestamp;
    private double numberValue;

    public OSTemperature() {
        // Empty Init
    }

    public OSTemperature(String timestamp, double numberValue) {
        this.timestamp = timestamp;
        this.numberValue = numberValue;
    }

    public double temp() {
        return this.numberValue;
    }

    public String timestamp() {
        return this.timestamp;
    }

}
