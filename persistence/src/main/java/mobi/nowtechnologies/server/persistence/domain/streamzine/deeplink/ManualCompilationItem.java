package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import mobi.nowtechnologies.server.persistence.domain.Media;

import javax.persistence.*;

@Entity
@Table(name = "sz_man_compilation_items")
public class ManualCompilationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "position")
    private int position;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "media_id")
    private Media media;

    @ManyToOne
    @JoinColumn(name = "man_compilation_id")
    private ManualCompilationDeeplinkInfo manualCompilationDeeplinkInfo;

    public ManualCompilationItem(ManualCompilationDeeplinkInfo manualCompilationDeeplinkInfo, Media media, int position) {
        this.media = media;
        this.manualCompilationDeeplinkInfo = manualCompilationDeeplinkInfo;
        this.position = position;
    }

    protected ManualCompilationItem() {
    }

    public int getPosition() {
        return position;
    }

    public Media getMedia() {
        return media;
    }
}
