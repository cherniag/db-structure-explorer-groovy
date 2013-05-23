package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Objects;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.shared.Utils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import javax.persistence.*;

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

    private long last_update;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private UserLogStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(25)")
    private UserLogType type;

    private String description;

    public UserLog(){/* 4hibernate*/}

    protected UserLog(UserLog oldLog, UserLogStatus status, UserLogType userLogType, String description) {
        if(oldLog != null)
            id = oldLog.getId();
        this.type = userLogType;
        this.last_update = System.currentTimeMillis();
        this.status = status;
        this.description = Utils.substring(description, 255);
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
        return user != null ? user.getId() : null;
    }

    public DateTime getLastUpdate() {
        return new DateTime(last_update);
    }
    
    public void setLastUpdateMillis(long last_update) {
		this.last_update = last_update;
	}

	public long getLastUpdateMillis() {
		return last_update;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserLogType getType() {
		return type;
	}

	public UserLogStatus getStatus() {
        return status;
    }

    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("userId", getUserId())
                .add("last_update", last_update)
                .add("status", status)
                .toString();
    }
}
