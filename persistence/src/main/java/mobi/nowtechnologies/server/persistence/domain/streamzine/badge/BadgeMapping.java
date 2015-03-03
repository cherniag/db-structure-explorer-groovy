package mobi.nowtechnologies.server.persistence.domain.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import org.springframework.util.Assert;

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

    @JoinColumn(name = "hidden")
    private Boolean hidden;

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

    public FilenameAlias getOriginalFilenameAlias() {
        return originalFilenameAlias;
    }

    public FilenameAlias getFilenameAlias() {
        return filenameAlias;
    }

    public void setFilenameAlias(FilenameAlias filenameAlias) {
        this.filenameAlias = filenameAlias;
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
               ", hidden=" + BooleanUtils.isTrue(hidden) +
               ", uploaded=" + uploaded +
               '}';
    }

    public void hide() {
        hidden = true;
    }
}
