package mobi.nowtechnologies.server.transport.controller.googleplus;

public class GooglePlusTemplateCustomizerImpl extends AbstractGooglePlusTemplateCustomizerImpl {
    private String googlePlusUserId;
    private String googlePlusEmail;
    private String firstName;
    private String lastName;
    private String profileUrl;

    public GooglePlusTemplateCustomizerImpl(String googlePlusEmail, String googlePlusUserId, String firstName, String lastName, String profileUrl, String accessToken) {
        super(accessToken);
        this.googlePlusUserId = googlePlusUserId;
        this.googlePlusEmail = googlePlusEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileUrl = profileUrl;
    }

    @Override
    protected String provideResourceName() {
        return "googleplus/ok.json";
    }

    @Override
    protected String render(String body) {
        return String.format(body, lastName, profileUrl, googlePlusEmail, firstName, googlePlusUserId);
    }
}
