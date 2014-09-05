package mobi.nowtechnologies.server.persistence.domain.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(
        name = "sz_badge_mapping",
        uniqueConstraints = @UniqueConstraint(name = "sz_badge_mapping_uk_c_res_fs", columnNames = {"community_id", "resolution_id", "filename_alias_id", "original_alias_id"}))
public class BadgeMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @OneToOne
    @JoinColumn(name = "resolution_id")
    private Resolution resolution;

    @OneToOne(orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "filename_alias_id")
    private FilenameAlias filenameAlias;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "original_alias_id", nullable = true)
    private FilenameAlias originalFilenameAlias;

    @Column(name = "uploaded")
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploaded = new Date();

    protected BadgeMapping() {
    }

    public static BadgeMapping general(Community community, FilenameAlias filenameAlias) {
        Assert.notNull(community);
        Assert.notNull(filenameAlias);

        BadgeMapping m = new BadgeMapping();
        m.community = community;
        m.filenameAlias = filenameAlias;
        m.originalFilenameAlias = filenameAlias;
        return m;
    }

    public static BadgeMapping specific(Resolution resolution, Community community, FilenameAlias originalFilenameAlias) {
        Assert.notNull(resolution);

        BadgeMapping specific = new BadgeMapping();
        specific.resolution = resolution;
        specific.community = community;
        specific.originalFilenameAlias = originalFilenameAlias;
        return specific;
    }

    public Community getCommunity() {
        return community;
    }

    public void setFilenameAlias(FilenameAlias filenameAlias) {
        this.filenameAlias = filenameAlias;
    }

    public FilenameAlias getOriginalFilenameAlias() {
        return originalFilenameAlias;
    }

    public FilenameAlias getFilenameAlias() {
        return filenameAlias;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BadgeMapping{" +
                "id=" + id +
                ", community=" + community.getRewriteUrlParameter() +
                ", resolution=" + resolution +
                ", uploaded=" + uploaded +
                '}';
    }
}
