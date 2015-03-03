package mobi.nowtechnologies.server.httpinvoker;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;

import javax.annotation.Resource;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

public class VideoAudioFreeTrialUrlStrategy implements DataToUrlStrategy {

    @Resource
    private UserRepository userRepository;

    @Override
    @Transactional
    public String createUrl(String id) {
        int idValue = Integer.parseInt(id);

        User user = userRepository.findOne(idValue);

        Assert.notNull(user);

        return doCreate(user);
    }

    private String doCreate(User user) {
        String rewriteUrlParameter = user.getUserGroup().getCommunity().getRewriteUrlParameter();

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/service/xxxxx/" + rewriteUrlParameter + "/4.0/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL");

        final String timestamp = new Date().getTime() + "";

        builder.queryParam("APP_VERSION", "appVersion");
        builder.queryParam("USER_NAME", user.getUserName());
        builder.queryParam("USER_TOKEN", Utils.createTimestampToken(user.getToken(), timestamp));
        builder.queryParam("TIMESTAMP", timestamp);
        builder.queryParam("DEVICE_UID", user.getDeviceUID());

        return builder.build().toUriString();
    }
}
