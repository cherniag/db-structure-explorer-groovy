/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.utils;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

// @author Titov Mykhaylo (titov) on 03.04.2015.
public class AppPropResourceConfigurer extends PropertyPlaceholderConfigurer{

    @Override
    public Properties mergeProperties() throws IOException {
        return super.mergeProperties();
    }
}
