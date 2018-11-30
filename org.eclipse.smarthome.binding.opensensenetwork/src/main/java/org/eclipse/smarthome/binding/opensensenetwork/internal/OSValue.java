package org.eclipse.smarthome.binding.opensensenetwork.internal;

public class OSValue {

    private String timestamp;
    private double numberValue;

    public OSValue() {
        // Empty Init
    }

    public OSValue(String timestamp, double numberValue) {
        this.timestamp = timestamp;
        this.numberValue = numberValue;
    }

    public double numberValue() {
        return this.numberValue;
    }

    public String timestamp() {
        return this.timestamp;
    }

}
