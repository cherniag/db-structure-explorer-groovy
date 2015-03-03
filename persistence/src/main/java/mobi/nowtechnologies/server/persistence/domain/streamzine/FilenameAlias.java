package mobi.nowtechnologies.server.persistence.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import java.util.Date;

import org.apache.commons.io.FilenameUtils;

@Entity
@Table(
    name = "sz_filename_alias",
    uniqueConstraints = @UniqueConstraint(name = "alias_with_domain", columnNames = {"file_name", "domain"}))
public class FilenameAlias {

    public static final int NAME_ALIAS_MAX_LENGTH = 1024;
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

    public FilenameAlias(String fileName, String alias, Dimensions dim) {
        this(fileName, alias);
        this.width = dim.getWidth();
        this.height = dim.getHeight();
    }

    public long getId() {
        return id;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public FilenameAlias createSpecific(Resolution resolution, Dimensions newDimension) {
        String newFileName = createUniqueFileName(resolution, resolution.newResolution(newDimension));
        String newTitle = alias + " for " + newDimension.getInfo();
        return new FilenameAlias(newFileName, newTitle, newDimension).forDomain(domain);
    }

    private String createUniqueFileName(Resolution previous, Resolution newOne) {
        final String name = FilenameUtils.getBaseName(fileName);
        final String ext = FilenameUtils.getExtension(fileName);

        return name + "_o_" + previous.getSizeInfo() + "_a_" + newOne.getFullInfo() + "." + ext;
    }

    public static enum Domain {
        ANY, HEY_LIST_BADGES
    }
}
