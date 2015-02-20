package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Anton Zemliankin
 */

@Entity
@Table(name = "pin_code")
public class PinCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "creation_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime = new Date();

    @Column(name = "entered", nullable = false)
    private boolean entered;

    protected PinCode() {
    }

    public PinCode(Integer userId, String code) {
        this.userId = userId;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public int getAttempts() {
        return attempts;
    }

    public void incAttempts() {
        attempts++;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public boolean isEntered() {
        return entered;
    }

    public void setEntered(boolean entered) {
        this.entered = entered;
    }

    @Override
    public String toString() {
        return "PinCode{" +
                "code='" + code + '\'' +
                ", userId=" + userId +
                ", creationTime=" + creationTime +
                ", attempts=" + attempts +
                ", entered=" + entered +
                '}';
    }
}
