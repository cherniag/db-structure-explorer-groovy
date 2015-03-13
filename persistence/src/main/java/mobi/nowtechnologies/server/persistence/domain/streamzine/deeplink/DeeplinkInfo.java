package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DeeplinkInfo {

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    protected ContentType contentType;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    @OneToOne
    @JoinColumn(name = "block_id")
    private Block block;

    public DeeplinkInfo copy(Block block) {
        DeeplinkInfo commonCopy = provideInstance();
        commonCopy.contentType = contentType;
        commonCopy.block = block;
        return commonCopy;
    }

    protected abstract DeeplinkInfo provideInstance();

    public long getId() {
        return id;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
