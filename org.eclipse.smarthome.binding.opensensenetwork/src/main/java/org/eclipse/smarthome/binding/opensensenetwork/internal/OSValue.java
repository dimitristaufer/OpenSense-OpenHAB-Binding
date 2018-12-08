package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.text.DecimalFormat;

import org.json.JSONObject;

public class OSValue {

    private double numberValue;
    private String observationTime;

    private int id;
    private int userId;
    private int measurandId;
    private int unitId;
    private String location;
    private double altitudeAboveGround;
    private double directionVertical;
    private double directionHorizontal;
    private String sensorModel;
    private int accuracy;
    private String attributionText;
    private String attributionURL;
    private int licenseId;

    public OSValue() {

    }

    public OSValue(String observationTime, double numberValue) {
        this.observationTime = observationTime;
        this.numberValue = numberValue;
    }

    public OSValue(String observationTime, double numberValue, int id, int userId, int measurandId, int unitId,
            String location, double altitudeAboveGround, double directionVertical, double directionHorizontal,
            String sensorModel, int accuracy, String attributionText, String attributionURL, int licenseId) {
        this.numberValue = numberValue;
        this.observationTime = observationTime;
        this.id = id;
        this.userId = userId;
        this.measurandId = measurandId;
        this.unitId = unitId;
        this.location = location;
        this.altitudeAboveGround = altitudeAboveGround;
        this.directionVertical = directionVertical;
        this.directionHorizontal = directionHorizontal;
        this.sensorModel = sensorModel;
        this.accuracy = accuracy;
        this.attributionText = attributionText;
        this.attributionURL = attributionURL;
        this.licenseId = licenseId;

    }

    public static OSValue makeValue(JSONObject json) {

        // System.out.println("Value Make");
        // System.out.println(json);

        JSONObject values = json.getJSONArray("values").getJSONObject(0);
        JSONObject loc = json.optJSONObject("location");

        String observationTime = values.optString("timestamp", "-1");
        double numberValue = values.optDouble("numberValue", -1.0);

        int id = json.optInt("id", -1);
        int userId = json.optInt("userId", -1);
        int measurandId = json.optInt("measurandId", -1);
        int unitId = json.optInt("unitId", -1);
        double lt = loc.optDouble("lat", 49.1259);
        double lg = loc.optDouble("lng", 9.1428);
        String location = String.format("%f°N %f°E", lt, lg);
        double altitudeAboveGround = json.optDouble("altitudeAboveGround", -1.0);
        double directionVertical = json.optDouble("directionVertical", -1.0);
        double directionHorizontal = json.optDouble("directionHorizontal", -1.0);
        String sensorModel = json.optString("sensorModel", "");
        int accuracy = json.optInt("accuracy", -1);
        String attributionText = json.optString("attributionText", "");
        String attributionURL = json.optString("attributionURL", "");
        int licenseId = json.optInt("licenseId", -1);

        OSValue osVal = new OSValue(observationTime, numberValue, id, userId, measurandId, unitId, location,
                altitudeAboveGround, directionVertical, directionHorizontal, sensorModel, accuracy, attributionText,
                attributionURL, licenseId);

        return osVal;
    }

    public double numberValue() {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(numberValue));
    }

    public String observationTime() {
        return this.observationTime;
    }

    public int id() {
        return this.id;
    }

    public int userId() {
        return this.userId;
    }

    public int measurandId() {
        return this.measurandId;
    }

    public int unitId() {
        return this.unitId;
    }

    public String location() {
        return this.location;
    }

    public double altitudeAboveGround() {
        return this.altitudeAboveGround;
    }

    public double directionVertical() {
        return this.directionVertical;
    }

    public double directionHorizontal() {
        return this.directionHorizontal;
    }

    public String sensorModel() {
        return this.sensorModel;
    }

    public int accuracy() {
        return this.accuracy;
    }

    public String attributionText() {
        return this.attributionText;
    }

    public String attributionURL() {
        return this.attributionURL;
    }

    public int licenseId() {
        return this.licenseId;
    }

    @Override
    public String toString() {
        return String.format(
                "numberValue#%f\nobservationTime#%s\nid#%d\nuserId#%d\nmeasurandId#%d\nunitId#%d\nlocation#%s\naltitudeAboveGround#%f\ndirectionVertical#%f\ndirectionHorizontal#%f\nsensorModel#%s\naccuracy#%d\nattributionText#%s\nattributionURL#%s\nlicenseId#%d",
                this.numberValue, this.observationTime, this.id, this.userId, this.measurandId, this.unitId,
                this.location, this.altitudeAboveGround, this.directionVertical, this.directionHorizontal,
                this.sensorModel, this.accuracy, this.attributionText, this.attributionURL, this.licenseId);
    }

}
