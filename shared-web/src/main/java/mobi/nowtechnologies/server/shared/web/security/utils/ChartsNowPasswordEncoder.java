package mobi.nowtechnologies.server.shared.web.security.utils;

import mobi.nowtechnologies.server.shared.Utils;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ChartsNowPasswordEncoder extends Md5PasswordEncoder {

    @Override
    public String encodePassword(String rawPass, Object salt) {
        String storedToken = Utils.createStoredToken(salt.toString(), rawPass);
        return storedToken;
    }
}
