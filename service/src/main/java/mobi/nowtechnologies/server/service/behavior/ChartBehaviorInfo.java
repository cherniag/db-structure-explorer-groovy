package mobi.nowtechnologies.server.service.behavior;

import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;

import java.util.Date;

public class ChartBehaviorInfo implements Comparable<ChartBehaviorInfo> {
    Date validFrom;
    ChartBehaviorType chartBehaviorType;
    String lockedAction;
    boolean canBeUnlocked;
    UserStatusType userStatusType;
    private boolean wasUnLocked = false;

    ChartBehaviorInfo() {
    }

    void unlock() {
        lockedAction = null;
        canBeUnlocked = false;
        wasUnLocked = true;
    }

    boolean canBeUnlocked() {
        return canBeUnlocked;
    }

    boolean wasUnlocked() {
        return wasUnLocked;
    }

    void borrow(ChartBehaviorInfo other) {
        chartBehaviorType = other.chartBehaviorType;
        lockedAction = other.lockedAction;
        canBeUnlocked = other.canBeUnlocked;
        wasUnLocked = other.wasUnLocked;
        userStatusType = other.userStatusType;
    }

    //
    // Out of the package behave
    //
    public Date getValidFrom() {
        return validFrom;
    }

    public ChartBehaviorType getChartBehaviorType() {
        return chartBehaviorType;
    }

    public String getLockedAction() {
        return lockedAction;
    }

    @Override
    public int compareTo(ChartBehaviorInfo o) {
        return validFrom.compareTo(o.validFrom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChartBehaviorInfo)) return false;

        ChartBehaviorInfo that = (ChartBehaviorInfo) o;

        if (chartBehaviorType != that.chartBehaviorType) return false;
        if (lockedAction != null ? !lockedAction.equals(that.lockedAction) : that.lockedAction != null) return false;
        if (!validFrom.equals(that.validFrom)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = validFrom.hashCode();
        result = 31 * result + chartBehaviorType.hashCode();
        result = 31 * result + (lockedAction != null ? lockedAction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChartBehaviorInfo{" +
                "validFrom=" + validFrom +
                ", chartBehaviorType=" + chartBehaviorType +
                ", lockedAction='" + lockedAction + '\'' +
                ", userStatusType=" + userStatusType +
                '}';
    }
}
