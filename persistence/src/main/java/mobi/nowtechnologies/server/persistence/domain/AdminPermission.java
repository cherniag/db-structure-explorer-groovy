package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_adminPermissions database table.
 * 
 */
@Entity
@Table(name="tb_adminPermissions")
public class AdminPermission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	private int adminPermissionsLabelID;

	private int adminUserID;

	private int communityID;

    public AdminPermission() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getAdminPermissionsLabelID() {
		return this.adminPermissionsLabelID;
	}

	public void setAdminPermissionsLabelID(int adminPermissionsLabelID) {
		this.adminPermissionsLabelID = adminPermissionsLabelID;
	}

	public int getAdminUserID() {
		return this.adminUserID;
	}

	public void setAdminUserID(int adminUserID) {
		this.adminUserID = adminUserID;
	}

	public int getCommunityID() {
		return this.communityID;
	}

	public void setCommunityID(int communityID) {
		this.communityID = communityID;
	}

}