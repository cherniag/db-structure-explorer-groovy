package mobi.nowtechnologies.server.apptests.facebook;

import mobi.nowtechnologies.server.service.social.facebook.FacebookTemplateProvider;
import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.facebook.api.*;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import javax.annotation.Resource;

public class AppTestFacebookTemplateProvider implements FacebookTemplateProvider {
    @Resource
    private AppTestDummyFacebookTokenComposer appTestDummyFacebookTokenComposer;

    @Override
    public FacebookTemplate provide(String facebookAccessToken) {
        final FacebookProfile facebookProfile = appTestDummyFacebookTokenComposer.parseToken(facebookAccessToken);

        return new FacebookTemplate() {
            @Override
            public UserOperations userOperations() {
                return new UserOperations() {
                    @Override
                    public FacebookProfile getUserProfile() {
                        if(facebookProfile instanceof FailureFacebookProfile) {
                            throw new MissingAuthorizationException("provider id");
                        }
                        return facebookProfile;
                    }

                    @Override
                    public FacebookProfile getUserProfile(String userId) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public byte[] getUserProfileImage() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public byte[] getUserProfileImage(String userId) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public byte[] getUserProfileImage(ImageType imageType) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public byte[] getUserProfileImage(String userId, ImageType imageType) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public PagedList<String> getUserPermissions() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public PagedList<Reference> search(String query) {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}
