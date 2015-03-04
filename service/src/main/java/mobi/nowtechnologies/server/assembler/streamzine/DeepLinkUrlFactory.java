package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.InformationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsStoryDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.PlayableItemDeepLink;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;

import java.util.List;

import org.apache.commons.net.util.Base64;

import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Composes URLs following this format:
 * <p/>
 * 1) <protocol>://<content>/<subtype>?id=<[ISRC | News Timestamp | News story Id | Playlist type Id]>
 * <p/>
 * 2) <protocol>://[web | page]/<[page enum | base64 encoded URL]>?action=<ACTION>
 * <p/>
 * where <protocol> is "mq-app"
 */
public class DeepLinkUrlFactory {

    private final static String ACTION = "action";
    private final static String ID = "id";
    private final static String PLAYER = "player";
    private static final String OPEN_IN = "open";

    private DeepLinkInfoService deepLinkInfoService;

    public String createForChart(Community community, int chartId, String action) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.scheme(createScheme(community));
        uriComponentsBuilder.host(FeatureValueType.CONTENT.getId());
        uriComponentsBuilder.pathSegment(ContentSubType.PLAYLIST.getName());
        uriComponentsBuilder.queryParam(ID, chartId);
        if (action != null) {
            uriComponentsBuilder.queryParam(ACTION, action);
        }
        return uriComponentsBuilder.build().toUriString();
    }


    public List<Integer> create(ManualCompilationDeeplinkInfo deeplinkInfo) {
        return deeplinkInfo.getMediaIds();
    }

    public String create(DeeplinkInfo deeplinkInfo, Community community, boolean includePlayer) {
        Assert.isTrue(!(deeplinkInfo instanceof ManualCompilationDeeplinkInfo));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.scheme(createScheme(community));
        uriComponentsBuilder.host(FeatureValueType.of(deeplinkInfo).getId());
        uriComponentsBuilder.pathSegment(ContentSubType.of(deepLinkInfoService.getSubType(deeplinkInfo)).getName());
        uriComponentsBuilder.pathSegment(decideSubValueForPromotional(deeplinkInfo));
        // query params if needed
        putActionOrOpenerQueryParamIfPromotional(deeplinkInfo, uriComponentsBuilder);
        putPlayerQueryParamForPlayableItemDeepLink(deeplinkInfo, uriComponentsBuilder, includePlayer);
        putIdQueryParamIfNotPromotional(deeplinkInfo, uriComponentsBuilder);

        return uriComponentsBuilder.build().toUriString();
    }

    private String createScheme(Community community) {
        return community.getRewriteUrlParameter().replace('_', '-');
    }

    private String decideSubValueForPromotional(DeeplinkInfo deeplinkInfo) {
        if (deeplinkInfo instanceof InformationDeeplinkInfo) {
            InformationDeeplinkInfo info = (InformationDeeplinkInfo) deeplinkInfo;

            if (LinkLocationType.EXTERNAL_AD == info.getLinkType()) {
                return Base64.encodeBase64String(info.getUrl().getBytes(), false);
            }
            else {
                return info.getUrl();
            }
        }

        return "";
    }

    private void putIdQueryParamIfNotPromotional(DeeplinkInfo deeplinkInfo, UriComponentsBuilder uriComponentsBuilder) {
        if (!(deeplinkInfo instanceof InformationDeeplinkInfo)) {
            uriComponentsBuilder.queryParam(ID, decideContentValue(deeplinkInfo));
        }
    }

    private void putPlayerQueryParamForPlayableItemDeepLink(DeeplinkInfo deeplinkInfo, UriComponentsBuilder uriComponentsBuilder, boolean includePlayer) {
        if (deeplinkInfo instanceof PlayableItemDeepLink) {
            if (includePlayer) {
                uriComponentsBuilder.queryParam(PLAYER, ((PlayableItemDeepLink) deeplinkInfo).getPlayerType().getId());
            }
        }
    }

    private void putActionOrOpenerQueryParamIfPromotional(DeeplinkInfo deeplinkInfo, UriComponentsBuilder uriComponentsBuilder) {
        if (deeplinkInfo instanceof InformationDeeplinkInfo) {
            InformationDeeplinkInfo i = (InformationDeeplinkInfo) deeplinkInfo;
            switch (i.getLinkType()) {
                case INTERNAL_AD:
                    if (i.getAction() != null) {
                        uriComponentsBuilder.queryParam(ACTION, i.getAction());
                    }
                    break;
                case EXTERNAL_AD:
                    if (i.getOpener() != null) {
                        uriComponentsBuilder.queryParam(OPEN_IN, i.getOpener().getQueryParamValue());
                    }
                    break;
            }
        }
    }


    private String decideContentValue(DeeplinkInfo deeplinkInfo) {
        if (deeplinkInfo instanceof NewsListDeeplinkInfo) {
            NewsListDeeplinkInfo info = (NewsListDeeplinkInfo) deeplinkInfo;
            return String.valueOf(info.getPublishDate().getTime());
        }

        if (deeplinkInfo instanceof NewsStoryDeeplinkInfo) {
            NewsStoryDeeplinkInfo info = (NewsStoryDeeplinkInfo) deeplinkInfo;
            return String.valueOf(info.getMessage().getId());
        }

        if (deeplinkInfo instanceof MusicPlayListDeeplinkInfo) {
            MusicPlayListDeeplinkInfo info = (MusicPlayListDeeplinkInfo) deeplinkInfo;
            return info.getChartId().toString();
        }

        if (deeplinkInfo instanceof MusicTrackDeeplinkInfo) {
            MusicTrackDeeplinkInfo info = (MusicTrackDeeplinkInfo) deeplinkInfo;
            return info.getMedia().getIsrcTrackId();
        }

        throw new IllegalArgumentException("Not recognized deeplink info type: " + deeplinkInfo.getClass());
    }

    public void setDeepLinkInfoService(DeepLinkInfoService deepLinkInfoService) {
        this.deepLinkInfoService = deepLinkInfoService;
    }

}
