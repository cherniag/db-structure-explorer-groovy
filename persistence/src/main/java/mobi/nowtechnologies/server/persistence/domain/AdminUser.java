package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_adminUsers database table.
 * 
 */
@Entity
@Table(name="tb_adminUsers")
public class AdminUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	private byte accessLevel;

	private int adminUserTypeID;

	private String firstName;

	private String ipAddress;

	private String lastName;

	private String lastUse;

	private String password;

	private String sessionID;

	private String userID;

    public AdminUser() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public byte getAccessLevel() {
		return this.accessLevel;
	}

	public void setAccessLevel(byte accessLevel) {
		this.accessLevel = accessLevel;
	}

	public int getAdminUserTypeID() {
		return this.adminUserTypeID;
	}

	public void setAdminUserTypeID(int adminUserTypeID) {
		this.adminUserTypeID = adminUserTypeID;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastUse() {
		return this.lastUse;
	}

	public void setLastUse(String lastUse) {
		this.lastUse = lastUse;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSessionID() {
		return this.sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getUserID() {
		return this.userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

}