package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

@Entity
@Table(name = "user_logs")
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "char(25)")
    private String phoneNumber;

    @Column(name = "last_update")
    private long logTimeMillis;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)", name = "status")
    private UserLogStatus userLogStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(25)", name = "type")
    private UserLogType userLogType;

    private String description;

    public UserLog() {/* 4hibernate*/}

    protected UserLog(UserLog oldLog, UserLogStatus userLogStatus, UserLogType userLogType, String description) {
        if (oldLog != null) {
            id = oldLog.getId();
        }
        this.userLogType = userLogType;
        this.logTimeMillis = System.currentTimeMillis();
        this.userLogStatus = userLogStatus;
        this.description = StringUtils.substring(description, 0, 255);
    }

    public UserLog(UserLog oldLog, String phoneNumber, UserLogStatus status, UserLogType userLogType, String description) {
        this(oldLog, status, userLogType, description);
        this.phoneNumber = phoneNumber;
    }

    public UserLog(UserLog oldLog, User user, UserLogStatus status, UserLogType userLogType, String description) {
        this(oldLog, status, userLogType, description);
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return user != null ?
               user.getId() :
               null;
    }

    public long getLastUpdateMillis() {
        return logTimeMillis;
    }

    public void setLastUpdateMillis(long last_update) {
        this.logTimeMillis = last_update;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserLogType getUserLogType() {
        return userLogType;
    }

    public UserLogStatus getUserLogStatus() {
        return userLogStatus;
    }

    public UserLog withUser(User user) {
        this.user = user;
        return this;
    }

    public UserLog withUserLogStatus(UserLogStatus userLogStatus) {
        this.userLogStatus = userLogStatus;
        return this;
    }

    public UserLog withUserLogType(UserLogType userLogType) {
        this.userLogType = userLogType;
        return this;
    }

    public UserLog withDescription(String description) {
        this.description = description;
        return this;
    }

    public UserLog withLogTimeMillis(long logTimeMillis) {
        this.logTimeMillis = logTimeMillis;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("userId", getUserId()).append("phoneNumber", phoneNumber).append("logTimeMillis", logTimeMillis).append("userLogStatus", userLogStatus)
                                        .append("userLogType", userLogType).append("description", description).toString();
    }
}
