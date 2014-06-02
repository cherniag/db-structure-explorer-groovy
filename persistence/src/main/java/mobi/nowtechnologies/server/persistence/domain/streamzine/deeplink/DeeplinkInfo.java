package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DeeplinkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    protected ContentType contentType;

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
