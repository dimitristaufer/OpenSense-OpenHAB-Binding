package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_MEASURANDS_URL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
                measurands.add(measurand.toLowerCase());
            }
        }
        return measurands;
    }

    public static void storeLocalReading(OHItem item, String sensorId) {
        try {
            HttpResponse<String> response = Unirest.get(item.getLink()).header("Accept", "application/json")
                    .header("Authorization", "Basic bWF0ZW85NkBvMi5wbDpTbWFydEhvbWU=").asString();

            JSONObject responseJson = new JSONObject(response.getBody());
            OHValue ohValue = new OHValue(responseJson);
            JSONObject json = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(ohValue.getAsReadyToStore(sensorId));
            json.append("collapsedMessages", jsonArray);
            File file = new File(
                    OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String filePath = file.getAbsolutePath().concat("/ESH-INF/binding/").concat(item.getLink());
            writeJSONtoFile(json, filePath);
        } catch (Exception ex) {
            // return ex.getMessage();
        }
        // TODO: 1) Take item.state and generate (correctly for Open Sense formatted) timestamp

        // TODO: 2) Generate some sort of JSON entry

        // TODO: 3) Store/Add to local JSON file using writeJSONtoFile()

        // TODO: 4) Write a separate method, which can read the local JSON file and format it accordingly for Open Sense

    }

    private static void writeJSONtoFile(JSONObject jObj, String filepath) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(filepath), jObj.toString());
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}