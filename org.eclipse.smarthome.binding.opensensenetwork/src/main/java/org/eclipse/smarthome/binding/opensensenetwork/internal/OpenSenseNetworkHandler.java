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
 * @author ISE - Initial contribution
 */
@NonNullByDefault
public class OpenSenseNetworkHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(OpenSenseNetworkHandler.class);
    private BigDecimal lt = new BigDecimal(200);// = 49.1259;
    private BigDecimal lg = new BigDecimal(200);// = 9.1428;
    private BigDecimal sensorID = new BigDecimal(-1);

    @Nullable
    private OpenSenseNetworkConfiguration config;

    public OpenSenseNetworkHandler(Thing thing) {
        super(thing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        String bindingID = channelUID.getThingUID().getThingTypeUID().getBindingId();
        /* equals "opensensenetwork" */
        String channel_group = channelUID.getGroupId();
        /* ex. "temperature" */
        String thingUID = channelUID.getThingUID().getId();
        /* ex. "2dd327f6" */
        String thingTypeID = channelUID.getThingUID().getThingTypeUID().getId();
        /* ex. "weather" or "environment" */

        if (command instanceof RefreshType) {

            if (thingTypeID.equals("weather")) {

                switch (channel_group) {
                    case "temperature":
                        updateTemperature(channelUID);
                    case "humidity":
                        updateHumidity(channelUID);
                    default:
                        logger.debug("Unknown Channel (Not sure if this could ever happen)", channel_group);
                }

            } else if (thingTypeID.equals("environment")) {
                /* do nothing yet */
            }

        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }

    }

    public void updateTemperature(ChannelUID channelUID) {

        BigDecimal lt = this.lt;
        BigDecimal lg = this.lg;

        // Thread temp_T = new Thread() {
        // @Override
        // public void run() {
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
                        System.out.println(channelUID);

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
        // }
        // };
        //
        //// temp_T.start();

    }

    public void updateHumidity(ChannelUID channelUID) {

        BigDecimal lt = this.lt;
        BigDecimal lg = this.lg;

        // Thread humi_T = new Thread() {
        // @Override
        // public void run() {
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
                        System.out.println(channelUID);

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
        // }
        // };
        //
        // humi_T.start();

    }

    @Override
    public void initialize() {
        logger.debug("Start initializing OpenSense - Line 1");
        // config = getConfigAs(OpenSenseNetworkConfiguration.class);
        Configuration config = getThing().getConfiguration();

        lt = (BigDecimal) config.get("latitude");
        lg = (BigDecimal) config.get("longitude");
        sensorID = (BigDecimal) config.get("sensorID");
        System.out.println("=====================================================");
        System.out.println(lt);
        System.out.println(lg);
        System.out.println(sensorID);
        System.out.println("=====================================================");

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.ONLINE);

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }
}
