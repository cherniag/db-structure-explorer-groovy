package mobi.nowtechnologies.server.service.itunes;

import com.google.gson.Gson;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.ITunesInAppSubscriptionRequestDto;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.service.BasicResponse;
import mobi.nowtechnologies.server.shared.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITunesValidator {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Gson gson = new Gson();

    private PostService postService;
    private CommunityResourceBundleMessageSource messageSource;

    public BasicResponse validateInITunes(User user, String appStoreReceipt){
        String iTunesUrl = messageSource.getDecryptedMessage(user.getCommunityRewriteUrl(), "apple.inApp.iTunesUrl", null, null);
        String password = messageSource.getDecryptedMessage(user.getCommunityRewriteUrl(), "apple.inApp.password", null, null);

        ITunesInAppSubscriptionRequestDto requestDto = new ITunesInAppSubscriptionRequestDto(appStoreReceipt, password);
        String body = gson.toJson(requestDto);

        logger.info("Trying to validate in-app subscription using url [{}] with following params [{}]", iTunesUrl, body);
        return postService.sendHttpPost(iTunesUrl, body);
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
