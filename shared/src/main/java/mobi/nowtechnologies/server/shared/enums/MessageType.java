package mobi.nowtechnologies.server.shared.enums;

import java.util.List;

import com.google.common.collect.Lists;

public enum MessageType {
    AD,
    NEWS,
    POPUP, NOTIFICATION, RICH_POPUP, LIMITED_BANNER, FREE_TRIAL_BANNER, SUBSCRIBED_BANNER;

    public static List<MessageType> getMessageTypes() {
        return Lists.newArrayList(NOTIFICATION, POPUP, RICH_POPUP, LIMITED_BANNER, FREE_TRIAL_BANNER, SUBSCRIBED_BANNER);
    }

    public static List<MessageType> getBannerTypes() {
        return Lists.newArrayList(LIMITED_BANNER, FREE_TRIAL_BANNER, SUBSCRIBED_BANNER);
    }
}
