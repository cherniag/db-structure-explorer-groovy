package mobi.nowtechnologies.server.persistence.domain.task;

import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import static javax.persistence.FetchType.EAGER;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: gch Date: 12/16/13
 */

@Entity
public abstract class UserTask extends Task {

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    protected UserTask(Date serverTime, User user) {
        super(serverTime);
        this.user = user;
    }

    protected UserTask() {
        super();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("user_id", user != null ?
                                                                                         user.getId() :
                                                                                         null).append("user_name", user != null ?
                                                                                                                   user.getUserName() :
                                                                                                                   null).append("user_mobile", user != null ?
                                                                                                                                               user.getMobile() :
                                                                                                                                               null).append("user_deviceUID", user != null ?
                                                                                                                                                                              user.getDeviceUID() :
                                                                                                                                                                              null).append("user_group",
                                                                                                                                                                                           user !=
                                                                                                                                                                                           null ?
                                                                                                                                                                                           user.getUserGroupId() :
                                                                                                                                                                                           null)
                                        .toString();
    }
}
