package com.c4wrd.loadtester.configuration;


import com.c4wrd.loadtester.exceptions.IncorrectTestConfigException;
import com.c4wrd.loadtester.exceptions.MissingConfigOptionException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class TestConfig {

    private int numThreads;
    private int interval;

    public TestConfig(Config config) throws MissingConfigOptionException, IncorrectTestConfigException {
        try {
            this.numThreads = config.getInt("threads");
        } catch (ConfigException.Missing e) {
            throw new MissingConfigOptionException("threads");
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("threads");
        }

        try {
            this.interval = config.getInt("interval");
        } catch (ConfigException.Missing e) {
            throw new MissingConfigOptionException("interval");
        } catch (ConfigException.WrongType e) {
            throw new IncorrectTestConfigException("interval");
        }
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
