package mobi.nowtechnologies.server.dto.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zam on 12/15/2014.
 */
public enum ContentBehaviorType {
    ENABLED(false), DISABLED(true);

    private boolean isOff;

    ContentBehaviorType(boolean isOff) {
        this.isOff = isOff;
    }

    public static ContentBehaviorType valueOf(boolean isOff) {
        return isOff ? DISABLED : ENABLED;
    }

    public boolean isOff(){
        return isOff;
    }
}
