package mobi.nowtechnologies.server.persistence.domain.streamzine;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(
        name = "sz_filename_alias",
        uniqueConstraints = @UniqueConstraint(name = "alias_with_domain", columnNames = {"file_name", "domain"}))
public class FilenameAlias {

    public static final int NAME_ALIAS_MAX_LENGTH = 1024;

    public long getId() {
        return id;
    }

    public static enum Domain {
        ANY, HEY_LIST_BADGES
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "file_name", length = 1024, unique = true, nullable = false)
    private String fileName;

    @Column(name = "name_alias", length = NAME_ALIAS_MAX_LENGTH, nullable = false)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", length = 128, nullable = false)
    private Domain domain = Domain.ANY;

    @Temporal(TemporalType.DATE)
    @Column(name = "creation_date")
    private Date created = new Date();

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    protected FilenameAlias() {
    }

    public FilenameAlias(String fileName, String alias) {
        this.fileName = fileName;
        this.alias = alias;
    }

    public FilenameAlias(String fileName, String alias, int width, int height) {
        this.fileName = fileName;
        this.alias = alias;
        this.width = width;
        this.height = height;
    }

    public void updateFrom(FilenameAlias from) {
        this.created = new Date();
        this.fileName = from.fileName;
        this.alias = from.alias;
        this.width = from.width;
        this.height = from.height;
    }

    public String getFileName() {
        return fileName;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public FilenameAlias forDomain(Domain domain) {
        this.domain = domain;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "FilenameAlias{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", alias='" + alias + '\'' +
                ", domain=" + domain +
                ", created=" + created +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
