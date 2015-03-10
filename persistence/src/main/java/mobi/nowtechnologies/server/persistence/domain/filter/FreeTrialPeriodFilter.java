package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilter;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
@Entity
@DiscriminatorValue("FreeTrialPeriodFilter")
public class FreeTrialPeriodFilter extends AbstractFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeTrialPeriodFilter.class);

    @ElementCollection(targetClass = Long.class)
    @CollectionTable(name = "tb_filter_params")
    private List<Long> activeSinceTrialStartTimestampMillis;

    @ElementCollection(targetClass = Long.class)
    @CollectionTable(name = "tb_filter_params")
    private List<Long> activeTillTrialEndTimestampMillis;

    @Override
    public boolean doFilter(User user, Object param) {
        LOGGER.debug("input parameters user, param: [{}], [{}]", user, param);

        boolean filtrate = false;

        Long sinceFreeTrialMillis = null;
        Long tillFreeTrialMillis = null;
        final Long freeTrialStartedTimestampMillis = user.getFreeTrialStartedTimestampMillis();
        if (freeTrialStartedTimestampMillis != null) {
            long currentTimeMillis = Utils.getEpochMillis();
            sinceFreeTrialMillis = currentTimeMillis - freeTrialStartedTimestampMillis;
            tillFreeTrialMillis = user.getFreeTrialExpiredMillis() - currentTimeMillis;

            for (int i = 0; i < activeSinceTrialStartTimestampMillis.size(); i++) {
                final Long activeSinceTrialStartTimestampMillisParam = activeSinceTrialStartTimestampMillis.get(i);
                final Long activeTillTrialEndTimestampMillisParam = activeTillTrialEndTimestampMillis.get(i);
                if (activeSinceTrialStartTimestampMillisParam == null || activeTillTrialEndTimestampMillisParam == null) {
                    continue;
                }
                if (sinceFreeTrialMillis >= activeSinceTrialStartTimestampMillisParam && tillFreeTrialMillis >= activeTillTrialEndTimestampMillisParam) {
                    filtrate = true;
                } else {
                    filtrate = false;
                    break;
                }
            }

        }

        LOGGER.info("Output parameter filtrate=[{}]", filtrate);
        return filtrate;
    }

}
