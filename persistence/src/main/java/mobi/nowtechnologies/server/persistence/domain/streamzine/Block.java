package mobi.nowtechnologies.server.persistence.domain.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.springframework.util.Assert;

import javax.persistence.*;

@Entity
@Table(name = "sz_block")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "sub_title", length = 255)
    private String subTitle;

    @Column(name = "cover_url", length = 1024)
    private String coverUrl;

    @Column(name = "badge_url", length = 1024)
    private String badgeUrl;

    @Column(name = "included")
    private boolean included;

    @Column(name = "expanded")
    private boolean expanded;

    @Column(name = "shape_type")
    @Enumerated(EnumType.STRING)
    private ShapeType shapeType;

    @OneToOne(mappedBy = "block")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private DeeplinkInfo deeplinkInfo;

    @OneToOne(mappedBy = "block")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private AccessPolicy accessPolicy;

    protected Block() {
    }

    public Block(int position, ShapeType shapeType, DeeplinkInfo deeplinkInfo) {
        Assert.isTrue(position >= 0);
        Assert.notNull(shapeType);
        Assert.notNull(deeplinkInfo);

        this.position = position;
        this.shapeType = shapeType;
        this.deeplinkInfo = deeplinkInfo;
    }

    public Block(Block block) {
        position = block.position;
        title = block.title;
        subTitle = block.subTitle;
        coverUrl = block.coverUrl;
        badgeUrl = block.badgeUrl;
        included = block.included;
        expanded = block.expanded;
        shapeType = block.shapeType;

        if(block.accessPolicy != null) {
            accessPolicy = block.accessPolicy.copy(this);
        }
        deeplinkInfo = block.deeplinkInfo.copy(this);
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void changePosition(int newPosition) {
        Assert.isTrue(newPosition >= 0);
        this.position = newPosition;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void include() {
        included = true;
    }

    public void exclude() {
        included = false;
    }

    public int getPosition() {
        return position;
    }

    public boolean isIncluded() {
        return included;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public DeeplinkInfo getDeeplinkInfo() {
        return deeplinkInfo;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;

        Block block = (Block) o;

        if (position != block.position) return false;

        return true;
    }

    public void setAccessPolicy(AccessPolicy accessPolicy) {
        this.accessPolicy = accessPolicy;
    }

    public AccessPolicy getAccessPolicy() {
        return accessPolicy;
    }

    @Override
    public int hashCode() {
        return position;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("position", position)
                .append("title", title)
                .append("subTitle", subTitle)
                .append("coverUrl", coverUrl)
                .append("included", included)
                .append("shapeType", shapeType)
                .toString();
    }
}
