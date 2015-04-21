/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.support.editor;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.support.UserAgent;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.util.Assert;

public class UserAgentPropertyEditor extends PropertyEditorSupport {

    private static final Pattern PATTERN = Pattern.compile("(.+)/(\\d{1,2}\\.\\d.*) \\((\\S+); (\\S+)\\)");

    private CommunityRepository communityRepository;

    public UserAgentPropertyEditor(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public String getAsText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.find()) {
            throw new ConversionNotSupportedException(text, UserAgent.class, null);
        }

        final String strApplicationName = matcher.group(1);
        final String strApplicationVersion = matcher.group(2);
        final String strPlatform = matcher.group(3);
        final String strCommunity = matcher.group(4);

        final DeviceType platform = toDeviceType(strPlatform.toUpperCase());
        Assert.notNull(platform, "Can not find device type by value: " + strPlatform);

        final Community community = toCommunity(strCommunity);
        Assert.notNull(community, "Can not find community by value: " + strCommunity);

        final ClientVersion version = ClientVersion.from(strApplicationVersion);
        Assert.notNull(version, "Can not find application version by value: " + strApplicationVersion);

        setValue(new UserAgent() {
            @Override
            public String getApplicationName() {
                return strApplicationName;
            }

            @Override
            public ClientVersion getVersion() {
                return version;
            }

            @Override
            public DeviceType getPlatform() {
                return platform;
            }

            @Override
            public Community getCommunity() {
                return community;
            }
        });
    }

    DeviceType toDeviceType(String deviceTypeString) {
        return DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(deviceTypeString);
    }

    Community toCommunity(String name) {
        return communityRepository.findByName(name);
    }
}
