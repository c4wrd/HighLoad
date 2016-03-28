package com.c4wrd.loadtester.request;

import com.c4wrd.loadtester.util.RandomSelector;

import java.util.ArrayList;
import java.util.List;

public class QueryParameter {

    private String query_parameter;
    private List<String> values;
    private String alias;

    public QueryParameter(String query_parameter) {
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

    public void setDatabackingAlias(String alias) {
        this.alias = alias;
    }

    public String getRandomParam(QueryDataRow row) {

        // our value is stored in the data-set
        if (this.alias != null) {
            return query_parameter + "=" + row.getValueMap().get(this.alias);
        }

        // read from the values stored here
        String selectedValue = values.get(RandomSelector.nextInt(values.size()));
        return query_parameter + "=" + selectedValue;
    }
}
