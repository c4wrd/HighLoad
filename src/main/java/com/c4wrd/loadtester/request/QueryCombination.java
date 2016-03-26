package com.c4wrd.loadtester.request;

import java.util.LinkedList;
import java.util.List;

public class QueryCombination {

    private List<QueryParam> queryParameterValues;

    public QueryCombination() {
        this.queryParameterValues = new LinkedList<>();
    }

    public void addQueryParameter(QueryParam parameter) {
        this.queryParameterValues.add(parameter);
    }

    public List<QueryParam> getQueryParameterValues() {
        return this.queryParameterValues;
    }

}
