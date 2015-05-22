/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.shared.message;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import java.util.Locale;
// @author Titov Mykhaylo (titov) on 07.04.2015.
public class PropLocale {

    public static Locale DEFAULT_LOCALE = new Locale("");
    private static final String DEFAULT_COMMUNITY_DELIMITER = "_";

    public Locale getCommunityLocale(String community, Locale locale) {
        Locale communityLocale = isNull(community) ? DEFAULT_LOCALE : new Locale(community);
        if (isNotNull(locale)) {
            communityLocale = new Locale(community + DEFAULT_COMMUNITY_DELIMITER + locale.getLanguage(), locale.getCountry(), locale.getVariant());
        }
        return communityLocale;
    }
}
