package mobi.nowtechnologies.applicationtests.services.helper;

import mobi.nowtechnologies.server.shared.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UserDataCreator {
    private Random random = new Random(System.currentTimeMillis());

    public String generateDeviceUID() {
        String deviceUID = "DEVICE_UID_" + UUID.randomUUID().toString();
        return deviceUID.toLowerCase();
    }

    public String generateEmail() {
        return Math.abs(random.nextLong()) + ".email.user@ussr.net";
    }

    public TimestampTokenData createUserToken(String userToken) {
        final String tmstp = new Date().getTime() + "";

        TimestampTokenData data = new TimestampTokenData();
        data.timestamp = tmstp;
        data.timestampToken = Utils.createTimestampToken(userToken, tmstp);
        return data;
    }

    public static class TimestampTokenData {

        private String timestamp;
        private String timestampToken;

        public String getTimestamp() {
            return timestamp;
        }

        public String getTimestampToken() {
            return timestampToken;
        }
    }
}
