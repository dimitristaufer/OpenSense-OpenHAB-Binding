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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
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
@SuppressWarnings("deprecation")
public class OpenSenseNetworkHandler extends BaseThingHandler {

    // private final Logger logger = LoggerFactory.getLogger(OpenSenseNetworkHandler.class);
    // private final ArrayList<String> desired_channels = new ArrayList<>();

    // @Nullable
    // private OpenSenseNetworkConfiguration config;

    public OpenSenseNetworkHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        System.out.println("Command Type: " + command.toString());

        if (command instanceof RefreshType) {
            refreshChannel(channelUID);
        }
    }

    public void refreshChannel(ChannelUID channelUID) {

        String thingType = channelUID.getThingUID().getThingTypeUID().getId(); // ex. "receive"
        String groupId = String.format(channelUID.getGroupId()); // ex. "temperature"
        String baseId = channelUID.getId().split("#")[0];

        if (thingType.equals("receive")) {

            /*
             * && channelId.equals("value")
             *
             * This only get's called when the channelId equals 'value' to prevent server queries for every
             * channelId (sensorModel, location, directionVertical, etc..)
             *
             * Instead we call it once and manually update all the channel states using the OSValue object
             */

            offAllChannels(baseId);

            OSSensor sensor = OSSensor.getSensorForMeasurand(groupId);
            // System.out.println("Updating Channel: '" + groupId + "' using Sensor:" + sensor.toString());
            updateChannels(getCurrentValue(sensor), baseId);

        } else if (thingType.equals("contribute")) {
            /* do nothing yet */
        } else {
            /* doesn't exist */
        }

    }

    private void offAllChannels(String baseId) {

        updateState(baseId + "#id", OnOffType.OFF);
        updateState(baseId + "#userId", OnOffType.OFF);
        updateState(baseId + "#measurandId", OnOffType.OFF);
        updateState(baseId + "#unitId", OnOffType.OFF);
        updateState(baseId + "#location", OnOffType.OFF);
        updateState(baseId + "#altitudeAboveGround", OnOffType.OFF);
        updateState(baseId + "#directionVertical", OnOffType.OFF);
        updateState(baseId + "#directionHorizontal", OnOffType.OFF);
        updateState(baseId + "#sensorModel", OnOffType.OFF);
        updateState(baseId + "#accuracy", OnOffType.OFF);
        updateState(baseId + "#attributionText", OnOffType.OFF);
        updateState(baseId + "#attributionURL", OnOffType.OFF);
        updateState(baseId + "#licenseId", OnOffType.OFF);
        updateState(baseId + "#observationTime", OnOffType.OFF);
        updateState(baseId + "#value", OnOffType.OFF);

    }

    private void updateChannels(OSValue osVal, String baseId) {

        updateState(baseId + "#id", DecimalType.valueOf(String.format("%d", osVal.id())));
        updateState(baseId + "#userId", DecimalType.valueOf(String.format("%d", osVal.userId())));
        updateState(baseId + "#measurandId", DecimalType.valueOf(String.format("%d", osVal.measurandId())));
        updateState(baseId + "#unitId", DecimalType.valueOf(String.format("%d", osVal.unitId())));
        updateState(baseId + "#location", StringType.valueOf(osVal.location()));
        updateState(baseId + "#altitudeAboveGround",
                DecimalType.valueOf(String.format("%f", osVal.altitudeAboveGround())));
        updateState(baseId + "#directionVertical", DecimalType.valueOf(String.format("%f", osVal.directionVertical())));
        updateState(baseId + "#directionHorizontal",
                DecimalType.valueOf(String.format("%f", osVal.directionHorizontal())));
        updateState(baseId + "#sensorModel", StringType.valueOf(osVal.sensorModel()));
        updateState(baseId + "#accuracy", DecimalType.valueOf(String.format("%d", osVal.accuracy())));
        updateState(baseId + "#attributionText", StringType.valueOf(osVal.attributionText()));
        updateState(baseId + "#attributionURL", StringType.valueOf(osVal.attributionURL()));
        updateState(baseId + "#licenseId", DecimalType.valueOf(String.format("%d", osVal.licenseId())));
        updateState(baseId + "#observationTime", StringType.valueOf(osVal.observationTime()));

        updateState(baseId + "#value", OSQuantityType.getQuantityType(osVal.measurandId(), osVal.numberValue()));

    }

    /**
     * This method GETs the current value for a given sensor and automatically handles units
     *
     * @param OSSensor
     */
    public OSValue getCurrentValue(OSSensor sensor) {

        // System.out.println("Getting current val for sensorid:" + sensor.id());

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_LATEST_URL.replace("SENSORID", sensor.id())).asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);
            OSValue osVal = OSValue.makeValue(json);
            return osVal;

        } catch (UnirestException error) {
            error.printStackTrace();
            OSValue osValue = new OSValue();
            return osValue;

        }

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void initialize() {

        updateStatus(ThingStatus.UNKNOWN);

        Boolean removeAllSensors = false;
        if (thing.getThingTypeUID().equals(THING_TYPE_RECEIVE)) {

            Configuration config = getThing().getConfiguration();
            if (config.get("latitude") != null && config.get("longitude") != null) {
                String lt = config.get("latitude").toString();
                String lg = config.get("longitude").toString();

                if (!(lt.equals(OSProperties.lt()) || !(lg.equals(OSProperties.lg())))) {
                    // New location -> clear local cache
                    removeAllSensors = true;
                }

                if (lt != "" & lg != "") {
                    OSProperties.storeLt(config.get("latitude").toString());
                    OSProperties.storeLg(config.get("longitude").toString());
                } else {
                    OSProperties.storeLt("auto");
                    OSProperties.storeLg("auto");
                }
            }
            if (config.get("distance") != null) {
                if (!(config.get("distance").equals(OSProperties.maxDistance()))) {
                    // New maxDistance -> clear local cache
                    removeAllSensors = true;
                }
                OSProperties.storeMaxDistance(config.get("distance").toString());
            }
            if (config.get("accuracy") != null) {
                if (!(config.get("accuracy").equals(OSProperties.minAccuracy()))) {
                    // New minAccuracy -> clear local cache
                    removeAllSensors = true;
                }
                OSProperties.storeMinAccuracy(config.get("accuracy").toString());
            }

            if (removeAllSensors) {
                OSProperties.removeAllSensors();
            }

            updateStatus(ThingStatus.ONLINE);

            // scheduler.scheduleWithFixedDelay(new Runnable() {
            // @Override
            // public void run() {
            // try {
            // // update Values
            // // initialize();
            // System.out.println("Scheduled Update");
            //
            // for (Channel channel : getThing().getChannels()) {
            // refreshChannel(channel.getUID());
            // }
            //
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            // }
            // }, 0, 12, TimeUnit.SECONDS);

        } else if (thing.getThingTypeUID().equals(THING_TYPE_CONTRIBUTE)) {

            Configuration config = getThing().getConfiguration();
            if (config.get("username") != null) {
                String username = config.get("username").toString();
                OSProperties.storeUsername(username);
            }
            if (config.get("password") != null) {
                String password = config.get("password").toString();
                OSProperties.storePassword(password);
            }
            if (config.get("sensor_id") != null && config.get("polling_interval") != null) {
                String sensor_id = config.get("sensor_id").toString();
                String measurand = OSSensor.getMeasurandNameFromSensor(sensor_id);
                OSProperties.storeContributeSensorID(sensor_id, measurand);
                String polling_interval = config.get("polling_interval").toString();
                OSProperties.storeContributePollingInterval(polling_interval, measurand);

                String measurandFromSensor = OSSensor.getMeasurandNameFromSensor(config.get("sensor_id").toString());
                if (OSContribute.getMeasurandsToContribute(OHItem.getMeasurandsFromOpenHab(),
                        OSContribute.getMeasurandsFromOpenSense()).contains(measurandFromSensor)) {
                    OSProperties.storeOpenHABLink(OHItem.getLinkForMeasurand(measurand), measurand);
                    System.out.println(OHItem.getLinkForMeasurand(measurand)); // TO CHECK IF CORRECT
                }
            }

        }

    }

    @Override
    public void dispose() {
        System.out.println("scheduler shutdown");
        scheduler.shutdown();
    }

}