package mobi.nowtechnologies.server.assembler.streamzine;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.RecognizedAction;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.HasVip;
import mobi.nowtechnologies.server.persistence.domain.streamzine.rules.DeeplinkInfoData;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.persistence.repository.MessageRepository;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.springframework.util.Assert;

import java.util.*;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class DeepLinkInfoService {
    private MediaRepository mediaRepository;

    private MessageRepository messageRepository;

    public void setMediaRepository(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    //
    // API
    //
    public Enum<?> getSubType(DeeplinkInfo info) {
        if(info instanceof MusicPlayListDeeplinkInfo) {
            return MusicType.PLAYLIST;
        }

        if(info instanceof MusicTrackDeeplinkInfo) {
            return MusicType.TRACK;
        }

        if(info instanceof ManualCompilationDeeplinkInfo) {
            return MusicType.MANUAL_COMPILATION;
        }

        if(info instanceof NewsListDeeplinkInfo) {
            return NewsType.LIST;
        }

        if(info instanceof NewsStoryDeeplinkInfo) {
            return NewsType.STORY;
        }

        if(info instanceof InformationDeeplinkInfo) {
            return ((InformationDeeplinkInfo) info).getLinkType();
        }

        throw new IllegalArgumentException("Not known info type: " + info);
    }

    public DeeplinkInfo create(DeeplinkInfoData data) {
        switch (data.getContentType()) {
            case NEWS:
                return createNewsDeeplinkInfo(data);
            case MUSIC:
                return createMusicDeeplinkInfo(data);
            case PROMOTIONAL:
                return createPromotionalType(data);
        }

        throw new IllegalArgumentException("Not supported content type: " + data.getContentType());
    }

    public <T extends DeeplinkInfoData & HasVip> AccessPolicy tryToHandleSecuredTile(T entry) {
        DeeplinkInfo deeplinkInfo = create(entry);

        if(deeplinkInfo instanceof NotificationDeeplinkInfo) {
            return handlePromotional((NotificationDeeplinkInfo) deeplinkInfo);
        }

        if(deeplinkInfo instanceof MusicPlayListDeeplinkInfo || deeplinkInfo instanceof MusicTrackDeeplinkInfo) {
            return handleMusic(entry.isVip());
        }

        return null;
    }

    private AccessPolicy handleMusic(boolean vip) {
        if(vip) {
            return AccessPolicy.enabledForVipOnly();
        }
        return null;
    }

    private AccessPolicy handlePromotional(NotificationDeeplinkInfo entry) {
        if(entry.getAction() != null) {
            RecognizedAction recongnized = RecognizedAction.recongnize(entry.getAction());
            if(RecognizedAction.SUBSCRIBE.equals(recongnized)) {
                return AccessPolicy.hiddenForSubscribed();
            }
        }
        return null;
    }


    //
    // Internals
    //
    //
    // Music
    //
    private DeeplinkInfo createMusicDeeplinkInfo(DeeplinkInfoData data) {
        String key = data.getKey();

        final MusicType musicType = MusicType.valueOf(key.trim());
        String value = data.getValue() != null ? data.getValue().trim() : "";

        if (musicType == MusicType.PLAYLIST) {
            ChartType chartType = null;
            if (!value.isEmpty()) {
                chartType = ChartType.valueOf(value);
            }
            return new MusicPlayListDeeplinkInfo(chartType);
        }

        if (musicType == MusicType.TRACK) {
            Media restored = null;
            if (!value.isEmpty()) {
                final int id = Integer.parseInt(value);
                restored = mediaRepository.findOne(id);
                Assert.notNull(restored, "Can not find media during restoring deep link info from id: " + id);
            }
            return new MusicTrackDeeplinkInfo(restored);
        }

        if (musicType == MusicType.MANUAL_COMPILATION) {
            List<Media> medias = new ArrayList<Media>();
            if (!value.isEmpty()) {
                ManualCompilationData manualCompilationData = new ManualCompilationData(value);
                List<Integer> mediaIds = manualCompilationData.getMediaIds();

                medias.addAll(getOrderedMediasById(mediaIds));
                Assert.isTrue(medias.size() == mediaIds.size(), "Can not find all medias from media ids: " + value);
            }

            return new ManualCompilationDeeplinkInfo(medias);
        }

        throw new IllegalArgumentException("Not supported music type: " + musicType);
    }

    private List<Media> getOrderedMediasById(List<Integer> mediaIds) {
        List<Media> medias = Lists.newArrayList();
        for (Integer mediaId : mediaIds) {
            Media media = mediaRepository.findOne(mediaId);
            if (media != null) {
                medias.add(media);
            }
        }
        return medias;
    }

    //
    // News
    //
    private DeeplinkInfo createNewsDeeplinkInfo(DeeplinkInfoData data) {
        NewsType newsType = NewsType.valueOf(data.getKey().trim());

        if (newsType == NewsType.LIST) {
            Date date = calculateDate(data);
            return new NewsListDeeplinkInfo(date);
        }

        if (newsType == NewsType.STORY) {
            Message message = calculateMessage(data);
            return new NewsStoryDeeplinkInfo(message);
        }

        throw new IllegalArgumentException("Not supported news type: " + newsType);
    }

    private Message calculateMessage(DeeplinkInfoData data) {
        if (data.getValue() != null && !isEmpty(data.getValue().toString())) {
            int id = Integer.parseInt(data.getValue().toString().trim());
            return messageRepository.findOne(id);
        }
        return  null;
    }

    private Date calculateDate(DeeplinkInfoData data) {
        if (data.getValue() != null && !isEmpty(data.getValue().toString())) {
            String timestampString = data.getValue().toString().trim();
            return new Date(Long.valueOf(timestampString));
        }
        return null;
    }

    //
    // Notifications
    //
    private DeeplinkInfo createPromotionalType(DeeplinkInfoData data) {
        LinkLocationType linkLocationType = LinkLocationType.valueOf(data.getKey().trim());

        Object dataValue = data.getValue() != null ? data.getValue() : "";
        ApplicationPageData applicationPageData = new ApplicationPageData(dataValue.toString().trim());

        NotificationDeeplinkInfo notificationDeeplinkInfo = null;
        switch (linkLocationType) {
            case INTERNAL_AD:
                notificationDeeplinkInfo = new NotificationDeeplinkInfo(linkLocationType, applicationPageData.getUrl());
                if (!applicationPageData.getAction().isEmpty()) {
                    notificationDeeplinkInfo.setAction(applicationPageData.getAction());
                }
                break;
            case EXTERNAL_AD:
                Opener opener = Opener.BROWSER;
                if (!applicationPageData.getAction().isEmpty()) {
                    opener = Opener.valueOf(applicationPageData.getAction());
                }
                notificationDeeplinkInfo = new NotificationDeeplinkInfo(linkLocationType, applicationPageData.getUrl(), opener);
                break;
        }
        return notificationDeeplinkInfo;
    }

    public static class ApplicationPageData {
        public static final String TOKEN = "#";

        private String[] rawValues;

        public ApplicationPageData(String urlAndAction) {
            this.rawValues = urlAndAction.split(TOKEN);
        }

        public ApplicationPageData(String url, String action) {
            this.rawValues = new String[]{url, action};

            Assert.isTrue(rawValues.length == 2);
        }

        public ApplicationPageData(String url, Opener opener) {
            this.rawValues = new String[]{url, opener.name()};
            Assert.isTrue(rawValues.length == 2);
        }

        public String getUrl() {
            return rawValues[0];
        }

        public String getAction() {
            if (rawValues.length == 2) {
                return rawValues[1];
            }
            return "";
        }

        public String toUrlAndAction() {
            if (getAction().isEmpty()) {
                return getUrl();
            } else {
                return getUrl() + "#" + getAction();
            }
        }
    }

    public static class ManualCompilationData {
        public static final String TOKEN = "#";
        private Set<Integer> mediaIds = new LinkedHashSet<Integer>();

        public ManualCompilationData(Collection<Integer> mediaIds) {
            this.mediaIds.addAll(mediaIds);
        }

        public ManualCompilationData(String mediaIdsAsString) {
            if (!isEmpty(mediaIdsAsString)) {
                for (String id : mediaIdsAsString.split(TOKEN)) {
                    mediaIds.add(Integer.valueOf(id));
                }
            }
        }

        public String toMediasString() {
            return Joiner.on(TOKEN).join(mediaIds);
        }

        public List<Integer> getMediaIds() {
            return Lists.newArrayList(mediaIds);
        }


    }

}
