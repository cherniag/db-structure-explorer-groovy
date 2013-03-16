package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Objects;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
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

    @Column(name = "user_id")
    private Integer userId;

    private long last_update;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char")
    private UserLogStatus status;

    private String description;

    public UserLog(){/* 4hibernate*/}

    public UserLog(UserLog oldLog, Integer userId, UserLogStatus status, String description) {
        if(oldLog != null)
            id = oldLog.getId();
        this.userId = userId;
        this.last_update = System.currentTimeMillis();
        this.status = status;
        this.description = Utils.substring(description, 255);
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public DateTime getLastUpdate() {
        return new DateTime(last_update);
    }

    public UserLogStatus getStatus() {
        return status;
    }

    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("userId", userId)
                .add("last_update", last_update)
                .add("status", status)
                .toString();
    }
}
