package mobi.nowtechnologies.server.service.streamzine;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.assembler.ArtistAsm;
import mobi.nowtechnologies.server.dto.streamzine.*;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.util.*;

public class StreamzineUpdateAdminAsm {
    private MessageSource messageSource;
    private DeepLinkInfoService deepLinkInfoService;
    private UserRepository userRepository;
    private String streamzineCommunity;
    private ChartService chartService;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setDeepLinkInfoService(DeepLinkInfoService deepLinkInfoService) {
        this.deepLinkInfoService = deepLinkInfoService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setStreamzineCommunity(String streamzineCommunity) {
        this.streamzineCommunity = streamzineCommunity;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }

    //
    // API
    //
    public List<UpdateDto> convertMany(Collection<Update> list) {
        List<UpdateDto> dtos = new ArrayList<UpdateDto>();

        for (Update update : list) {
            dtos.add(convertOne(update));
        }

        return dtos;
    }

    public UpdateDto convertOneWithBlocksToIncoming(Update update) {
        UpdateDto updateDto = convertOne(update);
        updateDto.addAllBlocks(convertToOrdinalBlockDtos(update));
        return updateDto;
    }

    public UpdateDto convertOneWithBlocks(Update update) {
        UpdateDto updateDto = convertOne(update);
        List<BlockDto> blockDtos = filterNarrow(convertToOrdinalBlockDtos(update));
        updateDto.addAllBlocks(blockDtos);

        return updateDto;
    }

    public Update fromIncomingDto(UpdateIncomingDto dto, String community) {
        Update u = new Update(new Date(dto.getTimestamp()));

        for (OrdinalBlockDto blockDto : dto.getBlocks()) {
            Block block = restoreBlock(blockDto);

            u.addBlock(block);
        }

        if (!StringUtils.isEmpty(dto.getUserName())) {
            User user = userRepository.findOne(dto.getUserName(), community);
            u.setUser(user);
        }

        return u;
    }

    //
    // Internals
    //
    private Block restoreBlock(OrdinalBlockDto blockDto) {
        DeeplinkInfo deeplinkInfo = deepLinkInfoService.create(blockDto);

        Block block = new Block(blockDto.getPosition(), blockDto.getShapeType(), deeplinkInfo);
        block.setBadgeUrl(blockDto.getBadgeUrl());
        block.setCoverUrl(blockDto.getCoverUrl());
        block.setSubTitle(blockDto.getSubTitle());
        block.setTitle(blockDto.getTitle());
        block.changePosition(blockDto.getPosition());
        block.setExpanded(blockDto.isExpanded());

        if(blockDto.isIncluded()) {
            block.include();
        }

        AccessPolicy accessPolicy = deepLinkInfoService.tryToHandleSecuredTile(blockDto);
        if(accessPolicy != null) {
            block.setAccessPolicy(accessPolicy);
        }
        return block;
    }

    private List<BlockDto> filterNarrow(List<OrdinalBlockDto> blockDtos) {
        List<BlockDto> dtos = new ArrayList<BlockDto>();

        for (int i = 0; i < blockDtos.size(); i++) {
            OrdinalBlockDto blockDto = blockDtos.get(i);

            if(blockDto.getShapeType() == ShapeType.NARROW) {
                NarrowBlockDto dto = new NarrowBlockDto();
                dto.setShapeType(ShapeType.NARROW);
                dto.setFirst(blockDto);
                dto.setSecond(blockDtos.get(++i));
                dtos.add(dto);
            } else {
                dtos.add(blockDto);
            }
        }

        return dtos;
    }

    private List<OrdinalBlockDto> convertToOrdinalBlockDtos(Update update) {
        final int amount = update.getBlocks().size();

        List<OrdinalBlockDto> dtos = new ArrayList<OrdinalBlockDto>(amount);

        for(int index = 0; index < amount; index++) {
            final Block block = update.getBlocks().get(index);

            OrdinalBlockDto blockDto = new OrdinalBlockDto();
            addCommonProperties(block, blockDto);
            addCustomPropertiesForOrdinal(block, blockDto);

            dtos.add(blockDto);
        }

        Collections.sort(dtos, OrdinalBlockDto.COMPARATOR);

        return dtos;
    }

    private void addCustomPropertiesForOrdinal(Block block, OrdinalBlockDto blockDto) {
        blockDto.setTitle(block.getTitle());
        blockDto.setSubTitle(block.getSubTitle());
        blockDto.setContentType(block.getDeeplinkInfo().getContentType());
        blockDto.setCoverUrl(block.getCoverUrl());
        blockDto.setPosition(block.getPosition());
        blockDto.setIncluded(block.isIncluded());
        blockDto.setBadgeUrl(block.getBadgeUrl());
        blockDto.setExpanded(block.isExpanded());
        blockDto.setShapeType(block.getShapeType());
        blockDto.setVip(block.getAccessPolicy() != null && block.getAccessPolicy().isVipMediaContent());

        final DeeplinkInfo info = block.getDeeplinkInfo();
        if(info instanceof MusicPlayListDeeplinkInfo) {
            final MusicType playlist = MusicType.PLAYLIST;

            MusicPlayListDeeplinkInfo i = (MusicPlayListDeeplinkInfo) info;
            blockDto.setKey(playlist.name());
            if (i.getChartType() != null) {
                blockDto.setValue(i.getChartType().name());
            }
            blockDto.setData(toPlaylistDto(i));
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, playlist));
        }

        if(info instanceof MusicTrackDeeplinkInfo) {
            final MusicType track = MusicType.TRACK;

            MusicTrackDeeplinkInfo i = (MusicTrackDeeplinkInfo) info;
            blockDto.setKey(track.name());
            Media media = i.getMedia();
            if (media != null) {
                blockDto.setValue(media.getIsrc());
                blockDto.setData(toMediaDto(i.getMedia()));
            }
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, track));
        }

        if(info instanceof ManualCompilationDeeplinkInfo) {
            final MusicType musicType = MusicType.MANUAL_COMPILATION;

            ManualCompilationDeeplinkInfo i = (ManualCompilationDeeplinkInfo) info;
            DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(i.getMediaIsrc());
            blockDto.setValue(manualCompilationData.toMediasString());
            blockDto.setData(toMediaDtos(i.getMedias()));
            blockDto.setKey(musicType.name());
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, musicType));
        }

        if(info instanceof InformationDeeplinkInfo) {
            InformationDeeplinkInfo i = (InformationDeeplinkInfo) info;

            final LinkLocationType linkType = i.getLinkType();
            DeepLinkInfoService.ApplicationPageData applicationPageData = createApplicationPageData(i);

            blockDto.setKey(linkType.name());
            blockDto.setValue(applicationPageData.toUrlAndAction());
            blockDto.setContentTypeTitle(getMessage(ContentType.PROMOTIONAL, linkType));
        }

        if(info instanceof NewsStoryDeeplinkInfo) {
            final NewsType list = NewsType.STORY;

            NewsStoryDeeplinkInfo i = (NewsStoryDeeplinkInfo) info;
            blockDto.setKey(list.name());
            blockDto.setValue((i.getMessage() == null) ? null : i.getMessage().getId().toString());
            blockDto.setContentTypeTitle(getMessage(ContentType.NEWS, list));
        }

        if(info instanceof NewsListDeeplinkInfo) {
            final NewsType story = NewsType.LIST;

            NewsListDeeplinkInfo i = (NewsListDeeplinkInfo) info;
            blockDto.setKey(story.name());
            if (i.getPublishDate() != null) {
                blockDto.setValue("" + i.getPublishDate().getTime());
            }
            blockDto.setContentTypeTitle(getMessage(ContentType.NEWS, story));
        }
    }

    private DeepLinkInfoService.ApplicationPageData createApplicationPageData(InformationDeeplinkInfo i) {
        final String action = i.getAction();

        if(action == null) {
            return new DeepLinkInfoService.ApplicationPageData(i.getUrl());
        } else {
            return new DeepLinkInfoService.ApplicationPageData(i.getUrl(), action);
        }
    }

    private void addCommonProperties(Block block, BlockDto dto) {
        dto.setIncluded(block.isIncluded());
        dto.setShapeType(block.getShapeType());
    }

    private UpdateDto convertOne(Update update) {
        UpdateDto dto = new UpdateDto();
        dto.setCanEdit(update.canEdit());
        dto.setId(update.getId());
        dto.setDate(update.getDate());

        if(update.getUser() != null) {
            dto.setUserName(update.getUser().getUserName());
        }
        return dto;
    }

    private <E0 extends Enum<E0>, E extends Enum<E>> String getMessage(E0 prefix, E suffix) {
        return messageSource.getMessage("streamzine.contenttype." + prefix.name() + "." + suffix.name(), null, LocaleContextHolder.getLocale());
    }

    public List<MediaDto> toMediaDtos(List<Media> medias) {
        List<MediaDto> dtos = new ArrayList<MediaDto>();
        for (Media media : medias) {
            dtos.add(toMediaDto(media));
        }
        return dtos;
    }

    private MediaDto toMediaDto(Media media) {
        MediaDto mediaDto = new MediaDto();
        mediaDto.setTitle(media.getTitle());
        mediaDto.setFileName(media.getImageFileSmall().getFilename());
        mediaDto.setIsrc(media.getIsrc());
        mediaDto.setArtistDto(ArtistAsm.toArtistDto(media.getArtist()));
        return mediaDto;
    }

    private ChartListItemDto toPlaylistDto(MusicPlayListDeeplinkInfo i) {
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

    public List<UserDto> toUserDtos(List<User> users) {
        List<UserDto> userDtos =  new ArrayList<UserDto>();
        for (User user : users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUserName(user.getUserName());
            userDtos.add(userDto);
        }
        return userDtos;
    }
}
