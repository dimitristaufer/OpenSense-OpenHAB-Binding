package org.eclipse.smarthome.binding.opensensenetwork.internal;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONObject;

public class OHValue {
    private final String link;
    private final String state;
    private final JSONObject stateDescription;
    private final Boolean editable;
    private final String type;
    private final String name;
    private final String label;
    private final String category;
    private final Array tags;
    private final Array groupNames;
    private final Timestamp timestamp;

    public OHValue(String link, String state, JSONObject stateDescription, Boolean editable, String type, String name,
            String label, String category, Array tags, Array groupNames) {
        this.link = link;
        this.state = state;
        this.stateDescription = stateDescription;
        this.editable = editable;
        this.type = type;
        this.name = name;
        this.label = label;
        this.category = category;
        this.tags = tags;
        this.groupNames = groupNames;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public OHValue(JSONObject json) {
        this.link = json.getString("link");
        this.state = json.getString("state");
        this.stateDescription = json.getJSONObject("stateDescription");
        this.editable = json.getBoolean("editable");
        this.type = json.getString("type");
        this.name = json.getString("name");
        this.label = json.getString("label");
        this.category = json.getString("category");
        this.tags = (Array) json.get("tags");
        this.groupNames = (Array) json.get("groupNames");
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public String link() {
        return link;
    }

    public String state() {
        return state;
    }

    public JSONObject stateDescription() {
        return stateDescription;
    }

    public Boolean editable() {
        return editable;
    }

    public String type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String label() {
        return label;
    }

    public String category() {
        return category;
    }

    public Array tags() {
        return tags;
    }

    public Array groupNames() {
        return groupNames;
    }

    public Timestamp timestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format(
                "link#%s\nstate#%s\nstateDescription#%s\neditable#%b\ntype#%s\nname#%s\nlabel#%s\ncategory#%s\ntags#%s\ngroupNames#%s\ntimestamp#%s",
                this.link, this.state, this.stateDescription.toString(), this.editable, this.type, this.name,
                this.label, this.category, this.tags.toString(), this.groupNames.toString(), this.timestamp.toString());
    }

    public JSONObject getAsJson() {
        JSONObject json = new JSONObject();
        json.append("link", link);
        json.append("state", state);
        json.append("stateDescription", stateDescription);
        json.append("editable", editable);
        json.append("type", type);
        json.append("name", name);
        json.append("label", label);
        json.append("category", category);
        json.append("tags", tags);
        json.append("groupNames", groupNames);
        json.append("timeStamp", timestamp);
        return json;
    }

    public JSONObject getAsReadyToStore(String osSensorId) {
        JSONObject json = new JSONObject();
        json.append("sensorId", Integer.parseInt(osSensorId));
        json.append("timestamp", timestamp);
        json.append("numberValue", Double.parseDouble(state));
        return json;
    }

}
