package mobi.nowtechnologies.server.persistence.domain;

/**
 * @author Titov Mykhaylo (titov)
 */
public class DrmPolicyFactory {

    public static DrmPolicy createDrmPolicy() {

        final DrmPolicy drmPolicy = new DrmPolicy();
        drmPolicy.setDrmValue(Byte.MIN_VALUE);
        drmPolicy.setName("name");

        return drmPolicy;
    }
}