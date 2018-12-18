package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_SENSOR_URL;

import java.util.HashMap;
import java.util.Map;

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

    public synchronized static OSSensor getSensorForMeasurand(String measurand) {

        String sID = OSProperties.sensorId(measurand);
        if (sID.equals("0")) {
            System.out.println("Searching nearest sensor for " + measurand + ", because no local sensorId was found");
            return getClosest(OSProperties.lt(), OSProperties.lg(), measurand);
        } else {
            System.out.println("Found sensorId for " + measurand + ", now getting sensor locally");
            return getSensor(sID);
        }

    }

    public static OSSensor getClosest(String lt, String lg, String measurand) {

        String refPoint = String.format("(%s, %s)", lt, lg);

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_SENSOR_URL).queryString("measurandId", OSProperties.measurandId(measurand))
                    .queryString("refPoint", refPoint).queryString("maxDistance", OSProperties.maxDistance().toString())
                    .queryString("maxSensors", "1").queryString("minAccuracy", OSProperties.minAccuracy().toString())
                    .asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);

            System.out.println("Nearest Sensor here:" + json);

            // Store SensorID for future reference in preferences
            String sensorId = String.format("%d", json.optInt("id", -1));
            OSProperties.storeSensorID(measurand, sensorId);

            return makeSensor(json, sensorId);

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

    public static OSSensor getSensor(String sensorId) {

        return OSProperties.sensor(sensorId);

    }

    public static OSSensor getRemoteSensor(String sensorId) {

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_SENSOR_URL).queryString("id", sensorId).asJson();
            JSONObject json = response.getBody().getArray().getJSONObject(0);
            return makeSensor(json, sensorId); // Create Sensor based on server data

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

    public static OSSensor makeSensor(JSONObject json, String sensorId) {

        // System.out.println("Sensor Make");
        // System.out.println(json);

        JSONObject location = json.optJSONObject("location");

        int id = json.optInt("id", -1);
        int userId = json.optInt("userId", -1);
        int measurandId = json.optInt("measurandId", -1);
        int unitId = json.optInt("unitId", -1);
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

        OSSensor sens = new OSSensor(id, userId, measurandId, unitId, lt, lg, altitudeAboveGround, directionVertical,
                directionHorizontal, sensorModel, accuracy, attributionText, attributionURL, licenseId);

        OSProperties.storeSensor(sens, sensorId);

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
                "id#%d\nuserId#%d\nmeasurandId#%d\nunitId#%d\nlat#%f\nlng#%f\naltitudeAboveGround#%f\ndirectionVertical#%f\ndirectionHorizontal#%f\nsensorModel#%s\naccuracy#%d\nattributionText#%s\nattributionURL#%s\nlicenseId#%d",
                this.id, this.userId, this.measurandId, this.unitId, this.lt, this.lg, this.altitudeAboveGround,
                this.directionVertical, this.directionHorizontal, this.sensorModel, this.accuracy, this.attributionText,
                this.attributionURL, this.licenseId);
    }

    public static OSSensor fromString(String sensorString) {
        String sString = sensorString;
        String[] lines = sString.split(System.getProperty("line.separator"));
        Map<String, String> map = new HashMap<String, String>();
        for (String line : lines) {
            String[] key_value = line.split("#");
            map.put(key_value[0], key_value[1]);
        }

        int id = Integer.parseInt(map.get("id"));
        int userId = Integer.parseInt(map.get("userId"));
        int measurandId = Integer.parseInt(map.get("measurandId"));
        int unitID = Integer.parseInt(map.get("unitId"));
        double lt = Double.parseDouble(map.get("lat"));
        double lg = Double.parseDouble(map.get("lng"));
        double altitudeAboveGround = Double.parseDouble(map.get("altitudeAboveGround"));
        double directionVertical = Double.parseDouble(map.get("directionVertical"));
        double directionHorizontal = Double.parseDouble(map.get("directionHorizontal"));
        String sensorModel = map.get("sensorModel");
        int accuracy = Integer.parseInt(map.get("accuracy"));
        String attributionText = map.get("attributionText");
        String attributionURL = map.get("attributionURL");
        int licenseId = Integer.parseInt(map.get("licenseId"));

        OSSensor sens = new OSSensor(id, userId, measurandId, unitID, lt, lg, altitudeAboveGround, directionVertical,
                directionHorizontal, sensorModel, accuracy, attributionText, attributionURL, licenseId);

        return sens;

    }

    public static String getMeasurandNameFromSensor(String sensorID) {
        OSSensor sensor = getOSSensorById(sensorID);
        int measurandID = -1;
        String measurandName = "";
        if (sensor != null) {
            measurandID = sensor.measurandId();
        }
        if (measurandID != -1) {
            HttpResponse<String> response;
            String link = String.format("%s/%s", OpenSenseNetworkBindingConstants.OS_MEASURANDS_URL, measurandID);
            try {
                response = Unirest.get(link).asString();
                JSONObject json = new JSONObject(response.getBody());
                measurandName = json.get("name").toString();

            } catch (UnirestException error) {
                error.printStackTrace();
            }
        }
        return measurandName;
    }

    public static OSSensor getOSSensorById(String sensorId) {
        HttpResponse<String> response;
        try {
            String link = String.format("%s/%s", OS_SENSOR_URL, sensorId);
            response = Unirest.get(link).asString();
            JSONObject json = new JSONObject(response.getBody());
            int id = json.getInt("id");
            int userId = json.getInt("userId");
            int measurandId = json.getInt("measurandId");
            int unitId = json.getInt("unitId");
            JSONObject location = (JSONObject) json.get("location");
            double lt = location.getDouble("lat");
            double lg = location.getDouble("lng");
            double altitudeAboveGround = json.getDouble("altitudeAboveGround");
            double directionVertical = json.getDouble("directionVertical");
            double directionHorizontal = json.getDouble("directionHorizontal");
            String sensorModel = json.getString("sensorModel");
            int accuracy = json.getInt("accuracy");
            String attributionText = json.getString("attributionText");
            String attributionURL = json.getString("attributionURL");
            int licenseId = json.getInt("licenseId");
            return new OSSensor(id, userId, measurandId, unitId, lt, lg, altitudeAboveGround, directionVertical,
                    directionHorizontal, sensorModel, accuracy, attributionText, attributionURL, licenseId);

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

}
