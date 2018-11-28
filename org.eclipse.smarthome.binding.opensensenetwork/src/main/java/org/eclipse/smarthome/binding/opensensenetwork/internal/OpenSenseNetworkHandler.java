/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * The {@link OpenSenseNetworkHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * CONSOLE SETTINGS:
 * 1) Find the following file: smarthome-master/git/smarthome/distribution/smarthome/logback_debug.xml
 * 2) Modify the "level=" to your liking
 *
 * DEBUG -> All logs
 * INFO -> Info logs only
 * ERROR -> Error logs only
 * OFF -> No logs
 *
 *
 *
 * @author ISE - Initial contribution
 */
@NonNullByDefault
public class OpenSenseNetworkHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(OpenSenseNetworkHandler.class);
    private BigDecimal lt = new BigDecimal(200);// = 49.1259;
    private BigDecimal lg = new BigDecimal(200);// = 9.1428;
    private BigDecimal sensorID = new BigDecimal(-1);
    private final ArrayList<String> measurands_channels = new ArrayList<>();

    @Nullable
    private OpenSenseNetworkConfiguration config;

    public OpenSenseNetworkHandler(Thing thing) {
        super(thing);
    }

    @SuppressWarnings({ "deprecation", "null" })
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        // String bindingID = channelUID.getThingUID().getThingTypeUID().getBindingId();
        String channel_group = channelUID.getGroupId();
        /* ex. "temperature" */
        // String thingUID = channelUID.getThingUID().getId();
        /* ex. "2dd327f6" */
        String thingTypeID = channelUID.getThingUID().getThingTypeUID().getId();
        /* ex. "weather" or "environment" */

        if (command instanceof RefreshType) {

            if (thingTypeID.equals("weather")) {

                switch (channel_group) {
                    case "temperature":
                        System.out.println("First Group: " + channel_group);
                        getCurrentValue(channelUID);
                    case "humidity":
                        updateHumidity(channelUID);
                        System.out.println("Second Group: " + channel_group);
                    default:
                        System.out.println("Other Group: " + channel_group);
                }

            } else if (thingTypeID.equals("environment")) {
                /* do nothing yet */
            }

        }

    }

    /**
     * This method will later be modified to automatically handle any measurand and find the right unit
     *
     * @param channelUID
     */
    public void getCurrentValue(ChannelUID channelUID) {

        BigDecimal lt = this.lt;
        BigDecimal lg = this.lg;

        String refPoint = String.format("(%f,%f)", lt, lg);

        System.out.println("Getting Temperature for refPoint:" + refPoint);

        Unirest.get(OS_VALUE_URL).queryString("measurandId", TEMP_ID).queryString("refPoint", refPoint)
                .queryString("maxDistance", "5000").queryString("maxSensors", "5")
                .queryString("minTimestamp", "2018-11-17T20:00:00.0Z").asJsonAsync(new Callback<JsonNode>() {

                    @Override
                    public void completed(@Nullable HttpResponse<JsonNode> response) {

                        JsonNode body = response.getBody();
                        JSONArray arr = body.getArray();
                        JSONObject json = arr.getJSONObject(0);
                        JSONArray valarr = json.getJSONArray("values");
                        JSONObject val = valarr.getJSONObject(valarr.length() - 1);

                        OSTemperature temp = new OSTemperature(val.getString("timestamp"),
                                val.getDouble("numberValue"));

                        System.out.println("Temp:" + temp.temp());
                        System.out.println("Timestamp:" + temp.timestamp());

                        QuantityType<Temperature> current_temp = new QuantityType<Temperature>(temp.temp(),
                                SIUnits.CELSIUS);

                        try {
                            updateState(channelUID, current_temp);
                        } catch (Exception e) {
                            logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                                    e.getMessage());
                        }

                    }

                    @Override
                    public void failed(@Nullable UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    @Override
                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });

    }

    /**
     * Can be deleted once "getCurrentValue()" is complete
     *
     * @param channelUID
     */
    public void updateHumidity(ChannelUID channelUID) {

        BigDecimal lt = this.lt;
        BigDecimal lg = this.lg;

        String refPoint = String.format("(%f,%f)", lt, lg);

        System.out.println("Getting Humidity for refPoint:" + refPoint);

        Unirest.get(OS_VALUE_URL).queryString("measurandId", HUMI_ID).queryString("refPoint", refPoint)
                .queryString("maxDistance", "5000").queryString("maxSensors", "5")
                .queryString("minTimestamp", "2018-11-10T20:00:00.0Z").asJsonAsync(new Callback<JsonNode>() {

                    @Override
                    public void completed(@Nullable HttpResponse<JsonNode> response) {

                        JsonNode body = response.getBody();
                        JSONArray arr = body.getArray();
                        JSONObject json = arr.getJSONObject(0);
                        JSONArray valarr = json.getJSONArray("values");
                        JSONObject val = valarr.getJSONObject(valarr.length() - 1);

                        OSTemperature temp = new OSTemperature(val.getString("timestamp"),
                                val.getDouble("numberValue"));

                        System.out.println("Humi:" + temp.temp());
                        System.out.println("Timestamp:" + temp.timestamp());

                        QuantityType<Dimensionless> current_humidity = new QuantityType<Dimensionless>(temp.temp(),
                                SmartHomeUnits.PERCENT);

                        try {
                            updateState(channelUID, current_humidity);
                        } catch (Exception e) {
                            logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                                    e.getMessage());
                        }

                    }

                    @Override
                    public void failed(@Nullable UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    @Override
                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });

    }

    @Override
    public void initialize() {

        updateStatus(ThingStatus.UNKNOWN); // Set Thing Status to unknown

        System.out.println("Thing Type: " + thing.getThingTypeUID());

        if (thing.getThingTypeUID().equals(THING_TYPE_CONFIGURATION)) {
            OpenSenseNetworkConfiguration config = new OpenSenseNetworkConfiguration();
            boolean success = false;
            try {
                success = config.performConfiguration();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            if (success) {
                updateStatus(ThingStatus.ONLINE);

            }

            // TODO: Load the manipulated "thing-types.xml" file -> Currently only after hard restart
            // I tried "updateConfiguration(configuration);", but this only updates the configuration of the
            // Configuration Thing

        } else if (thing.getThingTypeUID().equals(THING_TYPE_WEATHER)) {
            // TODO: Download Weather Data and Store locally
            Configuration config = getThing().getConfiguration();
            lt = (BigDecimal) config.get("latitude");
            lg = (BigDecimal) config.get("longitude");
            sensorID = (BigDecimal) config.get("sensorID");

            Map<String, Object> map = config.getProperties();

            for (Entry<String, Object> parameter : map.entrySet()) {

                if (parameter.getValue().equals(true)) {
                    measurands_channels.add(parameter.getKey());
                }

            }
            updateStatus(ThingStatus.ONLINE);
        } else if (thing.getThingTypeUID().equals(THING_TYPE_ENVIRONMENT)) {
            // TODO: Download Environmental Data and Store locally
            updateStatus(ThingStatus.ONLINE);
        }

    }

    /* For future reference -> How to run something on another thread */

    // Thread humi_T = new Thread() {
    // @Override
    // public void run() {
    // }
    // };
    //
    // humi_T.start();
}