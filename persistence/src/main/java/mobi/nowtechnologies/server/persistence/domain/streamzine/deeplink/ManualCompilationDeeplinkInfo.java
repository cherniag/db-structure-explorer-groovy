package mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Media;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sz_deeplink_man_compilation")
public class ManualCompilationDeeplinkInfo extends DeeplinkInfo {

    @OneToMany(mappedBy = "manualCompilationDeeplinkInfo",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("position")
    private List<ManualCompilationItem> manualCompilationItems = new ArrayList<ManualCompilationItem>();

    public ManualCompilationDeeplinkInfo(List<Media> medias) {
        toManualCompilationItems(medias);
        contentType = ContentType.MUSIC;
    }

    private void toManualCompilationItems(List<Media> medias) {
        int position = 0;
        for (Media media : medias) {
            manualCompilationItems.add(new ManualCompilationItem(this, media, position++));
        }
    }

    protected ManualCompilationDeeplinkInfo(){
    }

    public List<Media> getMedias() {
        List<Media> medias = Lists.newArrayList();
        for (ManualCompilationItem manualCompilationItem : manualCompilationItems) {
            medias.add(manualCompilationItem.getMedia());
        }
        return medias;
    }

    public List<Integer> getMediaIds() {
        List<Integer> mediaIds = Lists.newArrayList();
        for (ManualCompilationItem manualCompilationItem : manualCompilationItems) {
            mediaIds.add(manualCompilationItem.getMedia().getI());
        }
        return mediaIds;
    }

    public List<String> getMediaIsrc() {
        List<String> mediaIds = Lists.newArrayList();
        for (ManualCompilationItem manualCompilationItem : manualCompilationItems) {
            mediaIds.add(manualCompilationItem.getMedia().getIsrc());
        }
        return mediaIds;
    }



    @Override
    protected DeeplinkInfo provideInstance() {
        ManualCompilationDeeplinkInfo deeplinkInfo = new ManualCompilationDeeplinkInfo(this.getMedias());
        return deeplinkInfo;
    }
}
