package com.c4wrd.loadtester.request;

import java.util.LinkedList;
import java.util.List;

public class QueryCombination {

    private List<String> queryParameterValues;

    public QueryCombination() {
        this.queryParameterValues = new LinkedList<>();
    }

    public void addQueryParameter(String parameter) {
        this.queryParameterValues.add(parameter);
    }

    public List<String> getQueryParameterValues() {
        return this.queryParameterValues;
    }
}
