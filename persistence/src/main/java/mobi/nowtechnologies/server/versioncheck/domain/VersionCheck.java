/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.versioncheck.domain;

import mobi.nowtechnologies.server.device.domain.DeviceType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
@Entity
@Table(name = "client_version_info",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"device_type_id", "community_id", "application_name", "status", "message_id"})})
public class VersionCheck {

    public static final String MAJOR_NUMBER_PROPERTY_NAME = "majorNumber";
    public static final String MINOR_NUMBER_PROPERTY_NAME = "minorNumber";
    public static final String REVISION_NUMBER_PROPERTY_NAME = "revisionNumber";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "device_type_id", nullable = false)
    @ManyToOne
    private DeviceType deviceType;
    @Column(name = "community_id", nullable = false)
    private int communityId;
    @Column(name = "major_number", nullable = false)
    private int majorNumber;
    @Column(name = "minor_number", nullable = false)
    private int minorNumber;
    @Column(name = "revision_number", nullable = false)
    private int revisionNumber;
    @Column(name = "qualifier", columnDefinition = "char(100)")
    private String qualifier;
    @JoinColumn(name = "message_id", nullable = false)
    @ManyToOne
    private VersionMessage message;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", columnDefinition = "char(100)")
    private VersionCheckStatus status;
    @Column(name = "application_name", columnDefinition = "char(100)", nullable = false)
    private String applicationName;
    @Column(name = "image_file_name", columnDefinition = "char(255)")
    private String imageFileName;

    protected VersionCheck() {
    }

    public VersionCheck(DeviceType deviceType, int communityId, VersionMessage message, VersionCheckStatus status, String applicationName, ClientVersion clientVersion, String imageFileName) {
        this.deviceType = deviceType;
        this.communityId = communityId;
        this.message = message;
        this.status = status;
        this.applicationName = applicationName;
        this.imageFileName = imageFileName;

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

    public String getImageFileName() {
        return imageFileName;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
