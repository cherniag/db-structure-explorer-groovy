package mobi.nowtechnologies.server.trackrepo.controller;

import javax.annotation.Resource;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import org.junit.*;
import static org.junit.Assert.*;

// @author Alexander Kolpakov (akolpakov)
public class SignInControllerIT extends AbstractTrackRepoIT {

    @Resource
    SignInController fixture;

    @Test
    public void testLogin() throws Exception {
        Boolean result = fixture.login();

        assertNotNull(result);
        assertEquals(true, result.booleanValue());
    }

    @Test
    public void generateMd5Password_Admin$Admin_Successful() {

        String password = "admin";
        String salt = "admin";

        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        String encodedPass = passwordEncoder.encodePassword(password, salt);

        assertEquals("ceb4f32325eda6142bd65215f4c0f371", encodedPass);
    }

    @Test
    public void generateMd5Password_album$MQIph5ao2l_Successful() {

        String password = "MQIph5ao2l";
        String salt = "album";

        Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
        String encodedPass = passwordEncoder.encodePassword(password, salt);

        assertEquals("dd0d117d91ac00d7f6e83852ac454669", encodedPass);
    }
}