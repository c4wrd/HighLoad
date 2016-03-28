package com.c4wrd.loadtester.configuration;

import com.c4wrd.loadtester.exceptions.HighLoadException;
import com.c4wrd.loadtester.exceptions.IncorrectTestConfigException;
import com.c4wrd.loadtester.exceptions.MissingConfigOptionException;
import com.c4wrd.loadtester.testservice.RequestOptions;
import com.c4wrd.loadtester.testservice.RequestRunnable;
import com.c4wrd.loadtester.util.LogLevel;
import com.c4wrd.loadtester.util.Output;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HighLoadConfig {

    private String host;
    private String dataFile;
    private RequestOptions requestOptions;
    private TestType testType;
    private TestConfig testConfig;
    private List<String> endpoints;

    public HighLoadConfig(Config config) throws HighLoadException {

        Output.println(LogLevel.VERBOSE, " -> Setting up the HighLoad configuration file");

        try {
            this.host = config.getString("host");
        } catch (ConfigException.Missing e) {
            throw new MissingConfigOptionException("host");
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("host");
        }

        try {
            this.dataFile = config.getString("data_file");
            if (!Files.exists(Paths.get(this.dataFile))) {
                throw new HighLoadException(String.format("Failed to find the datafile %s", this.dataFile));
            }
        } catch (ConfigException.Missing e) {
            // a data file is not necessary, if determined later we need it,
            // exception will be thrown then.
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("data_file");
        }

        try {
            this.requestOptions = new RequestOptions(config.getConfig("request_config"));
            RequestRunnable.setRequestOptions(this.requestOptions);
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("request_config");
        }

        try {
            this.testType = TestType.fromString(config.getString("test_type"));
        } catch (ConfigException.Missing e) {
            throw new MissingConfigOptionException("test_type");
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("test_type");
        } catch (IllegalArgumentException e) {
            throw new HighLoadException(e.getMessage());
        }

        try {
            this.testConfig = new TestConfig(config.getConfig("test_config"));
        } catch (ConfigException.Missing e) {
            throw new MissingConfigOptionException("test_config");
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("test_config");
        }

        try {
            this.endpoints = config.getStringList("test_endpoints");
        } catch (ConfigException.Missing e) {
            throw new MissingConfigOptionException("test_endpoints");
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("test_endpoints");
        }

        Output.println(LogLevel.DEFAULT,
                "*************************************************\n" +
                "* Host:           %-30s\n" +
                "* Dataset:        %-30s\n" +
                "* Number Threads: %-30s\n" +
                "* Interval:       %-30s\n" +
                "* Endpoints:      %-30s\n" +
                "*************************************************",
                host,
                dataFile == null ? "Not set" : dataFile,
                String.valueOf(testConfig.getNumThreads()),
                String.valueOf(testConfig.getInterval()),
                endpoints.toString()
        );
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public TestType getTestType() {
        return testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public RequestOptions getRequestOptions() {
        return requestOptions;
    }

    public void setRequestOptions(RequestOptions requestOptions) {
        this.requestOptions = requestOptions;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public TestConfig getTestConfig() {
        return testConfig;
    }

    public void setTestConfig(TestConfig testConfig) {
        this.testConfig = testConfig;
    }
}
