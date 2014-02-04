package mobi.nowtechnologies.server.persistence.domain;


import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * @author Titov Mykhaylo (titov)
 */
@MappedSuperclass
public abstract class UserDeviceDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "i")
	private int id;

	@Column(name = "userUID", insertable = false, updatable = false)
	private int userId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userUID")
	private User user;

	private String token;

	@Column(name = "usergroup", insertable = false, updatable = false)
	private int userGroupId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usergroup")
	private UserGroup userGroup;

	@Column(name = "nbUpdates")
	private int nbUpdates;

	private int status;
	
	@Column(name = "last_push_of_content_update_millis")
	private long lastPushOfContentUpdateMillis;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		userId = user.getId();
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroupId = userGroup.getId();
		this.userGroup = userGroup;
	}

	public int getNbUpdates() {
		return nbUpdates;
	}

	public void setNbUpdates(int nbUpdates) {
		this.nbUpdates = nbUpdates;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public int getUserGroupId() {
		return userGroupId;
	}

	public long getLastPushOfContentUpdateMillis() {
		return lastPushOfContentUpdateMillis;
	}

	public void setLastPushOfContentUpdateMillis(long lastPushOfContentUpdateMillis) {
		this.lastPushOfContentUpdateMillis = lastPushOfContentUpdateMillis;
	}

	public UserDeviceDetails withUser(User user){
        setUser(user);
        return this;
    }

    public UserDeviceDetails withToken(String token){
        setToken(token);
        return this;
    }

    public UserDeviceDetails withUserGroup(UserGroup userGroup){
        setUserGroup(userGroup);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("userId", userId)
                .append("id", id)
                .append("token", token)
                .append("userGroupId", userGroupId)
                .append("nbUpdates", nbUpdates)
                .append("status", status)
                .append("lastPushOfContentUpdateMillis", lastPushOfContentUpdateMillis)
                .toString();
    }
}
