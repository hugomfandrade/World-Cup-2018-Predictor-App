package org.hugoandrade.worldcup2018.predictor.network;

import com.google.gson.JsonObject;

public class JsonObjectBuilder {

    private final JsonObject mJsonObject;

    public static JsonObjectBuilder instance() {
        return new JsonObjectBuilder();
    }

    public JsonObjectBuilder() {
        mJsonObject = new JsonObject();
    }

    public JsonObjectBuilder addProperty(String property, String value) {
        mJsonObject.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder addProperty(String property, Number value) {
        mJsonObject.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder addProperty(String property, Boolean value) {
        mJsonObject.addProperty(property, value);
        return this;
    }

    public JsonObjectBuilder removeProperties(String... properties) {
        for (String property : properties)
            mJsonObject.remove(property);
        return this;
    }

    public JsonObject create() {
        return mJsonObject;
    }
}