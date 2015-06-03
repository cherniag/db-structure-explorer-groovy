package mobi.nowtechnologies.server.transport.serviceconfig;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.transport.controller.AbstractControllerTestIT;
import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheck;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckRepository;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus;
import mobi.nowtechnologies.server.versioncheck.domain.VersionMessage;
import mobi.nowtechnologies.server.versioncheck.domain.VersionMessageRepository;

import static mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus.FORCED_UPDATE;
import static mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus.REVOKED;

import javax.annotation.Resource;

import org.springframework.http.MediaType;

import org.junit.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Author: Gennadii Cherniaiev Date: 6/3/2015
 */
public class ServiceConfigControllerIT extends AbstractControllerTestIT {
    private static final String MTV_TRACKS = "mtv-tracks";

    @Resource
    private VersionMessageRepository versionMessageRepository;
    @Resource
    private VersionCheckRepository versionCheckRepository;

    private DeviceType deviceType;

    @Before
    public void prepareTest() {
        deviceType = DeviceTypeCache.getAndroidDeviceType();

        VersionMessage versionMessage1 = versionMessageRepository.saveAndFlush(new VersionMessage("REVOKED.ANDROID.1.5.0", "http://play.google.com/new_community_app"));
        VersionMessage versionMessage2 = versionMessageRepository.saveAndFlush(new VersionMessage("FORCED_UPGRADE.ANDROID.1.6.0", "http://play.google.com/new_version_app"));

        VersionCheck versionCheck1 = new VersionCheck(deviceType, 10, versionMessage1, REVOKED, MTV_TRACKS, ClientVersion.from("1.5.0"), "image_revoked_1.5.0.jpg");
        VersionCheck versionCheck2 = new VersionCheck(deviceType, 10, versionMessage2, FORCED_UPDATE, MTV_TRACKS, ClientVersion.from("1.6.0"), "image_forced_1.6.0.jpg");

        versionCheckRepository.saveAndFlush(versionCheck1);
        versionCheckRepository.saveAndFlush(versionCheck2);
    }


    @Test
    public void serviceConfig_LatestVersion() throws Exception {
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "mtv1";

        mockMvc.perform(
            get("/" + communityUrl + "/" + apiVersion + "/SERVICE_CONFIG")
                .header("X-User-Agent", "mtv-tracks/1.5.0 (Android; mtv1)")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
               .andExpect(jsonPath("$.response.data[0].versionCheck").exists())
               .andExpect(jsonPath("$.response.data[0].versionCheck.status").value("REVOKED"))
               .andExpect(jsonPath("$.response.data[0].versionCheck.message").value("This preview version of MTV Trax has expired. Please get the latest"))
               .andExpect(jsonPath("$.response.data[0].versionCheck.link").value("http://play.google.com/new_community_app"))
               .andExpect(jsonPath("$.response.data[0].versionCheck.image").value("image_revoked_1.5.0.jpg"));

    }
}