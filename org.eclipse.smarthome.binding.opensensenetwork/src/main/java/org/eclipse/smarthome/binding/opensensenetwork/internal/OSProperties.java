package org.eclipse.smarthome.binding.opensensenetwork.internal;

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_MEASURANDS_URL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OSProperties {

    static Properties prop = new Properties();
    static InputStream input = null;
    static OutputStream output = null;

    static File file = new File(
            OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());

    /* TODO: Add timeout for values */

    public static String sensorID(String measurand) {

        try {
            prop.load(new FileInputStream(file.getPath() + "/OS.properties"));
            String key = String.format("%s_%s)", "sensorId", measurand); // ex. "sensorId_temperature"
            return prop.getProperty(key);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }

    public static String lt() {

        try {
            prop.load(new FileInputStream(file.getPath() + "/OS.properties"));
            String lt = prop.getProperty("lt");
            if (lt != null) {
                if (lt == "auto") {
                    // TODO: Get Lt based on IP
                    return String.format("%f", 52.5167); // Berlin
                } else {
                    return prop.getProperty("lt");
                }
            } else {
                return null;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }

    public static String lg() {

        try {
            prop.load(new FileInputStream(file.getPath() + "/OS.properties"));
            String lt = prop.getProperty("lg");
            if (lt != null) {
                if (lt == "auto") {
                    // TODO: Get Lg based on IP
                    return String.format("%f", 13.4000); // Berlin
                } else {
                    return prop.getProperty("lg");
                }
            } else {
                return null;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }

    public static Integer measurandId(String measurand) {
        try {
            prop.load(new FileInputStream(file.getPath() + "/OS.properties"));
            String measurandId = prop.getProperty(String.format("%s_%s)", "measurandId", measurand));
            if (measurandId == null) {
                return (remoteMeasurandId(measurand));
            }
            return Integer.parseInt(measurandId);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    private static Integer remoteMeasurandId(String measurand) {

        HttpResponse<JsonNode> response;
        try {
            response = Unirest.get(OS_MEASURANDS_URL).queryString("name", measurand).asJson();

            JSONObject json = response.getBody().getArray().getJSONObject(0);
            String measurandId = String.format("%d", json.optInt("id", -1));

            storeMeasurandID(measurand, measurandId);

            return json.optInt("id", -1);

        } catch (UnirestException error) {
            error.printStackTrace();
            return null;
        }

    }

    public static void storeMeasurandID(String measurand, String ID) {

        try {
            output = new FileOutputStream(file.getPath() + "/OS.properties");
            String key = String.format("%s_%s", "measurandId", measurand); // ex. "measurandId_temperature"
            prop.setProperty(key, ID);
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("ERROR: Writing SensorID to Properties");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: Writing SensorID to Properties");
                }
            }

        }

    }

    public static void storeSensorID(String measurand, String sensorID) {

        try {
            output = new FileOutputStream(file.getPath() + "/OS.properties");
            String key = String.format("%s_%s", "sensorId", measurand); // ex. "sensorId_temperature"
            prop.setProperty(key, sensorID);
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("ERROR: Writing SensorID to Properties");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: Writing SensorID to Properties");
                }
            }

        }

    }

    public static void storeLt(String lt) {

        try {
            output = new FileOutputStream(file.getPath() + "/OS.properties");
            prop.setProperty("lt", lt);
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("ERROR: Writing SensorID to Properties");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: Writing SensorID to Properties");
                }
            }

        }

    }

    public static void storeLg(String lg) {

        try {
            output = new FileOutputStream(file.getPath() + "/OS.properties");
            prop.setProperty("lg", lg);
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("ERROR: Writing SensorID to Properties");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: Writing SensorID to Properties");
                }
            }

        }

    }

}
