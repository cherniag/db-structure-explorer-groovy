package mobi.nowtechnologies.server.service.streamzine.asm;

import mobi.nowtechnologies.server.assembler.ArtistAsm;
import mobi.nowtechnologies.server.dto.streamzine.ChartListItemDto;
import mobi.nowtechnologies.server.dto.streamzine.MediaDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.service.ChartService;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class StreamzineAdminMediaAsm {

    private ChartService chartService;

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    //
    // API
    //
    public List<MediaDto> toMediaDtos(Collection<Media> medias) {
        List<MediaDto> dtos = new ArrayList<MediaDto>();
        for (Media media : medias) {
            dtos.add(toMediaDto(media));
        }
        return dtos;
    }

    public MediaDto toMediaDto(Media media) {
        MediaDto mediaDto = new MediaDto();
        mediaDto.setId(media.getI());
        mediaDto.setTitle(media.getTitle());
        mediaDto.setFileName(media.getImageFileSmall().getFilename());
        mediaDto.setIsrc(media.getIsrc());
        mediaDto.setTrackId(media.getIsrcTrackId());
        mediaDto.setArtistDto(ArtistAsm.toArtistDto(media.getArtist()));
        return mediaDto;
    }

    public ChartListItemDto toPlaylistDto(MusicPlayListDeeplinkInfo i, Community community) {
        List<ChartDetail> chartDetails = chartService.getChartsByCommunity(community.getRewriteUrlParameter(), null, null);

        for (ChartDetail chartDetail : chartDetails) {
            if (isNotNull(chartDetail.getI()) && chartDetail.getChart().getI().equals(i.getChartId())) {
                return toChartListItemDto(chartDetail);
            }
        }

        return null;
    }

    public List<ChartListItemDto> toChartListItemDtos(List<ChartDetail> chartsByCommunity) {
        List<ChartListItemDto> chartListItemDtos = Lists.newArrayList();
        for (ChartDetail chartDetail : chartsByCommunity) {
            chartListItemDtos.add(toChartListItemDto(chartDetail));
        }

        Collections.sort(chartListItemDtos);

        return chartListItemDtos;
    }

    private ChartListItemDto toChartListItemDto(ChartDetail chartDetail) {
        ChartListItemDto chartListItemDto = new ChartListItemDto();
        Chart chart = chartDetail.getChart();
        chartListItemDto.setName(chartDetail.getTitle() != null ?
                                 chartDetail.getTitle() :
                                 chart.getName());
        chartListItemDto.setSubtitle(chartDetail.getSubtitle());
        chartListItemDto.setImageFileName(chartDetail.getImageFileName());
        chartListItemDto.setTracksCount(chart.getNumTracks());
        chartListItemDto.setChartId(chart.getI());
        return chartListItemDto;
    }
}
