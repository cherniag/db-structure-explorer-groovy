package mobi.nowtechnologies.applicationtests.services.helper;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserDataCreator {

    public String generateDeviceUID() {
        return "DEVICE_UID_" + UUID.randomUUID().toString();
    }

    public String generateEmail() {
        return System.nanoTime() + ".email.user@ussr.net";
    }

    public String createUserToken(AccountCheckDTO accountCheck, String timestamp) {
        return Utils.createTimestampToken(accountCheck.userToken, timestamp);
    }

}
