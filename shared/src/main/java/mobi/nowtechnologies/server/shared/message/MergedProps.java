/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;
import java.util.Properties;

// @author Titov Mykhaylo (titov) on 08.04.2015.
public interface MergedProps {
    Properties getProperties(Locale locale);
}
