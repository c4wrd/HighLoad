package com.c4wrd.loadtester.configuration;

import com.typesafe.config.Config;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Configuration {

    public void LoadFrom(Config config);

}
