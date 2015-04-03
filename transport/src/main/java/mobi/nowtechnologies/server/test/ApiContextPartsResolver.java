/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.test;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;

import javax.servlet.http.HttpServletRequest;
/**
 * Created by zam on 4/2/2015.
 */
public interface ApiContextPartsResolver {

    /**
     * Resolves {@link Community} via given request.
     *
     * @param request the request to resolve {@link Community}
     * @return resolved community (or {@code null})
     */
    Community resolveCommunity(HttpServletRequest request);

    /**
     * Resolves {@link ClientVersion} via given request.
     *
     * @param request the request to resolve {@link ClientVersion}
     * @return resolved version (or {@code null})
     */
    ClientVersion resolveClientVersion(HttpServletRequest request);

}
