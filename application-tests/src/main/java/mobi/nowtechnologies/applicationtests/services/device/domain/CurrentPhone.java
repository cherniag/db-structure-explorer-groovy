package mobi.nowtechnologies.applicationtests.services.device.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Author: Gennadii Cherniaiev Date: 7/3/2014
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

    public long getPhoneSuffix() {
        return phoneSuffix;
    }
}
