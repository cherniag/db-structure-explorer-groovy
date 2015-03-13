package mobi.nowtechnologies.server.shared.enums;

/**
 * User: Titov Mykhaylo (titov) 13.08.13 14:28
 */
public enum ActionReason {
    USER_DOWNGRADED_TARIFF("User downgraded tariff"),
    VIDEO_AUDIO_FREE_TRIAL_ACTIVATION("User activated Video Audio Free Trial");

    final String description;

    ActionReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
