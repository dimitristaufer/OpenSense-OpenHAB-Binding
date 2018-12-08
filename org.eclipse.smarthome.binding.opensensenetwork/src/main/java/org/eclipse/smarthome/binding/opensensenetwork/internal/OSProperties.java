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

        // Preserve lat and long
        String lt = lt();
        String lg = lg();

        prefs = Preferences.userRoot().node(BINDING_ID);
        try {
            prefs.clear();

            prefs.put("lt", lt);
            prefs.put("lg", lg);

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

        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("%s_%s", "measurandId", measurand); // ex. "measurandId_temperature"
        prefs.put(key, ID);

    }

    public static void storeSensorID(String measurand, String sensorID) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("%s_%s", "sensorId", measurand); // ex. "sensorId_temperature"
        prefs.put(key, sensorID);

    }

    public static void storeSensor(OSSensor sensor, String sensorID) {

        System.out.println("storing sensor");
        prefs = Preferences.userRoot().node(BINDING_ID);
        String key = String.format("%s_%s", "sensor", sensorID); // ex. "sensor_temperature"
        System.out.println("Key: " + key);
        System.out.println("Value: " + sensor.toString());
        prefs.put(key, sensor.toString());

    }

    public static void storeLt(String lt) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.put("lt", lt);

    }

    public static void storeLg(String lg) {

        prefs = Preferences.userRoot().node(BINDING_ID);
        prefs.put("lg", lg);

    }

}
