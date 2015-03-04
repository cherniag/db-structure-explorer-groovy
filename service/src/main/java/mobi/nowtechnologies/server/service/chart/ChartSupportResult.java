package mobi.nowtechnologies.server.service.chart;

/**
 * Created by Oleg Artomov on 7/7/2014.
 */
public class ChartSupportResult {

    private final boolean support;

    private final boolean switchable;

    public ChartSupportResult(boolean support, boolean switchable) {
        this.support = support;
        this.switchable = switchable;
    }

    public boolean isSupport() {
        return support;
    }

    public boolean isSwitchable() {
        return switchable;
    }

}
