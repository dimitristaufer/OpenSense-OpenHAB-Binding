package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.core.library.unit.MetricPrefix.HECTO;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Illuminance;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;

import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;

public class OSQuantityType {

    @SuppressWarnings("rawtypes")
    public static QuantityType getQuantityType(int measurandId, double value) {

        System.out.println("TYPE IS: " + measurandId);

        switch (measurandId) {
            case 1:
                QuantityType<Temperature> temperature = new QuantityType<Temperature>(value, SIUnits.CELSIUS);
                return temperature;
            case 2:
                QuantityType<Dimensionless> noise = new QuantityType<Dimensionless>(value, SmartHomeUnits.DECIBEL);
                return noise;
            case 3:
                QuantityType<Dimensionless> humidity = new QuantityType<Dimensionless>(value, SmartHomeUnits.PERCENT);
                return humidity;
            case 4:
                QuantityType<Illuminance> brightness = new QuantityType<Illuminance>(value, SmartHomeUnits.LUX);
                return brightness;
            case 5:
                QuantityType<Pressure> air_pressure = new QuantityType<Pressure>(value, HECTO(SIUnits.PASCAL));
                return air_pressure;
            case 6:
                QuantityType<Speed> wind_speed = new QuantityType<Speed>(value, SmartHomeUnits.METRE_PER_SECOND);
                return wind_speed;
            case 7:
                QuantityType<Angle> wind_direction = new QuantityType<Angle>(value, SmartHomeUnits.DEGREE_ANGLE);
                return wind_direction;
            case 8:
                QuantityType<Dimensionless> cloudiness = new QuantityType<Dimensionless>(value,
                        SmartHomeUnits.PARTS_PER_MILLION);
                return cloudiness;
            case 9:
                QuantityType<Length> precipitation_amount = new QuantityType<Length>(value, SIUnits.METRE);
                return precipitation_amount;
            case 10:
                QuantityType<Dimensionless> precipitation_type = new QuantityType<Dimensionless>(value,
                        SmartHomeUnits.PERCENT);
                return precipitation_type;
            case 11:
                QuantityType<Dimensionless> pm10 = new QuantityType<Dimensionless>(value, SmartHomeUnits.PERCENT);
                return pm10;
            case 12:
                QuantityType<Dimensionless> pm2_5 = new QuantityType<Dimensionless>(value, SmartHomeUnits.PERCENT);
                return pm2_5;
            default:
                QuantityType<Dimensionless> none = new QuantityType<Dimensionless>(value, SmartHomeUnits.PERCENT);
                return none;
        }

    }

    /*
     * ArithmeticGroupFunction.java
     * DateTimeGroupFunction.java
     * DateTimeType.java
     * DecimalType.java
     * HSBType.java
     * IncreaseDecreaseType.java
     * NextPreviousType.java
     * OnOffType.java
     * OpenClosedType.java
     * PercentType.java
     * PlayPauseType.java
     * PointType.java
     * QuantityType.java
     * QuantityTypeArithmeticGroupFunction.java
     * RawType.java
     * RewindFastforwardType.java
     * StopMoveType.java
     * StringListType.java
     * StringType.java
     * UpDownType.java
     */

}
