package mobi.nowtechnologies.server.service.streamzine.asm;

import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.dto.streamzine.*;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class StreamzineUpdateAdminAsm {
    private MessageSource messageSource;
    private DeepLinkInfoService deepLinkInfoService;
    private UserRepository userRepository;
    private StreamzineAdminMediaAsm streamzineAdminMediaAsm;

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

    @Transactional
    public Update fromIncomingDto(UpdateIncomingDto dto, String community) {
        Update u = new Update(new Date(dto.getTimestamp()));

        for (OrdinalBlockDto blockDto : dto.getBlocks()) {
            Block block = restoreBlock(blockDto);

            u.addBlock(block);
        }

        for (String userName : dto.getUserNames()) {
            User user = userRepository.findOne(userName, community);
            u.addUser(user);
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
            blockDto.setData(streamzineAdminMediaAsm.toPlaylistDto(i));
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, playlist));
        }

        if(info instanceof MusicTrackDeeplinkInfo) {
            final MusicType track = MusicType.TRACK;

            MusicTrackDeeplinkInfo i = (MusicTrackDeeplinkInfo) info;
            blockDto.setKey(track.name());
            Media media = i.getMedia();
            if (media != null) {
                blockDto.setValue(media.getI() + "");
                blockDto.setData(streamzineAdminMediaAsm.toMediaDto(i.getMedia()));
            }
            blockDto.setContentTypeTitle(getMessage(ContentType.MUSIC, track));
        }

        if(info instanceof ManualCompilationDeeplinkInfo) {
            final MusicType musicType = MusicType.MANUAL_COMPILATION;

            ManualCompilationDeeplinkInfo i = (ManualCompilationDeeplinkInfo) info;
            DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(i.getMediaIds());
            blockDto.setValue(manualCompilationData.toMediasString());
            blockDto.setData(streamzineAdminMediaAsm.toMediaDtos(i.getMedias()));
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
        switch (i.getLinkType()) {
            case INTERNAL_AD:
                return buildForInternalAd(i);
            case EXTERNAL_AD:
                return buildForExternalAd(i);
        }
        throw new RuntimeException("Link type is not defined");
    }

    private DeepLinkInfoService.ApplicationPageData buildForExternalAd(InformationDeeplinkInfo i) {
            return new DeepLinkInfoService.ApplicationPageData(i.getUrl(), i.getOpener());
    }

    private DeepLinkInfoService.ApplicationPageData buildForInternalAd(InformationDeeplinkInfo i) {
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

        for (User user : update.getUsers()) {
            dto.addUserName(user.getUserName());
        }
        return dto;
    }

    private <E0 extends Enum<E0>, E extends Enum<E>> String getMessage(E0 prefix, E suffix) {
        return messageSource.getMessage("streamzine.contenttype." + prefix.name() + "." + suffix.name(), null, LocaleContextHolder.getLocale());
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
