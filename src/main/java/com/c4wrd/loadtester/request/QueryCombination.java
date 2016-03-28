package com.c4wrd.loadtester.request;

import java.util.LinkedList;
import java.util.List;

public class QueryCombination {

    private List<QueryParameter> queryParameterValues;

    public QueryCombination() {
        this.queryParameterValues = new LinkedList<>();
    }

    public void addQueryParameter(QueryParameter parameter) {
        this.queryParameterValues.add(parameter);
    }

    public List<QueryParameter> getQueryParameterValues() {
        return this.queryParameterValues;
    }

}
