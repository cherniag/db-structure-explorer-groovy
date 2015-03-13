package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.ItemAsm;
import mobi.nowtechnologies.server.assembler.OfferAsm;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Item;
import mobi.nowtechnologies.server.persistence.domain.Offer;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.ItemRepository;
import mobi.nowtechnologies.server.persistence.repository.OfferRepository;
import mobi.nowtechnologies.server.shared.dto.ItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.FilterDto;
import mobi.nowtechnologies.server.shared.dto.admin.OfferDto;
import mobi.nowtechnologies.server.shared.dto.web.ContentOfferDto;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 */
public class OfferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferService.class);

    private OfferRepository offerRepository;
    private ItemRepository itemRepository;
    private FilterService filterService;
    private UserService userService;
    private CloudFileService cloudFileService;
    private AccountLogService accountLogService;

    @Transactional(readOnly = true)
    public List<OfferDto> getOffers(String communityURL) {
        LOGGER.debug("input parameters communityURL: [{}]", communityURL);

        Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

        List<Offer> offers = offerRepository.findWithFiltersByCommunity(community);
        List<OfferDto> offerDtos = OfferAsm.toOfferDtos(offers);
        LOGGER.debug("Output parameter [{}]", offerDtos);

        return offerDtos;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Offer update(OfferDto offerDto, String communityURL) {
        LOGGER.debug("input parameters offerDto, communityURL: [{}], [{}]", offerDto, communityURL);

        Offer offer = null;

        final Integer id = offerDto.getId();
        if (id != null) {
            offer = offerRepository.findOne(id);
        }

        if (offer != null) {
            offer = saveOrUpdate(offerDto, communityURL, offer);
        }

        LOGGER.debug("Output parameter offer=[{}]", offer);
        return offer;
    }

    @SuppressWarnings("unchecked")
    private Offer saveOrUpdate(OfferDto offerDto, String communityURL, Offer offer) {
        LOGGER.debug("input parameters offerDto, communityURL, offer: [{}], [{}]", new Object[] {offerDto, communityURL, offer});

        Community community = CommunityDao.getMapAsUrls().get(communityURL.toUpperCase());

        final Set<FilterDto> filterDtos = offerDto.getFilterDtos();
        final Set<AbstractFilterWithCtiteria> filterWithCtiteria;
        if (filterDtos != null) {
            filterWithCtiteria = filterService.find(filterDtos);
        } else {
            filterWithCtiteria = Collections.EMPTY_SET;
        }

        final List<Integer> itemIds = offerDto.getItemIds();
        final List<Item> items;
        if (itemIds != null) {
            items = itemRepository.findByIds(itemIds);
        } else {
            items = Collections.EMPTY_LIST;
        }

        offer.setTitle(offerDto.getTitle());
        offer.setPrice(offerDto.getPrice());
        offer.setCurrency(offerDto.getCurrency());
        offer.setFilterWithCtiteria(filterWithCtiteria);
        offer.setItems(items);
        if (offer.getId() == null) {
            offer.setCoverFileName("");
        }
        offer.setCommunity(community);
        offer.setDescription(offerDto.getDescription());

        offer = offerRepository.save(offer);

        // in order not to remove the image name if there was no file selected for update
        if (null != offerDto.getFile() && !offerDto.getFile().isEmpty()) {
            String imageFileName = "offer_" + System.currentTimeMillis() + "_" + offer.getId();
            offer.setCoverFileName(imageFileName);
            offer = offerRepository.save(offer);
        }

        LOGGER.debug("Output parameter offer=[{}]", offer);
        return offer;
    }

    @Transactional(readOnly = true)
    public Offer getOffer(Integer offerId) {
        LOGGER.debug("getOffer input parameters offerId: [{}]", offerId);

        return offerRepository.findOneWithFilters(offerId);
    }

    @Transactional(readOnly = true)
    public OfferDto getOfferDto(Integer offerId) {
        LOGGER.debug("input parameters offerId: [{}]", offerId);

        Offer offer = getOffer(offerId);

        OfferDto offerDto = null;
        if (offer != null) {
            offerDto = OfferAsm.toOfferDto(offer);
        }
        LOGGER.debug("Output parameter offerDto=[{}]", offerDto);
        return offerDto;
    }

    @Transactional(readOnly = true)
    public ContentOfferDto getContentOfferDto(Integer offerId) {
        LOGGER.debug("input parameters offerId: [{}]", offerId);

        Offer offer = offerRepository.findOneWithFilters(offerId);

        ContentOfferDto contentOfferDto = null;
        if (offer != null) {
            contentOfferDto = OfferAsm.toContentOfferDto(offer);
        }
        LOGGER.debug("Output parameter contentOfferDto=[{}]", contentOfferDto);
        return contentOfferDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(Integer offerId) {
        LOGGER.debug("input parameters offerId: [{}]", offerId);

        boolean deleted = false;
        Offer offer = offerRepository.findOne(offerId);
        offer.setFilterWithCtiteria(new HashSet<AbstractFilterWithCtiteria>());
        offer.setItems(new LinkedList<Item>());
        offerRepository.delete(offer);

        LOGGER.debug("Output parameter deleted=[{}]", deleted);
        return deleted;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Offer save(OfferDto offerDto, String communityURL) {
        LOGGER.debug("input parameters offerDto, communityURL: [{}], [{}]", offerDto, communityURL);

        Offer offer = saveOrUpdate(offerDto, communityURL, new Offer());

        LOGGER.debug("Output parameter offer=[{}]", offer);
        return offer;
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems(String filter) {
        LOGGER.debug("input parameters filter: [{}]", filter);

        String substringFilter = "%" + filter + "%";

        List<Item> items = itemRepository.findByTitle(substringFilter);
        List<ItemDto> itemDtos = ItemAsm.toItemDtos(items);

        LOGGER.debug("Output parameter [{}]", itemDtos);

        return itemDtos;
    }

    @Transactional(readOnly = true)
    public ItemDto getItemById(Integer id) {
        LOGGER.debug("input parameters id: [{}]", id);

        Item item = itemRepository.findOne(id);
        ItemDto itemDto = ItemAsm.toItemDto(item);

        LOGGER.debug("Output parameter itemDto[{}]", itemDto);
        return itemDto;
    }

    @Transactional(readOnly = true)
    public boolean hasOffers(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        long count = offerRepository.countWithFiltersByCommunity(user.getUserGroup().getCommunity());

        boolean hasOffers = count > 0;
        LOGGER.debug("Output parameter [{}]", count);
        return hasOffers;
    }

    @Transactional(readOnly = true)
    public List<ContentOfferDto> getContentOfferDtos(Integer userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);

        User user = userService.findById(userId);
        LOGGER.info("performance measure: getting offers");
        List<Offer> offers = offerRepository.findWithItemsAndFiltersByCommunity(user.getUserGroup().getCommunity());
        LOGGER.info("performance measure: getting bought offers ids");
        List<Integer> boughtOfferIds = accountLogService.getRelatedMediaUIDsByLogType(user.getId(), TransactionType.OFFER_PURCHASE);
        LOGGER.info("performance measure: megring content");
        List<ContentOfferDto> contentOfferDtos = OfferAsm.toContentOfferDtos(offers, user, boughtOfferIds);
        LOGGER.info("performance measure: finished");
        LOGGER.debug("Output parameter [{}]", contentOfferDtos);
        return contentOfferDtos;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Offer saveOffer(OfferDto offerDto, String communityURL) {
        LOGGER.debug("input parameters offerDto, communityURL: [{}], [{}]", offerDto, communityURL);
        Offer offer = save(offerDto, communityURL);
        final MultipartFile file = offerDto.getFile();
        cloudFileService.uploadFile(file, offer.getCoverFileName());

        LOGGER.debug("Output parameter [{}]", offer);
        return offer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Offer updateOffer(OfferDto offerDto, String communityURL) {
        LOGGER.debug("input parameters offerDto, communityURL: [{}], [{}]", offerDto, communityURL);

        Offer offer = update(offerDto, communityURL);
        final MultipartFile file = offerDto.getFile();
        if (offer != null) {
            cloudFileService.uploadFile(file, offer.getCoverFileName());
        }

        LOGGER.debug("Output parameter [{}]", file);
        return offer;
    }

    public void setOfferRepository(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCloudFileService(CloudFileService cloudFileService) {
        this.cloudFileService = cloudFileService;
    }

    public void setAccountLogService(AccountLogService accountLogService) {
        this.accountLogService = accountLogService;
    }
}