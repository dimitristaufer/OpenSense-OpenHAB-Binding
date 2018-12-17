package org.eclipse.smarthome.binding.opensensenetwork.internal;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        // System.out.println(OHItem.getLinkForMeasurand("temperature"));
        // FakeLiveData();
        storeLocalReading("https://home.myopenhab.org:443/rest/items/Romanhome_Humidity", "1");
    }

    public static void FakeLiveData() {
        int startValue = 20;
        int step = 5;
        for (int i = 0; i < 6; i++) {
            int value = startValue + i * step;
            OSPostRequest.PostValueToOSSensor(40549, value);
        }
        for (int i = 5; i >= 0; i--) {
            int value = startValue + i * step;
            OSPostRequest.PostValueToOSSensor(40549, value);
        }
        System.out.println("OK!");
    }

    public static void storeLocalReading(String link, String sensorId) {
        try {
            HttpResponse<String> response = Unirest.get(link).header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Basic bWF0ZW85NkBvMi5wbDpTbWFydEhvbWU=").asString();
            System.out.println(response.getBody());
            /*
             * JsonNode body = response.getBody();
             * JSONArray arr = body.getArray();
             * String measurands = (String) (((JSONObject) arr.get(0)).get("name"));
             * JSONObject jObj = new JSONObject();
             * for (int i = 1; i < arr.length(); i++) {
             * jObj = (JSONObject) arr.get(i);
             * measurands += "#" + ((String) jObj.get("name"));
             * }
             * return measurands;
             */
        } catch (Exception ex) {
            // return ex.getMessage();
            System.out.println(ex.getMessage());
        }
        // TODO: 1) Take item.state and generate (correctly for Open Sense formatted) timestamp

        // TODO: 2) Generate some sort of JSON entry

        // TODO: 3) Store/Add to local JSON file using writeJSONtoFile()

        // TODO: 4) Write a separate method, which can read the local JSON file and format it accordingly for Open Sense

    }

}
