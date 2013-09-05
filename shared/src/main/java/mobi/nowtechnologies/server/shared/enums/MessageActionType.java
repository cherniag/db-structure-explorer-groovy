package mobi.nowtechnologies.server.shared.enums;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public enum MessageActionType {
	SUBSCRIPTION_PAGE,
	OFFICIAL_TOP_40_PLAYLIST,
	JUST_IN_PLAYLIST,
	OUR_PLAYLIST,
	THE_4TH_PLAYLIST,
	A_SPECIFIC_TRACK,
	THE_NEWS_LIST,
	A_SPECIFIC_NEWS_STORY,
	THE_ACCOUNT_SCREEN,
	EXTERNAL_URL,
	MOBILE_WEB_PORTAL,
    VIP_PLAYLIST;

    public boolean isSpecificNewsStoryOrSpecificTrackOrExternalUrlOrMobileWebPortal() {
        return this.equals(A_SPECIFIC_NEWS_STORY) || this.equals(A_SPECIFIC_TRACK)
                || this.equals(EXTERNAL_URL) || this.equals(MOBILE_WEB_PORTAL);
    }
}
