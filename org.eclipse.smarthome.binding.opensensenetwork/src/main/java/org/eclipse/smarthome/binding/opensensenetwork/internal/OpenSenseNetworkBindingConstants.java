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
 * @author Dimitri Jan Staufer
 * @author Mateusz Kedzierski
 * @author Maksym Koliesnikov
 * @author Manisha Nagbanshi
 * @author Roman Zabrovarny
 */

@NonNullByDefault
public class OpenSenseNetworkBindingConstants {

    public static final String BINDING_ID = "opensensenetwork";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_RECEIVE = new ThingTypeUID(BINDING_ID, "receive");
    public static final ThingTypeUID THING_TYPE_CONTRIBUTE = new ThingTypeUID(BINDING_ID, "contribute");

    public static final Boolean SERVER_ONLY_MODE = true;

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(
            Arrays.asList(THING_TYPE_RECEIVE, THING_TYPE_CONTRIBUTE));

    public static final String OS_BASE_URL = "https://www.opensense.network/progprak/beta/api/v1.0/";
    // public static final String OS_BASE_URL = "https://www.opensense.network/beta/api/v1.0/";

    public static final String OS_VALUE_URL = "values";
    public static final String OS_MEASURANDS_URL = OS_BASE_URL + "measurands";
    public static final String OS_SENSOR_URL = OS_BASE_URL + "sensors";
    public static final String OS_LATEST_URL = OS_BASE_URL + "sensors/SENSORID/values/last";
    public static final String OS_OLDEST_URL = OS_BASE_URL + "sensors/SENSORID/values/first";
    public static final String OS_POST_LOGIN = OS_BASE_URL + "users/login";
    public static final String OS_POST_ADDVALUE = OS_BASE_URL + "sensors/addValue";
    public static final String OS_POST_ADDMULTIPLEVALUES = OS_BASE_URL + "sensors/addMultipleValues";

}
