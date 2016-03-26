package com.c4wrd.loadtester.request;

import com.c4wrd.loadtester.util.RandomSelector;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Endpoint {

    private String endpoint;
    private List<QueryCombination> queryParameterCombinations;

    public Endpoint(String endpoint) {
        this.endpoint = endpoint;
        this.queryParameterCombinations = new LinkedList<>();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public List<QueryCombination> getQueryParameterCombinations() {
        return queryParameterCombinations;
    }

    public void addQueryCombination(QueryCombination combination) {
        this.queryParameterCombinations.add(combination);
    }

    /**
     * Builds a random set of data that this endpoint can consume.
     * This can be used as POST data or as a query string in a GET
     * request.
     */
    public String buildRandomData() {
        StringBuilder builder = new StringBuilder();

        if (this.queryParameterCombinations.size() > 0) {
            QueryCombination selectedCombination = queryParameterCombinations.get(
                    RandomSelector.nextInt(queryParameterCombinations.size())
            );

            builder.append(
                    selectedCombination.getQueryParameterValues()
                            .stream()
                            .map(QueryParam::getRandomParam)
                            .collect(Collectors.joining("&")));

        }

        return builder.toString();
    }

}
