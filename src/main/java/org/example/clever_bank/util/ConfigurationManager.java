package org.example.clever_bank.util;

import com.tvd12.properties.file.reader.BaseFileReader;

import java.util.Properties;

public class ConfigurationManager {

    private static final Properties properties = new BaseFileReader().read("config.yaml");

    private ConfigurationManager() {
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
