package mobi.nowtechnologies.server.transport.controller.facebook;

public class FacebookTemplateCustomizerImpl extends AbstractFacebookTemplateCustomizerImpl {

    private String facebookUserId;
    private String facebookEmail;
    private String firstName;
    private String lastName;
    private String userName;
    private String facebookLocation;

    public FacebookTemplateCustomizerImpl(String userName, String firstName, String lastName, String facebookUserId, String facebookEmail, String facebookLocation, String facebookToken) {
        super(facebookToken);

        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.facebookUserId = facebookUserId;
        this.facebookEmail = facebookEmail;
        this.facebookLocation = facebookLocation;
    }

    @Override
    protected String provideResourceName() {
        return "facebook/ok.json";
    }

    @Override
    protected String render(String body) {
        return String.format(body, facebookUserId, facebookEmail, firstName, lastName, userName, facebookLocation);
    }
}
