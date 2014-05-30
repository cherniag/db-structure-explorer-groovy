package mobi.nowtechnologies.server.service.streamzine;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.domain.streamzine.RecognizedAction;
import mobi.nowtechnologies.server.dto.streamzine.HasVip;
import mobi.nowtechnologies.server.dto.streamzine.MusicType;
import mobi.nowtechnologies.server.dto.streamzine.NewsType;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
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
        String value = data.getValue() != null ? data.getValue().toString() : "";

        if (musicType == MusicType.PLAYLIST) {
            ChartType chartType = null;
            if (!value.isEmpty()) {
                chartType = ChartType.valueOf(value.trim());
            }
            return new MusicPlayListDeeplinkInfo(chartType);
        }

        if (musicType == MusicType.TRACK) {
            final String isrc = value.trim();
            Media media = null;
            if (!isEmpty(isrc)) {
                media = mediaRepository.getByIsrc(isrc);
                Assert.notNull(media, "Can not find media during restoring deep link info from isrc: " + isrc);
            }


            return new MusicTrackDeeplinkInfo(media);
        }

        if (musicType == MusicType.MANUAL_COMPILATION) {
            String mediaIdsAsString = value.trim();
            ManualCompilationData manualCompilationData = new ManualCompilationData(mediaIdsAsString);
            List<String> mediaIsrcs = manualCompilationData.getMediaIsrcs();

            List<Media> medias = getOrderedMediasByIsrc(mediaIsrcs);

            Assert.isTrue(medias.size() == mediaIsrcs.size(), "Can not find all medias from media ids: " + mediaIdsAsString);

            return new ManualCompilationDeeplinkInfo(medias);
        }

        throw new IllegalArgumentException("Not supported music type: " + musicType);
    }

    private List<Media> getOrderedMediasByIsrc(List<String> mediaIsrcs) {
        List<Media> medias = Lists.newArrayList();
        for (String mediaIsrc : mediaIsrcs) {
            Media media = mediaRepository.getByIsrc(mediaIsrc);
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

        NotificationDeeplinkInfo notificationDeeplinkInfo = new NotificationDeeplinkInfo(linkLocationType, applicationPageData.getUrl());
        if (!applicationPageData.getAction().isEmpty()) {
            notificationDeeplinkInfo.setAction(applicationPageData.getAction());
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
        private Set<String> mediaIsrcs = new LinkedHashSet<String>();

        public ManualCompilationData(Collection<String> mediaIsrcs) {
            this.mediaIsrcs.addAll(mediaIsrcs);
        }

        public ManualCompilationData(String mediaIdsAsString) {
            if (!isEmpty(mediaIdsAsString)) {
                for (String isrc : mediaIdsAsString.split(TOKEN)) {
                    mediaIsrcs.add(isrc);
                }
            }
        }

        public String toMediasString() {
            return Joiner.on(TOKEN).join(mediaIsrcs);
        }

        public List<String> getMediaIsrcs() {
            return Lists.newArrayList(mediaIsrcs);
        }


    }

    public static interface DeeplinkInfoData {
        ShapeType getShapeType();

        ContentType getContentType();

        String getKey();

        String getValue();
    }
}
