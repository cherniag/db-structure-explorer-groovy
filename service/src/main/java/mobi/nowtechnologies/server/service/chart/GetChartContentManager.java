package mobi.nowtechnologies.server.service.chart;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import java.util.Map;

/**
 * Created by Oleg Artomov on 7/7/2014.
 */
public interface GetChartContentManager {
    ChartSupportResult support(User user, Map<ChartType, Integer> chartGroups, ChartDetail chart);
}
