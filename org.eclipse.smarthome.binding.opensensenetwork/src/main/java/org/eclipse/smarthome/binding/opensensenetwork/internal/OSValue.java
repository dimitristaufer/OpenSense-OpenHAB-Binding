package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.text.DecimalFormat;

public class OSValue {

    private String observationTime;
    private double numberValue;

    private int id;
    private int userId;
    private int measurandId;
    private int unitId;
    private String location;
    private double altitudeAboveGround;
    private double directionVertical;
    private double directionHorizontal;
    private String sensorModel;
    private int accuracy;
    private String attributionText;
    private String attributionURL;
    private int licenseId;

    public OSValue() {

    }

    public OSValue(String observationTime, double numberValue) {
        this.observationTime = observationTime;
        this.numberValue = numberValue;
    }

    public OSValue(String observationTime, double numberValue, int id, int userId, int measurandId) {
        this.observationTime = observationTime;
        this.numberValue = numberValue;
    }

    public double numberValue() {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(numberValue));
    }

    public String observationTime() {
        return this.observationTime;
    }

    /*
     *
     * <channel id="id" typeId="id"/>
     * <channel id="userId" typeId="userId"/>
     * <channel id="measurandId" typeId="measurandId"/>
     * <channel id="unitId" typeId="unitId"/>
     * <channel id="location" typeId="location"/>
     * <channel id="altitudeAboveGround" typeId="altitudeAboveGround"/>
     * <channel id="directionVertical" typeId="directionVertical"/>
     * <channel id="directionHorizontal" typeId="directionHorizontal"/>
     * <channel id="sensorModel" typeId="sensorModel"/>
     * <channel id="accuracy" typeId="accuracy"/>
     * <channel id="attributionText" typeId="attributionText"/>
     * <channel id="attributionURL" typeId="attributionURL"/>
     * <channel id="licenseId" typeId="licenseId"/>
     * <channel id="observationTime" typeId="observationTime"/>
     * <channel id="value" typeId="pm2_5_level"/>
     *
     *
     */

}
