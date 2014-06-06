package mobi.nowtechnologies.server.service.streamzine.asm;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.assembler.ArtistAsm;
import mobi.nowtechnologies.server.dto.streamzine.ChartListItemDto;
import mobi.nowtechnologies.server.dto.streamzine.MediaDto;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.shared.enums.ChartType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamzineAdminMediaAsm {
    private String streamzineCommunity;
    private ChartService chartService;

    public void setStreamzineCommunity(String streamzineCommunity) {
        this.streamzineCommunity = streamzineCommunity;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    //
    // API
    //
    public List<MediaDto> toMediaDtos(List<Media> medias) {
        List<MediaDto> dtos = new ArrayList<MediaDto>();
        for (Media media : medias) {
            dtos.add(toMediaDto(media));
        }
        return dtos;
    }

    public MediaDto toMediaDto(Media media) {
        MediaDto mediaDto = new MediaDto();
        mediaDto.setTitle(media.getTitle());
        mediaDto.setFileName(media.getImageFileSmall().getFilename());
        mediaDto.setIsrc(media.getIsrc());
        mediaDto.setArtistDto(ArtistAsm.toArtistDto(media.getArtist()));
        return mediaDto;
    }

    public ChartListItemDto toPlaylistDto(MusicPlayListDeeplinkInfo i) {
        final ChartType requiredChartType = i.getChartType();

        List<ChartDetail> chartDetails = chartService.getChartsByCommunity(streamzineCommunity, null, null);

        for (ChartListItemDto dto : toChartListItemDtos(chartDetails)) {
            if(dto.getChartType() == requiredChartType) {
                return dto;
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
        chartListItemDto.setName(chartDetail.getTitle() != null ? chartDetail.getTitle() : chart.getName());
        chartListItemDto.setSubtitle(chartDetail.getSubtitle());
        chartListItemDto.setImageFileName(chartDetail.getImageFileName());
        chartListItemDto.setTracksCount(chart.getNumTracks());
        chartListItemDto.setChartType(chartDetail.getChartType());
        return chartListItemDto;
    }
}