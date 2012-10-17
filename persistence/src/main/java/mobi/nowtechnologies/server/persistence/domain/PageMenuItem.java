package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tb_page_menu_items database table.
 * 
 */
@Entity
@Table(name="tb_page_menu_items")
public class PageMenuItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private byte i;

	@Column(name="internalLink",columnDefinition="smallint(5) unsigned")
	private int internalLink;

	@Column(name="internalParams",columnDefinition="char(40)")
	private String internalParams;

	@Column(name="label",columnDefinition="char(40)")
	private String label;

	private byte menu;

	private byte position;

	private byte submenu;

    public PageMenuItem() {
    }

	public byte getI() {
		return this.i;
	}

	public void setI(byte i) {
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

	public byte getMenu() {
		return this.menu;
	}

	public void setMenu(byte menu) {
		this.menu = menu;
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public byte getSubmenu() {
		return this.submenu;
	}

	public void setSubmenu(byte submenu) {
		this.submenu = submenu;
	}

}