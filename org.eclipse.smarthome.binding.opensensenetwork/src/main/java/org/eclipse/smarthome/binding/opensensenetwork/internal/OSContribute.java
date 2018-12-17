package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_MEASURANDS_URL;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class OSContribute {
    public static String getMeasurandsFromOpenSense() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(OS_MEASURANDS_URL).asJson();

            JsonNode body = response.getBody();
            JSONArray arr = body.getArray();
            String measurands = (String) (((JSONObject) arr.get(0)).get("name"));
            JSONObject jObj = new JSONObject();
            for (int i = 1; i < arr.length(); i++) {
                jObj = (JSONObject) arr.get(i);
                measurands += "#" + ((String) jObj.get("name"));
            }
            return measurands;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public static ArrayList<String> getMeasurandsToContribute(ArrayList<String> measurandsFromOpenHab,
            String measurandsFromOS) {
        ArrayList<String> measurands = new ArrayList<>();
        for (String measurand : measurandsFromOpenHab) {
            if (!measurands.contains(measurand) && measurandsFromOS.toLowerCase().contains(measurand.toLowerCase())) {
                measurands.add(measurand);
            }
        }
        return measurands;
    }

    public static void storeLocalReading() {

    }

}