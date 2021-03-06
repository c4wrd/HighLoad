package com.c4wrd.loadtester.testservice;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to set options on the URLConneciton, such as headers,
 * cookies, whether or not to read the input stream, whether or
 * not to add POST data, etc...
 */
public class RequestOptions {

    public static RequestOptions DEFAULT = new RequestOptions();

    /**
     * Whether or not the the request callable will read the input stream
     * and store response in the payload. Default: False
     */
    public boolean readInputStream;

    /**
     * A list of URL options to specify, which are sent as headers
     * in the request.
     * <p>
     * requestOption.addProperty("Cookie", "cookievalue");
     * requestOption.addProperty("Authorization", "...");
     */
    public Map<String, String> urlProperties;

    public RequestOptions() {
        this.readInputStream = false;
        this.urlProperties = new HashMap<String, String>();
    }

    public RequestOptions(Config config) {
        this.urlProperties = new HashMap<>();
        try {
            this.readInputStream = config.getBoolean("return_response");
        } catch (ConfigException.Missing ignored) {

        }

        try {
            for (Map.Entry<String, ConfigValue> values : config.getConfig("properties").entrySet()) {
                this.urlProperties.put(values.getKey(), (String) values.getValue().unwrapped());
            }
        } catch (ConfigException.Missing ignored) {

        }
    }

    public void addProperty(String propertyName, String propertyValue) {
        this.urlProperties.put(propertyName, propertyValue);
    }

    public void setReadInputStream(boolean readInputStream) {
        this.readInputStream = readInputStream;
    }

}
