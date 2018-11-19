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

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
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

        if (command instanceof RefreshType) { /*
                                               * What kind of type is the command? RefreshType means, the value is
                                               * being accessed via GET for example to be displayed in the PaperUI
                                               */

            System.out.println("calling sample data");

            boolean dataDownloaded = getSampleData(channelUID, command);

            findSensor(49.1259, 9.1428, 50000);

            if (dataDownloaded) {

                /* For now, let's just assume the data is already available */

                if (thingTypeID.equals("weather")) {

                    switch (channel_group) {
                        case "temperature":
                            QuantityType<Temperature> temperature = new QuantityType<Temperature>(23.0,
                                    SIUnits.CELSIUS);
                            updateState(channelUID, temperature);
                        case "humidity":
                            QuantityType<Dimensionless> humidity = new QuantityType<Dimensionless>(65.6,
                                    SmartHomeUnits.PERCENT);
                            updateState(channelUID, humidity);
                        default:
                            logger.debug("Unknown Channel (Not sure if this could ever happen)", channel_group);
                    }

                } else if (thingTypeID.equals("environment")) {
                    /* do nothing yet */
                }

            }

        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }

    }

    private void findSensor(double lt, double lg, int md) {

        String refPoint = String.format("(%f,%f)", lt, lg);
        String maxDistance = String.format("%d", md);

        System.out.println(refPoint);
        System.out.println(maxDistance);

        Unirest.get("https://www.opensense.network/progprak/beta/api/v1.0/sensors").queryString("measurandId", "7")
                .queryString("refPoint", refPoint).queryString("maxDistance", maxDistance)
                .queryString("maxSensors", "1").asJsonAsync(new Callback<JsonNode>() {

                    @Override
                    public void failed(@Nullable UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    @Override
                    public void completed(@Nullable HttpResponse<JsonNode> response) {
                        JsonNode body = response.getBody();
                        System.out.println(body.toString());

                        Gson gson = new Gson();
                        OSSensor sensor = gson.fromJson(body.toString(), OSSensor.class);

                    }

                    @Override
                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });

        // OSSensor sensor = new OSSensor(id, userId, measurandId, location, altitudeAboveGround, directionVertical,
        // directionHorizontal, sensorModel, accuracy, attributionText, attributionURL, licenseId);

    }

    private boolean getSampleData(ChannelUID channelUID, Command command) { // Get sample data from OpenSense

        System.out.println("yoo");

        return true;
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing OpenSense - Line 1");
        config = getConfigAs(OpenSenseNetworkConfiguration.class);

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
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>
            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        updateStatus(ThingStatus.ONLINE);

        logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }
}
