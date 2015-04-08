/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

// @author Titov Mykhaylo (titov) on 07.04.2015.
public class Prop extends ReloadableResourceBundleMessageSource implements MergedProps{

    @Override
    public Properties getProperties(Locale locale) {
        return getMergedProperties(locale).getProperties();
    }
}
