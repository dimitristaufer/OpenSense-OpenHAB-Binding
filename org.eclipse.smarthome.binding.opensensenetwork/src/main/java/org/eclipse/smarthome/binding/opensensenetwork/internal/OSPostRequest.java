package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.*;

import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author Dimitri Jan Staufer
 * @author Mateusz Kedzierski
 * @author Maksym Koliesnikov
 * @author Manisha Nagbanshi
 * @author Roman Zabrovarny
 *
 *         This class handles post requests to opensense.network.
 *
 */

public class OSPostRequest {

    private int measurandId;
    private final String sensorModel = null;
    private int licenseId;
    private int userId;

    public OSPostRequest(int measurandId, int licenseId, int userId) {
        super();
        this.measurandId = measurandId;
        this.licenseId = licenseId;
        this.userId = userId;
    }

    public static String getAPIKey() {

        String un = OSProperties.username();
        String pw = OSProperties.password();

        try {
            JSONObject json = new JSONObject();
            JSONArray arr = new JSONArray();
            json.put("username", un); // smarthome
            json.put("password", pw); // 8KO9koE+
            arr.put(json);

            HttpResponse<JsonNode> jsonResponse = Unirest.post(OS_POST_LOGIN).header("accept", "application/json")
                    .header("Content-Type", "application/json").body(json).asJson();
            return jsonResponse.getBody().getObject().get("id").toString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static boolean postValue(int sensorId, double value) {

        try {
            JSONObject json = new JSONObject();
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            json.put("sensorId", sensorId);
            json.put("timestamp", timestamp);
            json.put("numberValue", value);
            HttpResponse<String> request = Unirest.post(OS_POST_ADDVALUE).header("Content-Type", "application/json")
                    .header("Accept", "application/json").header("Authorization", getAPIKey()).body(json).asString();
            System.out.println(request.getStatusText());
            if (request.getStatus() == 200) {
                System.out.println("Sucessfully posted value");
            }
            return true;

        } catch (UnirestException | JSONException je) {
            System.out.println(je.getMessage());
            return false;
        }
    }

    public static boolean postMultipleValues(JSONObject jObj) {

        try {
            HttpResponse<String> request = Unirest.post(OS_POST_ADDMULTIPLEVALUES)
                    .header("Content-Type", "application/json").header("Accept", "application/json")
                    .header("Authorization", getAPIKey()).body(jObj).asString();
            System.out.println(request.getStatusText());
            if (request.getStatus() == 200) {
                System.out.println("Sucessfully posted values");
            }
            return true;

        } catch (UnirestException | JSONException je) {
            System.out.println(je.getMessage());
            return false;
        }
    }

    public int getMeasurandId() {
        return measurandId;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public void setMeasurandId(int measurandId) {
        this.measurandId = measurandId;
    }

    public int getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(int licenseId) {
        this.licenseId = licenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
