/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.database.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import java.util.Properties;

/**
 * Created by enes on 3/31/15.
 */
public class DecryptableProperties extends Properties {

    private final static String PWD_SYS_PROP_NAME = "pwd";

    private EncryptableProperties properties;

    public DecryptableProperties() {
        String password = System.getProperty(PWD_SYS_PROP_NAME);

        if (password == null) {
            throw new IllegalStateException(
                    String.format("System property '%s' is expected but missing.", PWD_SYS_PROP_NAME)
            );
        }
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        properties = new EncryptableProperties(encryptor);
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public synchronized Object get(Object key) {
        return properties.get(key);
    }
}
