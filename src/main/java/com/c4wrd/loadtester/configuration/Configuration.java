package com.c4wrd.loadtester.configuration;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cory Forward
 */
public class Configuration {

    /**
     * Represents the number of threads that our load tester will run
     * with.
     */
    private int num_threads;

    /**
     * The base URL of the server to load test, including the http:// prefix
     */
    private URL baseUrl;

    /**
     * Map of endpoints and a list of required query parameters for the endpoint.
     *
     * Example:
     * {
     *     "graph": ['sc', 'bundle']
     * }
     *
     * This would mean the /graph endpoint requires a lookup for a value for the SC and BUNDLE parameters
     */
    private Map<String, List<String>> endpoints;

    /**
     *  A list of each respective query value supplied to the endpoints along with
     *  acceptable values
     */
    private Map<String, List<String>> query_vals;

    public Configuration() {
        this.query_vals = new HashMap<>();
        this.endpoints = new HashMap<>();
    }

    public int getNumThreads() {
        return num_threads;
    }

    public void setNumThreads(int num_threads) {
        this.num_threads = num_threads;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, List<String>> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, List<String>> endpoints) {
        this.endpoints = endpoints;
    }

    public Map<String, List<String>> getQuery_vals() {
        return query_vals;
    }

    public void setQuery_vals(Map<String, List<String>> query_vals) {
        this.query_vals = query_vals;
    }

}
