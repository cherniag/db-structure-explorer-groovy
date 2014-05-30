package mobi.nowtechnologies.server.persistence.domain.streamzine;

import javax.persistence.*;

@Entity
@Table(name = "sz_filename_alias")
public class FilenameAlias {
    public static enum Domain {
        ANY, HEY_LIST
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
