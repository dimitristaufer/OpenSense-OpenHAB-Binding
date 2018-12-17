package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.*;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OSProperties {

    private static Preferences prefs;

    /* TODO: Add timeout for values */

    public static String validateAndRemoveTimestamp(String value) {

        // FORMAT: VALUE#TIMESTAMP

        return null;
    }

    private void removeValue(String key) {
        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.remove(key);
    }

    public static void removeAllValues() {

        prefs = Preferences.userRoot().node(BINDING_ID);
        try {
            prefs.clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    public static void removeAllSensors() {

        // Preserve user values
        String lt = lt();
        String lg = lg();
        int maxDistance = maxDistance();
        int minAccuracy = minAccuracy();

        prefs = Preferences.userRoot().node(BINDING_ID);
        try {
            prefs.clear();

            prefs.put("lt", lt);
            prefs.put("lg", lg);
            prefs.putInt("maxDistance", maxDistance);
            prefs.putInt("minAccuracy", minAccuracy);

        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    public static String sensorId(String measurand) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("%s_%s", "sensorId", measurand); // ex. "sensorId_temperature"
        String value = prefs.get(key, "0");
        if (value.equals("-1")) {
            return "0";
        }
        // System.out.println("Used Key:" + key);
        // System.out.println("Loaded Value:" + value);
        return value.toString();

    }

    public static OSSensor sensor(String sensorId) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("%s_%s", "sensor", sensorId); // ex. "sensor_temperature"
        String value = prefs.get(key, "0");

        // System.out.println("Used Key:" + key);
        // System.out.println("Loaded Value:" + value);
        return OSSensor.fromString(value);

    }

    public static String lt() {

        prefs = Preferences.userRoot().node(BINDING_ID);
        String lt = prefs.get("lt", "52.5167");

        if (lt == "auto") {
            // TODO: Get lat based on IP
            return String.format("%f", 52.5167); // Berlin
        } else {
            return lt;
        }

    }

    public static String lg() {

        prefs = Preferences.userRoot().node(BINDING_ID);
        String lg = prefs.get("lg", "13.4000");

        if (lg == "auto") {
            // TODO: Get long based on IP
            return String.format("%f", 13.4000); // Berlin
        } else {
            return lg;
        }

    }

    public static String OpenHABLink(String measurand) {
        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("ContributeLink_%s", measurand);
        String link = prefs.get(key, "0");
        return link;
    }

    public static String Username() {
        prefs = Preferences.userRoot().node(BINDING_ID);
        String username = prefs.get("username", "0");
        return username;
    }

    public static String Password() {
        prefs = Preferences.userRoot().node(BINDING_ID);
        String password = prefs.get("password", "0");
        return password;
    }

    public static long ContributeSensorID(String measurand) {
        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("%s_%s_contribute", "sensorId", measurand);
        long id = prefs.getLong(key, -1);
        return id;
    }

    public static int ContributePollingInterval(String measurand) {
        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("ContributePollingInterval_%s", measurand);
        int interval = prefs.getInt(key, 10);
        return interval;
    }

    public static Integer maxDistance() {

        prefs = Preferences.userRoot().node(BINDING_ID);
        return prefs.getInt("maxDistance", 30000);
    }

    public static Integer minAccuracy() {

        prefs = Preferences.userRoot().node(BINDING_ID);
        return prefs.getInt("minAccuracy", 1);
    }

    public static Integer measurandId(String measurand) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        String measurandId = prefs.get(String.format("%s_%s)", "measurandId", measurand), "null");
        if (measurandId == "null") {
            return (remoteMeasurandId(measurand));
        }
        return Integer.parseInt(measurandId);

    }

    private static Integer remoteMeasurandId(String measurand) {

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_MEASURANDS_URL).queryString("name", measurand).asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);
            String measurandId = String.format("%d", json.optInt("id", -1));

            storeMeasurandID(measurand, measurandId);

            return json.optInt("id", -1);

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

    public static void storeMeasurandID(String measurand, String ID) {

        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            String key = String.format("%s_%s", "measurandId", measurand); // ex. "measurandId_temperature"
            prefs.put(key, ID);
        }

    }

    public static void storeSensorID(String measurand, String sensorID) {

        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            String key = String.format("%s_%s", "sensorId", measurand); // ex. "sensorId_temperature"
            prefs.put(key, sensorID);
        }

    }

    public static void storeSensor(OSSensor sensor, String sensorID) {

        if (!SERVER_ONLY_MODE) {
            System.out.println("storing sensor");
            prefs = Preferences.userRoot().node(BINDING_ID);
            String key = String.format("%s_%s", "sensor", sensorID); // ex. "sensor_temperature"
            prefs.put(key, sensor.toString());
        }

    }

    public static void storeLt(String lt) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.put("lt", lt);

    }

    public static void storeLg(String lg) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.put("lg", lg);

    }

    public static void storeMaxDistance(String distance) {

        int d = Integer.parseInt(distance);
        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.putInt("maxDistance", d);

    }

    public static void storeMinAccuracy(String accuracy) {

        int a = Integer.parseInt(accuracy);
        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.putInt("minAccuracy", a);

    }

    public static void storeUsername(String username) {
        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            prefs.put("username", username);
        }
    }

    public static void storePassword(String password) {
        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            prefs.put("password", password);
        }
    }

    public static void storeContributeSensorID(String id, String measurand) {
        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            String key = String.format("%s_%s_contribute", "sensorId", measurand); // ex. "sensorId_temperature"
            prefs.put(key, id);
        }
    }

    public static void storeContributePollingInterval(String interval, String measurand) {
        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            String key = String.format("ContributePollingInterval_%s", measurand);
            prefs.put(key, interval);
        }
    }

    public static void storeOpenHABLink(String link, String measurand) {
        if (!SERVER_ONLY_MODE) {
            prefs = Preferences.userRoot().node(BINDING_ID);
            String key = String.format("ContributeLink_%s", measurand);
            prefs.put(key, link);
        }
    }

}
