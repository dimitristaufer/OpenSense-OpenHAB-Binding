package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OSPostRequest {

    private int measurandId;
    private final String sensorModel = null;
    private int licenseId;
    private int userId;

    public static void main(String[] args) throws IOException {
        PostRequest(40549);

    }

    public OSPostRequest(int measurandId, int licenseId, int userId) {
        super();
        this.measurandId = measurandId;
        this.licenseId = licenseId;
        this.userId = userId;
    }

    public static String getAPIKey() {

        try {
            JSONObject json = new JSONObject();
            JSONArray arr = new JSONArray();
            json.put("username", "smarthome");
            json.put("password", "8KO9koE+");
            arr.put(json);

            HttpResponse<JsonNode> jsonResponse = Unirest
                    .post("https://www.opensense.network/progprak/beta/api/v1.0/users/login")
                    .header("accept", "application/json").header("Content-Type", "application/json").body(json)
                    .asJson();
            return jsonResponse.getBody().getObject().get("id").toString();
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Didnt work ohhhhhhhhhhh";
        }

    }

    public static String PostRequest(long id) {

        try {
            JSONObject json = new JSONObject();
            JSONArray jArr = new JSONArray();
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            json.put("sensorId", id);
            json.put("timestamp", timestamp);
            json.put("numberValue", 10);
            jArr.put(json);
            HttpResponse<String> request = Unirest
                    .post("https://www.opensense.network/progprak/beta/api/v1.0/sensors/addValue")
                    .header("Content-Type", "application/json").header("Accept", "application/json")
                    .header("Authorization", getAPIKey()).body(json).asString();
            System.out.println(request.getStatusText());
            if (request.getStatus() == 200) {
                System.out.println("Sucessfully posted data");

            }
            return json.toString();

        } catch (UnirestException | JSONException je) {
            System.out.println(je.getMessage());
            return "Failed to post.";
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
