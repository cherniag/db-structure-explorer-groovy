package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.service.versioncheck.ClientVersion;
import mobi.nowtechnologies.server.service.versioncheck.UserAgentRequest;
import org.springframework.beans.ConversionNotSupportedException;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentRequestEditor extends PropertyEditorSupport {
    private static Pattern pattern = Pattern.compile("(href)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?");

    @Override
    public String getAsText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Matcher hrefMatcher = pattern.matcher(text);
        boolean found = hrefMatcher.find();

        if(!found) {
            throw new ConversionNotSupportedException(text, UserAgentRequest.class, null);
        }

        // do some logic to parse and validation
        UserAgentRequestImpl request = new UserAgentRequestImpl();
        // request.applicationName =

        setValue(request);
    }

    private static class UserAgentRequestImpl implements UserAgentRequest {
        String applicationName;
        ClientVersionImpl version;
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

    private static class ClientVersionImpl implements ClientVersion {
        private int major;
        private int minor;
        private int revision;

        @Override
        public int major() {
            return major;
        }

        @Override
        public int minor() {
            return minor;
        }

        @Override
        public int revision() {
            return revision;
        }
    }
}
