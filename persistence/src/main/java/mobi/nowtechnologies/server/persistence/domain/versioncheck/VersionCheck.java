package mobi.nowtechnologies.server.persistence.domain.versioncheck;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
@Entity
@Table(name = "client_version_info", uniqueConstraints = {@UniqueConstraint(columnNames = {"device_type_id", "community_id", "application_name", "status", "message_id"})})
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

    @Column(name="qualifier",columnDefinition="char(100)")
    private String qualifier;

    @JoinColumn(name = "message_id", nullable = false)
    @ManyToOne
    private VersionMessage message;

    @Enumerated(value=EnumType.STRING)
    @Column(name="status",columnDefinition="char(100)")
    private VersionCheckStatus status;

    @Column(name="application_name",columnDefinition="char(100)", nullable = false)
    private String applicationName;

    protected VersionCheck() {
    }

    public VersionCheck(DeviceType deviceType, Community community, VersionMessage message, VersionCheckStatus status, String applicationName, ClientVersion clientVersion) {
        this.deviceType = deviceType;
        this.community = community;
        this.message = message;
        this.status = status;
        this.applicationName = applicationName;

        this.majorNumber = clientVersion.major();
        this.minorNumber = clientVersion.minor();
        this.revisionNumber = clientVersion.revision();
        this.qualifier = clientVersion.qualifier();
    }

    public VersionCheckStatus getStatus() {
        return status;
    }

    public VersionMessage getMessage() {
        return message;
    }


    public static final String MAJOR_NUMBER_PROPERTY_NAME = "majorNumber";

    public static final String MINOR_NUMBER_PROPERTY_NAME = "minorNumber";

    public static final String REVISION_NUMBER_PROPERTY_NAME = "revisionNumber";


    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
