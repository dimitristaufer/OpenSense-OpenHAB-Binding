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
import org.eclipse.smarthome.core.library.types.QuantityType;
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

        String measurand = channelUID.getGroupId(); // ex. "temperature"
        String thingType = channelUID.getThingUID().getThingTypeUID().getId(); // ex. "receive"

        if (command instanceof RefreshType) {
            if (thingType.equals("receive")) {
                OSSensor sensor = OSSensor.getSensorForMeasurand(measurand);
                // System.out.println("Updating Channel: '" + measurand + "' using Sensor:");
                // System.out.println(sensor.toString());
                updateState(channelUID, getCurrentValue(sensor));
                // updateState(channelUID, OnOffType.OFF);
            } else if (thingType.equals("contribute")) {
                /* do nothing yet */
            }
        }
    }

    /**
     * This method GETs the current value for a given sensor and automatically handles units
     *
     * @param OSSensor
     */
    @SuppressWarnings("rawtypes")
    public QuantityType getCurrentValue(OSSensor sensor) {

        // System.out.println("Getting current val for sensorid:" + sensor.id());

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_LATEST_URL.replace("SENSORID", sensor.id())).asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);
            JSONObject values = json.getJSONArray("values").getJSONObject(0);
            OSValue osValue = new OSValue(values.getString("timestamp"), values.getDouble("numberValue"));
            return OSQuantityType.getQuantityType(sensor.measurandId(), osValue.numberValue());

        } catch (UnirestException error) {
            error.printStackTrace();
            OSValue osValue = new OSValue("", 0.0);
            return OSQuantityType.getQuantityType(sensor.measurandId(), osValue.numberValue());

        }

    }

    @Override
    public void initialize() {

        updateStatus(ThingStatus.UNKNOWN);

        System.out.println("Thing Type: " + thing.getThingTypeUID());

        if (thing.getThingTypeUID().equals(THING_TYPE_RECEIVE)) {

            try {
                OpenSenseNetworkConfiguration.performConfiguration();
            } catch (UnirestException e) {
                e.printStackTrace();
            }

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

    /* For future reference -> How to run something on another thread */

    // Thread humi_T = new Thread() {
    // @Override
    // public void run() {
    // }
    // };
    //
    // humi_T.start();

    /*
     * DynamicStateDescriptionProvider dyn = new DynamicStateDescriptionProvider() {
     *
     * @Override
     * public @Nullable StateDescription getStateDescription(Channel channel,
     *
     * @Nullable StateDescription originalStateDescription, @Nullable Locale locale) {
     * // TODO Auto-generated method stub
     *
     * return null;
     * }
     * };
     */
}