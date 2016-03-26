package com.c4wrd.loadtester.request;

import java.util.ArrayList;
import java.util.List;
import com.c4wrd.loadtester.util.RandomSelector;

public class QueryParam {

    private String query_parameter;
    private List<String> values;

    public QueryParam(String query_parameter) {
        this.query_parameter = query_parameter;
        this.values = new ArrayList<String>();
    }

    public void addValue(String value) {
        this.values.add(value);
    }

    public void addAll(List<String> value) {
        this.values.addAll(value);
    }

    public String getQueryParameter() {
        return query_parameter;
    }

    public List<String> getValues() {
        return values;
    }

    public String getRandomParam() {
        String selectedValue = values.get(RandomSelector.nextInt(values.size()));
        return query_parameter + "=" + selectedValue;
    }
}
