package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_menuItems database table.
 * 
 */
@Entity
@Table(name="tb_menuItems")
public class MenuItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@Column(name="internalLink",columnDefinition="smallint(5) unsigned")
	private int internalLink;

	private String internalParams;

	private String label;

	@Column(name="menu",columnDefinition="smallint(5) unsigned")
	private int menu;

	private byte position;

	@Column(name="submenu",columnDefinition="smallint(5) unsigned")
	private int submenu;

    public MenuItem() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getInternalLink() {
		return this.internalLink;
	}

	public void setInternalLink(int internalLink) {
		this.internalLink = internalLink;
	}

	public String getInternalParams() {
		return this.internalParams;
	}

	public void setInternalParams(String internalParams) {
		this.internalParams = internalParams;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getMenu() {
		return this.menu;
	}

	public void setMenu(int menu) {
		this.menu = menu;
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public int getSubmenu() {
		return this.submenu;
	}

	public void setSubmenu(int submenu) {
		this.submenu = submenu;
	}

}