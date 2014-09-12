package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.versioncheck.ClientVersion;
import mobi.nowtechnologies.server.service.versioncheck.UserAgentRequest;
import org.springframework.util.Assert;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentRequestEditor extends PropertyEditorSupport {
    private static Pattern pattern = Pattern.compile("(.+)/(\\d\\.\\d.*) \\((\\S+); (\\S+)\\)");

    private CommunityRepository communityRepository;

    public UserAgentRequestEditor(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    @Override
    public String getAsText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Matcher hrefMatcher = pattern.matcher(text);
        boolean found = hrefMatcher.find();

        if(!found) {
            throw new IllegalArgumentException("Did not match expected pattern value: " + text);
        }

        UserAgentRequestImpl request = new UserAgentRequestImpl();
        request.applicationName = hrefMatcher.group(1);
        request.version = restoreVersion(hrefMatcher.group(2));

        final String normilizedDeviceType = hrefMatcher.group(3).toUpperCase();
        request.platform = restoreDeviceType(normilizedDeviceType);
        Assert.notNull(request.platform, "Can not find device type by value: " + normilizedDeviceType);

        final String deviceTypeString = hrefMatcher.group(4);
        request.community = restoreCommunity(deviceTypeString);
        Assert.notNull(request.community, "Can not find community by name: " + deviceTypeString);

        setValue(request);
    }

    DeviceType restoreDeviceType(String deviceTypeString) {
        return DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(deviceTypeString);
    }

    private Community restoreCommunity(String communityName) {
        return communityRepository.findByName(communityName);
    }

    private ClientVersion restoreVersion(String versionString) {
        return ClientVersionImpl.from(versionString);
    }

    private static class UserAgentRequestImpl implements UserAgentRequest {
        String applicationName;
        ClientVersion version;
        DeviceType platform;
        Community community;

        @Override
        public String getApplicationName() {
            return applicationName;
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
    }

}
