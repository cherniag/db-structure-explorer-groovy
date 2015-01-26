package mobi.nowtechnologies.server.persistence.domain.behavior;

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

    public abstract boolean isTracksInfoSupported();
    public abstract boolean isTracksPlayDurationSupported();

}
