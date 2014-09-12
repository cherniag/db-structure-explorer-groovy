package mobi.nowtechnologies.server.persistence.domain.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
@Entity
@Table(name = "client_version_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"device_type_id", "community_id", "status"})})
public class VersionCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "device_type_id", nullable = false)
    @ManyToOne
    private DeviceType deviceType;

    @JoinColumn(name = "community_id", nullable = false)
    @ManyToOne
    private Community community;

    @Column(name="major_number", nullable = false)
    private int majorNumber;

    @Column(name="minor_number", nullable = false)
    private int minorNumber;

    @Column(name="revision_number", nullable = false)
    private int revisionNumber;

    @JoinColumn(name = "message_id", nullable = false)
    @ManyToOne
    private VersionMessage message;

    @Enumerated(value=EnumType.STRING)
    @Column(name="status",columnDefinition="char(100)")
    private VersionCheckStatus status;


    protected VersionCheck() {
    }

    public VersionCheckStatus getStatus() {
        return status;
    }

    public VersionMessage getMessage() {
        return message;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public Community getCommunity() {
        return community;
    }

    public int getMajorNumber() {
        return majorNumber;
    }

    public int getMinorNumber() {
        return minorNumber;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
