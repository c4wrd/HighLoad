package com.c4wrd.loadtester.configuration;

import com.c4wrd.loadtester.exceptions.HighLoadException;
import com.c4wrd.loadtester.request.Endpoint;
import com.c4wrd.loadtester.request.QueryCombination;
import com.c4wrd.loadtester.request.QueryParameter;
import com.typesafe.config.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EndpointConfiguration {

    private HighLoadConfig loadTestingConfig;
    private Config endpointConfiguration;
    private Map<String, Endpoint> endpointList;

    public EndpointConfiguration(HighLoadConfig loadTestingConfig, Config endpointConfiguration) throws HighLoadException {
        this.loadTestingConfig = loadTestingConfig;
        this.endpointConfiguration = endpointConfiguration;
        this.endpointList = new HashMap<>();
        this.initEndpoints();
        this.initQueryData();
    }

    private void initQueryData() throws HighLoadException {
        for (String endpoint : loadTestingConfig.getEndpoints()) {
            try {

                Endpoint workingEndpoint = this.endpointList.get(endpoint);

                ConfigList endpointConfig = endpointConfiguration.getList(endpoint);

                for (ConfigValue value : endpointConfig) {    // we're reading an array of possible query parameter combinations

                    QueryCombination combo = new QueryCombination();
                    List<QueryParameter> paramList = this.loadParams((ConfigObject) value);
                    paramList.stream().forEach(combo::addQueryParameter);
                    workingEndpoint.addQueryCombination(combo);

                }

            } catch (ConfigException.Missing e) {
                throw new HighLoadException(String.format("Failed to find the endpoint %s in the endpoint data-set"));
            }
        }
    }

    private List<QueryParameter> loadParams(ConfigObject params) throws HighLoadException {
        List<QueryParameter> paramList = new LinkedList<>();

        for (Map.Entry<String, ConfigValue> rawParam : params.entrySet()) {
            paramList.add(this.getParameterForSet(rawParam));
        }

        return paramList;
    }

    private QueryParameter getParameterForSet(Map.Entry<String, ConfigValue> rawParamSet) throws HighLoadException {
        QueryParameter param = new QueryParameter(rawParamSet.getKey());

        if (rawParamSet.getValue().unwrapped() instanceof String) {

            // we can add the value as expected
            param.addValue((String) rawParamSet.getValue().unwrapped());

        } else {

            if (loadTestingConfig.getDataFile() == null) {
                throw new HighLoadException("Attempted to create a data-backed parameter, but there was no data-set configured");
            }

            // this is backed by our data-set, todo clean up
            param.setDatabackingAlias((
                    (Map<String, String>) rawParamSet.getValue().unwrapped()
            ).get("key"));

        }

        return param;
    }

    private void initEndpoints() {
        for (String endpoint : this.loadTestingConfig.getEndpoints()) {
            this.endpointList.put(endpoint, new Endpoint(endpoint));
        }
    }

    public List<Endpoint> getEndpoints() {
        // todo fix this, shouldn't need to do a stream operation

        return this.endpointList.values()
                .stream()
                .collect(Collectors.toList());
    }
}
