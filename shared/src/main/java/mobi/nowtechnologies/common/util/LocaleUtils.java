/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.common.util;

import java.util.Locale;

// @author Titov Mykhaylo (titov) on 07.04.2015.
public final class LocaleUtils {

    private static final Locale DEFAULT_LOCALE = new Locale("");

    private LocaleUtils() {
    }

    public static Locale buildLocale(String community, Locale locale) {
        Locale communityLocale = DEFAULT_LOCALE;
        if (community != null) {
            communityLocale = new Locale(community);
        }

        if (locale != null) {
            communityLocale = new Locale(String.format("%s_%s", community, locale.getLanguage()), locale.getCountry(), locale.getVariant());
        }
        return communityLocale;
    }

    public static Locale buildLocale(String community, String language, String country) {
        if (language == null) {
            return buildLocale(community, null);
        }

        Locale locale;
        if (country == null) {
            locale = new Locale(language);
        } else {
            locale = new Locale(language, country);
        }
        return buildLocale(community, locale);
    }
}
