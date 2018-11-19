package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.util.Dictionary;

public class OSSensor {

    private final int id;
    private final int userId;
    private final int measurandId;
    private final Dictionary<String, Double> location;
    private final double altitudeAboveGround;
    private final double directionVertical;
    private final double directionHorizontal;
    private final String sensorModel;
    private final int accuracy;
    private final String attributionText;
    private final String attributionURL;
    private final int licenseId;

    public OSSensor(int id, int userId, int measurandId, Dictionary<String, Double> location,
            double altitudeAboveGround, double directionVertical, double directionHorizontal, String sensorModel,
            int accuracy, String attributionText, String attributionURL, int licenseId) {

        this.id = id;
        this.userId = userId;
        this.measurandId = measurandId;
        this.location = location;
        this.altitudeAboveGround = altitudeAboveGround;
        this.directionVertical = directionVertical;
        this.directionHorizontal = directionHorizontal;
        this.sensorModel = sensorModel;
        this.accuracy = accuracy;
        this.attributionText = attributionText;
        this.attributionURL = attributionURL;
        this.licenseId = licenseId;

    }

}

//
// "id": 2,
// "userId": 3,
// "measurandId": 7,
// "unitId": 12,
// "location": {
// "lat": 49.1259,
// "lng": 9.1428
// },
// "altitudeAboveGround": 2.0,
// "directionVertical": 0,
// "directionHorizontal": 0,
// "sensorModel": "DWD station",
// "accuracy": 10,
// "attributionText": "Deutscher Wetterdienst (DWD)",
// "attributionURL": "ftp://ftp-cdc.dwd.de/pub/CDC/",
// "licenseId": 4