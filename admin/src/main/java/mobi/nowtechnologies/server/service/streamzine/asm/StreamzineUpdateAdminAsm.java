package mobi.nowtechnologies.server.service.streamzine.asm;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.PlaylistData;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService.TrackData;
import mobi.nowtechnologies.server.dto.streamzine.BlockDto;
import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.dto.streamzine.NarrowBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.InformationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicPlayListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsListDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NewsStoryDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

public class StreamzineUpdateAdminAsm {

    private MessageSource messageSource;
    private DeepLinkInfoService deepLinkInfoService;
    private UserRepository userRepository;
    private CommunityRepository communityRepository;
    private StreamzineAdminMediaAsm streamzineAdminMediaAsm;
    private FilenameAliasRepository filenameAliasRepository;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setDeepLinkInfoService(DeepLinkInfoService deepLinkInfoService) {
        this.deepLinkInfoService = deepLinkInfoService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setStreamzineAdminMediaAsm(StreamzineAdminMediaAsm streamzineAdminMediaAsm) {
        this.streamzineAdminMediaAsm = streamzineAdminMediaAsm;
    }

    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public void setFilenameAliasRepository(FilenameAliasRepository filenameAliasRepository) {
        this.filenameAliasRepository = filenameAliasRepository;
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

    public UpdateDto convertOneWithBlocksToIncoming(Update update, Community community) {
        UpdateDto updateDto = convertOne(update);
        updateDto.addAllBlocks(convertToOrdinalBlockDtos(update, community));
        return updateDto;
    }

    public UpdateDto convertOneWithBlocks(Update update, Community community) {
        UpdateDto updateDto = convertOne(update);
        List<BlockDto> blockDtos = filterNarrow(convertToOrdinalBlockDtos(update, community));
        updateDto.addAllBlocks(blockDtos);

        return updateDto;
    }

    @Transactional
    public Update fromIncomingDto(UpdateIncomingDto dto, String community) {
        Community c = communityRepository.findByRewriteUrlParameter(community);

        Update u = new Update(new Date(dto.getTimestamp()), c);

        for (OrdinalBlockDto blockDto : dto.getBlocks()) {
            Block block = restoreBlock(blockDto);

            u.addBlock(block);
        }

        for (String userName : dto.getUserNames()) {
            User user = userRepository.findByUserNameAndCommunityUrl(userName, community);
            u.addUser(user);
        }

        return u;
    }

    public List<UserDto> toUserDtos(List<User> users) {
        List<UserDto> userDtos = new ArrayList<UserDto>();
        for (User user : users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUserName(user.getUserName());
            userDtos.add(userDto);
        }
        return userDtos;
    }

    //
    // Internals
    //
    private Block restoreBlock(OrdinalBlockDto blockDto) {
        DeeplinkInfo deeplinkInfo = deepLinkInfoService.create(blockDto);

        Block block = new Block(blockDto.getPosition(), blockDto.getShapeType(), deeplinkInfo);
        block.setBadgeId(blockDto.getBadgeId());
        block.setCoverUrl(blockDto.getCoverUrl());
        block.setSubTitle(blockDto.getSubTitle());
        block.setTitle(blockDto.getTitle());
        block.changePosition(blockDto.getPosition());
        block.setExpanded(blockDto.isExpanded());

        if (blockDto.isIncluded()) {
            block.include();
        }

        AccessPolicy accessPolicy = deepLinkInfoService.tryToHandleSecuredTile(blockDto);
        if (accessPolicy != null) {
            block.setAccessPolicy(accessPolicy);
        }
        return block;
    }

    private List<BlockDto> filterNarrow(List<OrdinalBlockDto> blockDtos) {
        List<BlockDto> dtos = new ArrayList<BlockDto>();

        for (int i = 0; i < blockDtos.size(); i++) {
            OrdinalBlockDto blockDto = blockDtos.get(i);

            if (blockDto.getShapeType() == ShapeType.NARROW) {
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

    private List<OrdinalBlockDto> convertToOrdinalBlockDtos(Update update, Community community) {
        final int amount = update.getBlocks().size();

        List<OrdinalBlockDto> dtos = new ArrayList<OrdinalBlockDto>(amount);

        for (int index = 0; index < amount; index++) {
            final Block block = update.getBlocks().get(index);

            OrdinalBlockDto blockDto = new OrdinalBlockDto();
            addCommonProperties(block, blockDto);
            addCustomPropertiesForOrdinal(block, blockDto, community);

            dtos.add(blockDto);
        }

        Collections.sort(dtos, OrdinalBlockDto.COMPARATOR);

        return dtos;
    }

    private void addCustomPropertiesForOrdinal(Block block, OrdinalBlockDto blockDto, Community community) {
        blockDto.setTitle(block.getTitle());
        blockDto.setSubTitle(block.getSubTitle());
        blockDto.setContentType(block.getDeeplinkInfo().getContentType());
        blockDto.setCoverUrl(block.getCoverUrl());
        blockDto.setPosition(block.getPosition());
        blockDto.setIncluded(block.isIncluded());
        blockDto.setBadgeFileNameAlias(getBadgeFilenameDto(block.getBadgeId()));
        blockDto.setExpanded(block.isExpanded());
        blockDto.setShapeType(block.getShapeType());
        blockDto.setVip(block.getAccessPolicy() != null && block.getAccessPolicy().isVipMediaContent());

        final DeeplinkInfo info = block.getDeeplinkInfo();
        if (info instanceof MusicPlayListDeeplinkInfo) {
            final MusicType playlist = MusicType.PLAYLIST;

            MusicPlayListDeeplinkInfo musicPlayListDeeplinkInfo = (MusicPlayListDeeplinkInfo) info;
            blockDto.setKey(playlist.name());
            PlaylistData playlistData = new PlaylistData(musicPlayListDeeplinkInfo.getChartId(), musicPlayListDeeplinkInfo.getPlayerType());
            blockDto.setValue(playlistData.toValueString());
            blockDto.setData(streamzineAdminMediaAsm.toPlaylistDto(musicPlayListDeeplinkInfo, community));
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, playlist));
        }

        if (info instanceof MusicTrackDeeplinkInfo) {
            final MusicType track = MusicType.TRACK;

            MusicTrackDeeplinkInfo musicTrackDeeplinkInfo = (MusicTrackDeeplinkInfo) info;
            blockDto.setKey(track.name());
            Media media = musicTrackDeeplinkInfo.getMedia();
            if (isNotNull(media)) {
                blockDto.setData(streamzineAdminMediaAsm.toMediaDto(media));
            }
            blockDto.setValue(new TrackData(media, musicTrackDeeplinkInfo.getPlayerType()).toValueString());
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, track));
        }

        if (info instanceof ManualCompilationDeeplinkInfo) {
            final MusicType musicType = MusicType.MANUAL_COMPILATION;

            ManualCompilationDeeplinkInfo i = (ManualCompilationDeeplinkInfo) info;
            DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(i.getMediaIds());
            blockDto.setValue(manualCompilationData.toMediasString());
            blockDto.setData(streamzineAdminMediaAsm.toMediaDtos(i.getMedias()));
            blockDto.setKey(musicType.name());
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, musicType));
        }

        if (info instanceof InformationDeeplinkInfo) {
            InformationDeeplinkInfo i = (InformationDeeplinkInfo) info;

            final LinkLocationType linkType = i.getLinkType();
            DeepLinkInfoService.ApplicationPageData applicationPageData = createApplicationPageData(i);

            blockDto.setKey(linkType.name());
            blockDto.setValue(applicationPageData.toUrlAndAction());
            blockDto.setContentTypeTitle(getMessage(ContentType.PROMOTIONAL, linkType));
        }

        if (info instanceof NewsStoryDeeplinkInfo) {
            final NewsType list = NewsType.STORY;

            NewsStoryDeeplinkInfo i = (NewsStoryDeeplinkInfo) info;
            blockDto.setKey(list.name());
            blockDto.setValue((i.getMessage() == null) ?
                              null :
                              i.getMessage().getId().toString());
            blockDto.setContentTypeTitle(getMessage(ContentType.NEWS, list));
        }

        if (info instanceof NewsListDeeplinkInfo) {
            final NewsType story = NewsType.LIST;

            NewsListDeeplinkInfo i = (NewsListDeeplinkInfo) info;
            blockDto.setKey(story.name());
            if (i.getPublishDate() != null) {
                blockDto.setValue("" + i.getPublishDate().getTime());
            }
            blockDto.setContentTypeTitle(getMessage(ContentType.NEWS, story));
        }
    }

    private FileNameAliasDto getBadgeFilenameDto(Long badgeId) {
        if (badgeId == null) {
            return null;
        }

        FilenameAlias filenameAlias = filenameAliasRepository.findOne(badgeId);

        if (filenameAlias == null) {
            return null;
        } else {
            FileNameAliasDto dto = new FileNameAliasDto();
            dto.setAlias(filenameAlias.getAlias());
            dto.setId(filenameAlias.getId());
            dto.setFileName(filenameAlias.getFileName());
            return dto;
        }
    }

    private DeepLinkInfoService.ApplicationPageData createApplicationPageData(InformationDeeplinkInfo i) {
        switch (i.getLinkType()) {
            case INTERNAL_AD:
                return buildForInternalAd(i);
            case EXTERNAL_AD:
                return buildForExternalAd(i);
        }
        throw new UnsupportedOperationException("Link type is not defined");
    }

    private DeepLinkInfoService.ApplicationPageData buildForExternalAd(InformationDeeplinkInfo i) {
        return new DeepLinkInfoService.ApplicationPageData(i.getUrl(), i.getOpener());
    }

    private DeepLinkInfoService.ApplicationPageData buildForInternalAd(InformationDeeplinkInfo i) {
        final String action = i.getAction();
        if (action == null) {
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

        for (User user : update.getUsers()) {
            dto.addUserName(user.getUserName());
        }
        return dto;
    }

    private <E0 extends Enum<E0>, E extends Enum<E>> String getMessage(E0 prefix, E suffix) {
        return messageSource.getMessage("streamzine.contenttype." + prefix.name() + "." + suffix.name(), null, LocaleContextHolder.getLocale());
    }
}
