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

import static org.eclipse.smarthome.binding.opensensenetwork.internal.OpenSenseNetworkBindingConstants.OS_MEASURANDS_URL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * The {@link OpenSenseNetworkConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author ISE - Initial contribution
 */
public class OpenSenseNetworkConfiguration {

    /**
     * GETs measurands from OpenSense and manipulates "thing-types.xml" in order to show switches
     * for the different measurands
     *
     * @return
     * @throws UnirestException
     */
    public synchronized boolean performConfiguration() throws UnirestException {

        HttpResponse<JsonNode> response = Unirest.get(OS_MEASURANDS_URL).asJson();
        {

            JsonNode body = response.getBody();
            JSONArray arr = body.getArray();
            ArrayList<String> measurands = new ArrayList<String>();
            JSONObject jObj = new JSONObject();
            for (int i = 0; i < arr.length(); i++) {
                jObj = (JSONObject) arr.get(i);
                measurands.add((String) jObj.get("name"));
            }

            try {
                File file = new File(
                        OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                String filepath = file.getAbsolutePath().concat("/ESH-INF/thing/thing-types.xml");
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(filepath);

                Node description = doc.getElementsByTagName("config-description").item(0); // 0 = weather
                NodeList parameters = doc.getElementsByTagName("parameter");

                for (String name : measurands) {
                    boolean exists = false;
                    for (int i = 0; i < parameters.getLength(); i++) {
                        if (parameters.item(i).getTextContent().contains(name)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        org.w3c.dom.Element newBox = doc.createElement("parameter");
                        newBox.setAttribute("name", name);
                        newBox.setAttribute("type", "boolean");
                        newBox.setAttribute("required", "false");
                        org.w3c.dom.Element label = doc.createElement("label");
                        label.setTextContent(name);
                        newBox.appendChild(label);
                        description.appendChild(newBox);
                    }
                }

                DOMSource source = new DOMSource(doc);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StreamResult result = new StreamResult(filepath);
                transformer.transform(source, result);

                System.out.println("Done - Config");
                // System.out.println(filepath);
                boolean success = false;
                success = didWriteJSONtoFile(body, file.getAbsolutePath().concat("/ESH-INF/binding/measurants.json"));
                success = didCreateChannels(measurands);

                if (success) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }

        }

    }

    /**
     * Writes the Measurands JSON to file
     * We will later have to retrieve the id for a given measurand quickly
     *
     * @param jObj
     * @param filepath
     * @return
     */
    private synchronized boolean didWriteJSONtoFile(JsonNode jObj, String filepath) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(filepath), jObj.toString());
            return true;
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * EXPERIMENTAL: Automatically manipulates "thing-types.xml" to create new channels based on new measurands
     * Currently not working and I have no idea why :(
     * Also I am not creating "channel-group-types", which I believe is necessary for it to work
     * The main problem is, that OpenHAB doesn't seem to re-read the new manipulated "thing-types.xml"
     *
     * @param measurands
     * @return
     */
    private synchronized boolean didCreateChannels(ArrayList<String> measurands) {

        System.out.println("The Channels are: " + measurands);

        try {
            File file = new File(
                    OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String filepath = file.getAbsolutePath().concat("/ESH-INF/thing/thing-types.xml");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            Node description_node = doc.getElementsByTagName("channel-groups").item(0); // 0 = weather
            NodeList parameters = doc.getElementsByTagName("channel-group");

            for (String measurand : measurands) {
                boolean exists = false;
                for (int i = 0; i < parameters.getLength(); i++) {
                    if (parameters.item(i).getAttributes().item(0).getNodeValue().contains(measurand)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    /* channel-group */
                    org.w3c.dom.Element newBox = doc.createElement("channel-group");
                    newBox.setAttribute("id", measurand);
                    newBox.setAttribute("typeId", measurand);
                    /* label */
                    org.w3c.dom.Element label_elem = doc.createElement("label");
                    label_elem.setTextContent("Sample Measurand Label");
                    newBox.appendChild(label_elem);
                    /* description */
                    org.w3c.dom.Element desc_elem = doc.createElement("description");
                    desc_elem.setTextContent("Sample Measurand Description");
                    newBox.appendChild(desc_elem);
                    /* <- add child -> */
                    description_node.appendChild(newBox);
                }
            }

            DOMSource source = new DOMSource(doc);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(filepath);
            transformer.transform(source, result);

            System.out.println("Done - Create Channels");
            // System.out.println(filepath);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

}
