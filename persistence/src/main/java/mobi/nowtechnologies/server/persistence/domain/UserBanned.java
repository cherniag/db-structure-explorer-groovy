package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "user_banned")
public class UserBanned {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer userId;

    private long timestamp;

    private String description;

    private boolean giveAnyPromotion;

    public UserBanned(User user) {
        if (user == null)
            throw new IllegalArgumentException("user is null");

        this.user = user;
        this.userId = user.getId();
        this.timestamp = System.currentTimeMillis();
    }

    public UserBanned() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null)
            throw new IllegalArgumentException("user is null");

        this.user = user;
        this.userId = user.getId();
    }

    public Integer getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGiveAnyPromotion() {
        return giveAnyPromotion;
    }

    public void setGiveAnyPromotion(boolean giveAnyPromotion) {
        this.giveAnyPromotion = giveAnyPromotion;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("timestamp", timestamp)
                .append("description", description)
                .append("giveAnyPromotion", giveAnyPromotion)
                .toString();
    }


}
