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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link OpenSenseNetworkBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author ISE - Initial contribution
 */
@NonNullByDefault
public class OpenSenseNetworkBindingConstants {

    public static final String BINDING_ID = "opensensenetwork";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_RECEIVE = new ThingTypeUID(BINDING_ID, "receive");
    public static final ThingTypeUID THING_TYPE_CONTRIBUTE = new ThingTypeUID(BINDING_ID, "contribute");

    public static final Boolean SERVER_ONLY_MODE = true;

    // List of all Channel ids
    // public static final String CHANNEL_TEMPERATURE = "temperature";
    // public static final String CHANNEL_HUMIDITY = "humidity";

    // public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_WEATHER);
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(
            Arrays.asList(THING_TYPE_RECEIVE, THING_TYPE_CONTRIBUTE));

    public static final String OS_BASE_URL = "https://www.opensense.network/progprak/beta/api/v1.0/";
    public static final String OS_VALUE_URL = "values";
    public static final String OS_MEASURANDS_URL = "https://www.opensense.network/progprak/beta/api/v1.0/measurands";
    public static final String OS_SENSOR_URL = "https://www.opensense.network/progprak/beta/api/v1.0/sensors";
    public static final String OS_LATEST_URL = "https://www.opensense.network/progprak/beta/api/v1.0/sensors/SENSORID/values/last";
    public static final String OS_OLDEST_URL = "https://www.opensense.network/progprak/beta/api/v1.0/sensors/SENSORID/values/first";

}
