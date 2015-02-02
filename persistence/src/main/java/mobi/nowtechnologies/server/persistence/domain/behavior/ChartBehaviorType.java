package mobi.nowtechnologies.server.persistence.domain.behavior;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Created by zam on 12/9/2014.
 */
public enum ChartBehaviorType {
    PREVIEW {
        @Override
        public boolean isTracksInfoSupported() {
            return false;
        }

        @Override
        public boolean isTracksPlayDurationSupported() {
            return true;
        }
    }, SHUFFLED {
        @Override
        public boolean isTracksInfoSupported() {
            return true;
        }

        @Override
        public boolean isTracksPlayDurationSupported() {
            return false;
        }
    }, NORMAL {
        @Override
        public boolean isTracksInfoSupported() {
            return false;
        }

        @Override
        public boolean isTracksPlayDurationSupported() {
            return false;
        }
    };

    public static Collection<ChartBehaviorType> all() {
        return Lists.newArrayList(values());
    }

    public abstract boolean isTracksInfoSupported();
    public abstract boolean isTracksPlayDurationSupported();

}
