package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_MEASURANDS_URL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

/**
 * @author Dimitri Jan Staufer
 * @author Mateusz Kedzierski
 * @author Maksym Koliesnikov
 * @author Manisha Nagbanshi
 * @author Roman Zabrovarny
 *
 *         OSContribute manages and schedules the collection of local sensor data from openHAB channels
 *         It also formats JSON files which are passed on to OSPostRequest for POSTing to opensense.network
 *
 */

public class OSContribute {

    static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void startPostSchedule(String sensorId, int delay) {

        int delaySeconds = delay * 60;

        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {

                    File file = new File(OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource()
                            .getLocation().getPath());
                    String filePath = file.getAbsolutePath().concat("/ESH-INF/binding/").concat(sensorId)
                            .concat(".json");
                    JSONObject localReadings = getJSONfromFile(filePath);

                    if (OSPostRequest.postMultipleValues(localReadings)) {
                        // Delete local readings
                        deleteJSONfromFile(filePath);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delaySeconds, delaySeconds, TimeUnit.SECONDS); // Start after delaySeconds, every delaySeconds

    }

    public static void terminatePostSchedule(String sensorId) {
        scheduler.shutdown();
    }

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
            /* Local File Handling */
            File file = new File(
                    OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String filePath = file.getAbsolutePath().concat("/ESH-INF/binding/").concat(sensorId).concat(".json");
            JSONObject objToWrite = new JSONObject();

            /* New JSONObject to add to JSONArray */
            JSONObject currentReading = item.getAsReadyToStore(sensorId);
            System.out.println("currentRead: " + currentReading);
            /* Handle already existing local readings */
            JSONObject localReadings = getJSONfromFile(filePath);
            System.out.println(localReadings);
            if (localReadings == null) { // no readings exist
                JSONArray collapsedMessages = new JSONArray();
                collapsedMessages.put(currentReading);

                objToWrite.put("collapsedMessages", collapsedMessages);
                writeJSONtoFile(objToWrite, filePath);
            } else { // readings exist
                JSONArray collapsedMessages = localReadings.getJSONArray("collapsedMessages");
                collapsedMessages.put(currentReading);

                objToWrite.put("collapsedMessages", collapsedMessages);
                writeJSONtoFile(objToWrite, filePath);
            }

        } catch (Exception ex) {
            System.out.println("storeLocalReading ERROR");
        }

    }

    public static JSONObject getLatestStoredLocalValue(String sensorId) throws JSONException, IOException {

        File file = new File(
                OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String filePath = file.getAbsolutePath().concat("/ESH-INF/binding/").concat(sensorId).concat(".json");
        JSONObject localReadings = getJSONfromFile(filePath);

        if (localReadings == null) {
            return null;
        } else {
            JSONArray allReadings = localReadings.getJSONArray("collapsedMessages");
            JSONObject lastReading = allReadings.getJSONObject(allReadings.length() - 1);
            return lastReading;
        }

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

    public static JSONObject getJSONfromFile(String filepath) throws JSONException, IOException {

        File file = new File(filepath);
        if (file.exists() && !file.isDirectory()) {
            String content = new String(Files.readAllBytes(Paths.get(filepath)));
            if (content.isEmpty() || content == "") {
                return null;
            } else {
                JSONObject jObj = new ObjectMapper().readValue(content, JSONObject.class);
                return jObj;
            }
        } else {
            return null;
        }

    }

    public static void deleteJSONfromFile(String filepath) throws JSONException, IOException {
        File file = new File(filepath);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

}