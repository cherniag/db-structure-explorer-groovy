package mobi.nowtechnologies.server.transport.controller.googleplus;

public class GooglePlusTemplateCustomizerImpl extends AbstractGooglePlusTemplateCustomizerImpl {

    private String imageUrl;
    private String gender;
    private String birthday;
    private String primaryLocation;
    private String googlePlusUserId;
    private String googlePlusEmail;
    private String firstName;
    private String lastName;
    private String profileUrl;
    private String displayName;
    private String homePage;

    public GooglePlusTemplateCustomizerImpl(String googlePlusEmail, String googlePlusUserId, String firstName, String lastName, String profileUrl, String accessToken, String gender, String birthday,
                                            String primaryLocation, String displayName, String homePage) {
        super(accessToken);
        this.googlePlusUserId = googlePlusUserId;
        this.googlePlusEmail = googlePlusEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileUrl = profileUrl;
        this.gender = gender;
        this.birthday = birthday;
        this.primaryLocation = primaryLocation;
        this.displayName = displayName;
        this.homePage = homePage;
    }


    protected String provideResourceNameForGooglePlusResponse() {
        return "googleplus/okGooglePlus.json";
    }

    @Override
    protected String renderGooglePlusResponse(String body) {
        return String.format(body, displayName, firstName, lastName, homePage, gender, profileUrl, primaryLocation, birthday, googlePlusUserId, googlePlusEmail);
    }

}
