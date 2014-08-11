package mobi.nowtechnologies.applicationtests.services.device.domain;

import javax.persistence.*;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/3/2014
 */
@Entity
@Table(name = "fat_currentPhone")
public class CurrentPhone {
    @Id
    @GeneratedValue
    @Column(name = "phone_suffix")
    private long phoneSuffix;

    @Override
    public String toString() {
        return "CurrentPhone{" +
                "phoneSuffix=" + phoneSuffix +
                '}';
    }

    public String getO2Phone(Integer phoneTypePrefix) {
        return String.format("+447%02d%07d", phoneTypePrefix, phoneSuffix);
    }
}
