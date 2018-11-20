package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

public class OSSensor {

    private int id;
    private int userId;
    private int measurandId;
    private Map<String, Double> location;
    private double altitudeAboveGround;
    private double directionVertical;
    private double directionHorizontal;
    private String sensorModel;
    private int accuracy;
    private String attributionText;
    private String attributionURL;
    private int licenseId;

    public OSSensor(int id, int userId, int measurandId, Map<String, Double> location, double altitudeAboveGround,
            double directionVertical, double directionHorizontal, String sensorModel, int accuracy,
            String attributionText, String attributionURL, int licenseId) {

        this.id = id;
        this.userId = userId;
        this.measurandId = measurandId;
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

    public OSSensor() {
        // Empty Init
    }

    public void initSensor(JSONObject json) {

        int id = Optional.ofNullable(json.getInt("id")).orElse(0);
        int userId = Optional.ofNullable(json.getInt("userId")).orElse(0);
        int measurandId = Optional.ofNullable(0).orElse(0);
        Map<String, Double> location = new HashMap<String, Double>();
        location = Optional.ofNullable(location).orElse(location);
        double altitudeAboveGround = Optional.ofNullable(0).orElse(0);
        double directionVertical = Optional.ofNullable(0).orElse(0);
        double directionHorizontal = Optional.ofNullable(0).orElse(0);
        String sensorModel = Optional.ofNullable("").orElse("");
        int accuracy = Optional.ofNullable(0).orElse(0);
        String attributionText = Optional.ofNullable("").orElse("");
        String attributionURL = Optional.ofNullable(json.getString("attributionURL")).orElse("");
        int licenseId = Optional.ofNullable(0).orElse(0);

        this.id = id;
        this.userId = userId;
        this.measurandId = measurandId;
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

    public int id() {
        return this.id;
    }

    public int userId() {
        return this.userId;
    }

    public int measurandId() {
        return this.measurandId;
    }

    public Map<String, Double> location() {
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

}
