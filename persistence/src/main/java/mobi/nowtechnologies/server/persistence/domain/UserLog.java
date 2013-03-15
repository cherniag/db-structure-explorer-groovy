package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Objects;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLosStatus;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

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
    private UserLosStatus status;

    public UserLog(){/* 4hibernate*/}

    public UserLog(Integer userId, long last_update, UserLosStatus status) {
        this.userId = userId;
        this.last_update = last_update;
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public DateTime getLastUpdate() {
        return new DateTime(last_update);
    }

    public UserLosStatus getStatus() {
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
