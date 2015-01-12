package mobi.nowtechnologies.server.persistence.domain.behavior;

/**
 * Created by zam on 12/9/2014.
 */
public enum BehaviorConfigType {
    DEFAULT, FREEMIUM;

    public boolean isDefault() {
        return DEFAULT == this;
    }
}