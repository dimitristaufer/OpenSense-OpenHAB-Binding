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
import org.eclipse.jdt.annotation.Nullable;
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

    @Nullable
    private OpenSenseNetworkConfiguration config;

    public OpenSenseNetworkHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        String thingType = channelUID.getThingUID().getThingTypeUID().getId(); // ex. "receive"
        String groupId = String.format(channelUID.getGroupId()); // ex. "temperature"
        String[] channelUIDstring = channelUID.toString().split("#");
        // ex. "opensensenetwork:receive:0e4604b1:temperature#value"
        String baseId = channelUIDstring[0];
        // ex. "opensensenetwork:receive:0e4604b1:temperature"
        String channelId = channelUIDstring[1];
        // ex. "value"

        System.out.println("Command Type: " + command.toString());

        if (command instanceof RefreshType) {
            if (thingType.equals("receive")) {

                /*
                 * && channelId.equals("value")
                 *
                 * This only get's called when the channelId equals 'value' to prevent server queries for every
                 * channelId (sensorModel, location, directionVertical, etc..)
                 *
                 * Instead we call it once and manually update all the channel states using the OSValue object
                 */

                offAllChannels(channelUID.getId().split("#")[0]);

                OSSensor sensor = OSSensor.getSensorForMeasurand(groupId);
                // System.out.println("Updating Channel: '" + groupId + "' using Sensor:" + sensor.toString());
                updateChannels(getCurrentValue(sensor), channelUID.getId().split("#")[0]);

            } else if (thingType.equals("contribute")) {
                /* do nothing yet */
            } else {
                /* doesn't exist */
            }
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

    @Override
    public void initialize() {

        if (DEBUG) {
            // DEBUG: Clear local cache -> Force server values
            OSProperties.removeAllValues();
        }

        updateStatus(ThingStatus.UNKNOWN);

        if (thing.getThingTypeUID().equals(THING_TYPE_RECEIVE)) {

            Configuration config = getThing().getConfiguration();
            if (config.get("latitude") != null && config.get("longitude") != null) {
                String lt = config.get("latitude").toString();
                String lg = config.get("longitude").toString();
                if (lt != "" & lg != "") {
                    OSProperties.storeLt(config.get("latitude").toString());
                    OSProperties.storeLg(config.get("longitude").toString());
                } else {
                    OSProperties.storeLt("auto");
                    OSProperties.storeLg("auto");
                }
            }

            // Map<String, Object> parameters = config.getProperties();
            //
            // // A list of the user selected measurandIDs
            // // (for now we don't use this!)
            // for (Entry<String, Object> parameter : parameters.entrySet()) {
            // if (parameter.getValue().equals(true)) {
            // desired_channels.add(parameter.getKey());
            // }
            // }

            updateStatus(ThingStatus.ONLINE);
        }

    }

}