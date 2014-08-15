package mobi.nowtechnologies.server.assembler.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener;
import org.apache.commons.net.util.Base64;
import org.modelmapper.internal.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Composes URLs following this format:
 * <p/>
 * 1)
 * <protocol>://<content>/<subtype>?id=<[ISRC | News Timestamp | News story Id | Playlist type Id]>
 * <p/>
 * 2)
 * <protocol>://[web | page]/<[page enum | base64 encoded URL]>?action=<ACTION>
 * <p/>
 * where <protocol> is "mq-app"
 */
public class DeepLinkUrlFactory {

    private final static String ACTION = "action";
    private final static String ID = "id";
    private static final String OPEN_IN = "openIn";

    private DeepLinkInfoService deepLinkInfoService;

    public List<Integer> create(ManualCompilationDeeplinkInfo deeplinkInfo) {
        return deeplinkInfo.getMediaIds();
    }

    public String create(DeeplinkInfo deeplinkInfo, String community) {
        Assert.isTrue(!(deeplinkInfo instanceof ManualCompilationDeeplinkInfo));

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.scheme(createScheme(community));
        uriComponentsBuilder.host(FeatureValueType.of(deeplinkInfo).getId());
        uriComponentsBuilder.pathSegment(ContentSubType.of(deepLinkInfoService.getSubType(deeplinkInfo)).getName());
        uriComponentsBuilder.pathSegment(decideSubValueForPromotional(deeplinkInfo));
        // query params if needed
        putActionOrOpenerQueryParamIfPromotional(deeplinkInfo, uriComponentsBuilder);
        putIdQueryParamIfNotPromotional(deeplinkInfo, uriComponentsBuilder);

        return uriComponentsBuilder.build().toUriString();
    }

    private String createScheme(String community) {
        return community.replace('_', '-');
    }

    private String decideSubValueForPromotional(DeeplinkInfo deeplinkInfo) {
        if (deeplinkInfo instanceof InformationDeeplinkInfo) {
            InformationDeeplinkInfo info = (InformationDeeplinkInfo) deeplinkInfo;

            if (LinkLocationType.EXTERNAL_AD == info.getLinkType()) {
                return Base64.encodeBase64String(info.getUrl().getBytes(), false);
            } else {
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
                        uriComponentsBuilder.queryParam(OPEN_IN, convertOpenerValue(i.getOpener()));
                    }
                    break;
            }
        }
    }

    private String convertOpenerValue(Opener opener) {
        switch (opener) {
            case BROWSER:
                return "browser";
            case IN_APP:
                return "app";
        }
        throw new RuntimeException("Opener is not valid");
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
            return info.getChartType().name();
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
