package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_SENSOR_URL;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OSSensor {

    private final int id;
    private final int userId;
    private final int measurandId;
    private final int unitId;
    private final double lt;
    private final double lg;
    private final double altitudeAboveGround;
    private final double directionVertical;
    private final double directionHorizontal;
    private final String sensorModel;
    private final int accuracy;
    private final String attributionText;
    private final String attributionURL;
    private final int licenseId;

    public OSSensor(int id, int userId, int measurandId, int unitId, double lt, double lg, double altitudeAboveGround,
            double directionVertical, double directionHorizontal, String sensorModel, int accuracy,
            String attributionText, String attributionURL, int licenseId) {

        this.id = id;
        this.userId = userId;
        this.measurandId = measurandId;
        this.unitId = unitId;
        this.lt = lt;
        this.lg = lg;
        this.altitudeAboveGround = altitudeAboveGround;
        this.directionVertical = directionVertical;
        this.directionHorizontal = directionHorizontal;
        this.sensorModel = sensorModel;
        this.accuracy = accuracy;
        this.attributionText = attributionText;
        this.attributionURL = attributionURL;
        this.licenseId = licenseId;

    }

    public static OSSensor getSensorForMeasurand(String measurand) {

        String sID = OSProperties.sensorID(measurand);
        if (sID == null) {
            return getClosest(OSProperties.lt(), OSProperties.lg(), measurand);
        } else {
            return getSensor(sID);
        }

    }

    public static OSSensor getClosest(String lt, String lg, String measurand) {

        String refPoint = String.format("(%s, %s)", lt, lg);

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_SENSOR_URL).queryString("measurandId", OSProperties.measurandId(measurand))
                    .queryString("refPoint", refPoint).queryString("maxDistance", "5000").queryString("maxSensors", "1")
                    .asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);

            // Store SensorID for future reference in PLIST
            OSProperties.storeSensorID(measurand, String.format("%d", json.optInt("id", -1)));

            return makeSensor(json);

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

    public static OSSensor getSensor(String sensorID) {

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_SENSOR_URL).queryString("id", sensorID).asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);

            return makeSensor(json);

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

    public static OSSensor makeSensor(JSONObject json) {

        // System.out.println("Sensor Make");
        // System.out.println(json);

        JSONObject location = json.optJSONObject("location");

        int id = json.optInt("id", -1);
        int userId = json.optInt("userId", -1);
        int measurandId = json.optInt("measurandId", -1);
        int unitID = json.optInt("unitId", -1);
        double lt = location.optDouble("lat", 49.1259);
        double lg = location.optDouble("lng", 9.1428);
        double altitudeAboveGround = json.optDouble("altitudeAboveGround", -1.0);
        double directionVertical = json.optDouble("directionVertical", -1.0);
        double directionHorizontal = json.optDouble("directionHorizontal", -1.0);
        String sensorModel = json.optString("sensorModel", "");
        int accuracy = json.optInt("accuracy", -1);
        String attributionText = json.optString("attributionText", "");
        String attributionURL = json.optString("attributionURL", "");
        int licenseId = json.optInt("licenseId", -1);

        OSSensor sens = new OSSensor(id, userId, measurandId, unitID, lt, lg, altitudeAboveGround, directionVertical,
                directionHorizontal, sensorModel, accuracy, attributionText, attributionURL, licenseId);

        return sens;
    }

    public String id() {
        return String.format("%d", this.id);
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

    public double lt() {
        return this.lt;
    }

    public double lg() {
        return this.lg;
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
                "Sensor: \n id:%d \n userId:%d \n measurandId:%d \n unitId:%d \n lt:%f \n lg:%f \n altitudeAboveGround:%f \n directionVertical:%f \n directionHorizontal:%f \n sensorModel:%s \n accuracy:%d \n String:%s \n String:%s \n licenseId:%d",
                this.id, this.userId, this.measurandId, this.unitId, this.lt, this.lg, this.altitudeAboveGround,
                this.directionVertical, this.directionHorizontal, this.sensorModel, this.accuracy, this.attributionText,
                this.attributionURL, this.licenseId);
    }

}
