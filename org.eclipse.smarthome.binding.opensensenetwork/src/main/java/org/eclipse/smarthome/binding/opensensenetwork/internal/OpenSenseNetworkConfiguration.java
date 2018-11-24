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

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jdt.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * The {@link OpenSenseNetworkConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author ISE - Initial contribution
 */
public class OpenSenseNetworkConfiguration {

    /**
     * Sample configuration parameter. Replace with your own.
     */
    public String config1;

    public static void UpdateThingConfigFile() {

        Unirest.get("https://www.opensense.network/progprak/beta/api/v1.0/measurands")
                .asJsonAsync(new Callback<JsonNode>() {

                    @Override
                    public void completed(@Nullable HttpResponse<JsonNode> response) {

                        JsonNode body = response.getBody();
                        JSONArray arr = body.getArray();
                        ArrayList<String> measurands = new ArrayList<String>();
                        JSONObject jObj = new JSONObject();
                        for (int i = 0; i < arr.length(); i++) {
                            jObj = (JSONObject) arr.get(i);
                            measurands.add((String) jObj.get("name"));
                        }
                        try {
                            File file = new File(OpenSenseNetworkHandler.class.getProtectionDomain().getCodeSource()
                                    .getLocation().getPath());
                            String filepath = file.getAbsolutePath().concat("/ESH-INF/thing/thing-types.xml");
                            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                            Document doc = docBuilder.parse(filepath);

                            Node description = doc.getElementsByTagName("config-description").item(0);
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

                            System.out.println("Done");
                            System.out.println(filepath);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                    @Override
                    public void failed(@Nullable UnirestException e) {
                        System.out.println("The request has failed");
                    }

                    @Override
                    public void cancelled() {
                        System.out.println("The request has been cancelled");
                    }

                });
    }

}
