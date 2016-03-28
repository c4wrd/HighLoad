package com.c4wrd.loadtester.request;

public class RequestDetail {

    /**
     * The endpoint related to this test.
     */
    private String endpoint;

    /**
     * The query parameters associated with the request.
     */
    private String queryParameters;

    /**
     * The time the server took to respond
     * to our request
     */
    private int serverResponseTime;

    /**
     * This payload can be used for test-specific details
     * such as the Bottleneck's current interval for results
     */
    private Object payload;

    /**
     * The time this request was completed.
     */
    private Long requestTime;

    /**
     * If the RequstOptions specified to save the response,
     * this field will be accessible.
     */
    private String response;

    public RequestDetail(String endpoint) {
        sanitizeEndpoint(endpoint);
        this.requestTime = System.currentTimeMillis();
    }

    private void sanitizeEndpoint(String endpoint) {

        if (endpoint.indexOf('?') != -1) {
            this.endpoint = endpoint.substring(0, endpoint.indexOf('?'));
            this.queryParameters = endpoint.substring(endpoint.indexOf('?'), endpoint.length());
        } else {
            this.endpoint = endpoint;
            this.queryParameters = "None";
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Object getPayload() {
        return this.payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public int getServerResponseTime() {
        return serverResponseTime;
    }

    public void setServerResponseTime(int serverResponseTime) {
        this.serverResponseTime = serverResponseTime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getQueryParameters() {
        return queryParameters;
    }
}
