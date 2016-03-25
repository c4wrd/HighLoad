package com.c4wrd.loadtester.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigurationLoader {

    public static void main(String[] args) {
        Config config = ConfigFactory.load();
                config.getConfig("endpoints")
                .getConfig("graph")
                .getAnyRefList("mq")
                .stream()
                .forEach(t -> System.out.println(t));
    }

}
