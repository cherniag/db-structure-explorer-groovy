package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_pages database table.
 * 
 */
@Entity
@Table(name="tb_pages")
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	private byte accessLevel;

	@Column(name="contentModule",columnDefinition="smallint(5) unsigned")
	private int contentModule;

	@Column(name="mainMenu",columnDefinition="smallint(5) unsigned")
	private int mainMenu;

	@Column(name="name",columnDefinition="char(40)")
	private String name;

	@Column(name="subMenu",columnDefinition="smallint(5) unsigned")
	private int subMenu;

	@Column(name="title",columnDefinition="char(40)")
	private String title;

	@Column(name="userMenu",columnDefinition="smallint(5) unsigned")
	private int userMenu;

    public Page() {
    }

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
		this.i = i;
	}

	public byte getAccessLevel() {
		return this.accessLevel;
	}

	public void setAccessLevel(byte accessLevel) {
		this.accessLevel = accessLevel;
	}

	public int getContentModule() {
		return this.contentModule;
	}

	public void setContentModule(int contentModule) {
		this.contentModule = contentModule;
	}

	public int getMainMenu() {
		return this.mainMenu;
	}

	public void setMainMenu(int mainMenu) {
		this.mainMenu = mainMenu;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSubMenu() {
		return this.subMenu;
	}

	public void setSubMenu(int subMenu) {
		this.subMenu = subMenu;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getUserMenu() {
		return this.userMenu;
	}

	public void setUserMenu(int userMenu) {
		this.userMenu = userMenu;
	}

}