package mobi.nowtechnologies.server.transport.context.dto;

/**
 * Created by zam on 12/15/2014.
 */
public enum ContentBehaviorType {

    ENABLED, DISABLED;

    public static ContentBehaviorType valueOf(boolean isOff) {
        return isOff ? DISABLED : ENABLED;
    }
}
