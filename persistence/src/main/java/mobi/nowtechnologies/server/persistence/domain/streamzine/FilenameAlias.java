package mobi.nowtechnologies.server.persistence.domain.streamzine;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(
        name = "sz_filename_alias",
        uniqueConstraints = @UniqueConstraint(name = "alias_with_domain", columnNames = {"name_alias", "domain"}))
public class FilenameAlias {
    public static enum Domain {
        ANY, HEY_LIST_BADGES
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "file_name", length = 1024, unique = true, nullable = false)
    private String fileName;

    @Column(name = "name_alias", length = 1024, nullable = false)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", length = 128, nullable = false)
    private Domain domain = Domain.ANY;

    @Temporal(TemporalType.DATE)
    @Column(name = "creation_date")
    private Date created = new Date();

    protected FilenameAlias() {
    }

    public FilenameAlias(String fileName, String alias) {
        this.fileName = fileName;
        this.alias = alias;
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
}
