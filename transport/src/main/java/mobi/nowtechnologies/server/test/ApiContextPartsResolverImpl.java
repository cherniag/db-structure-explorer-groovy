/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.test;

import mobi.nowtechnologies.server.device.domain.Device;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;
/**
 * Created by zam on 4/2/2015.
 */
public class ApiContextPartsResolverImpl implements ApiContextPartsResolver {

    private static final int PATH_INDEX_COMMUNITY = 0;
    private static final int PATH_INDEX_API_VERSION = 1;
    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @Resource
    CommunityRepository communityRepository;

    Map<String, Community> communityName2CommunityCache;

    @Override
    public Community resolveCommunity(HttpServletRequest request) {
        try {
            String communityName = resolvePathSegment(request, PATH_INDEX_COMMUNITY);
            return communityName2CommunityCache.get(communityName);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public ClientVersion resolveClientVersion(HttpServletRequest request) {
        try {
            String apiVersion = resolvePathSegment(request, PATH_INDEX_API_VERSION);
            return ClientVersion.from(apiVersion);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Device resolveDevice(HttpServletRequest request) {
        return null;
    }

    public User resolveUser(HttpServletRequest request) {
        return null;
    }

    @PostConstruct
    void init() {
        List<Community> communityList = communityRepository.findAll();
        Map<String, Community> communityName2Community = new LinkedHashMap<>();
        for (Community community : communityList) {
            communityName2Community.put(community.getName(), community);
        }
        this.communityName2CommunityCache = Collections.unmodifiableMap(communityName2Community);
    }

    String resolvePathSegment(HttpServletRequest request, int segmentIndex) {
        String pathWithinServletMapping = URL_PATH_HELPER.getPathWithinServletMapping(request);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(pathWithinServletMapping);
        List<String> pathSegments = builder.build().getPathSegments();
        return pathSegments.size() >= segmentIndex ? pathSegments.get(segmentIndex) : null;
    }
}
