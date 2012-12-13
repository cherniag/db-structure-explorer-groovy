package mobi.nowtechnologies.server.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.dao.ChartDetailDao;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DrmDao;
import mobi.nowtechnologies.server.persistence.dao.DrmTypeDao;
import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.DrmPolicy;
import mobi.nowtechnologies.server.persistence.domain.DrmType;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.BuyTrackDto;
import mobi.nowtechnologies.server.shared.dto.DrmDto;
import mobi.nowtechnologies.server.shared.dto.DrmItemDto;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDto;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * 
 */
public class DrmService {
	private static final String CHARTSNOW = "CHARTSNOW";

	private static final Logger LOGGER = LoggerFactory.getLogger(DrmService.class);

	private EntityService entityService;
	private DrmDao drmDao;
	private ChartDetailDao chartDetailDao;
	private MediaService mediaService;
	private AccountLogService accountLogService;
	private UserService userService;
	private ChartDetailService chartDetailService;

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setDrmDao(DrmDao drmDao) {
		this.drmDao = drmDao;
	}

	public void setChartDetailDao(ChartDetailDao chartDetailDao) {
		this.chartDetailDao = chartDetailDao;
	}

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}

	public void setAccountLogService(AccountLogService accountLogService) {
		this.accountLogService = accountLogService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setChartDetailService(ChartDetailService chartDetailService) {
		this.chartDetailService = chartDetailService;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Object[] processSetDrmCommand(String mediaIsrc, byte newDrmValue, int userId, String communityName) {
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");
		if (mediaIsrc == null)
			throw new NullPointerException("The parameter mediaIsrc is null");
		LOGGER.debug("input parameters mediaIsrc, newDrmValue, userId, communityName: [{}], [{}], [{}], [{}]", new Object[] { mediaIsrc, newDrmValue, userId,
				communityName });

		AccountCheckDTO accountCheck = userService.proceessAccountCheckCommandForAuthorizedUser(userId, null, null);
		List<Drm> drmList = findDrmTreeAndUpdateDrmValue(userId, mediaIsrc, newDrmValue);

		DrmDto drmDto = new DrmDto();
		List<DrmItemDto> drmItemDtoList = Drm.toDrmItemDtoList(drmList);
		DrmItemDto[] drmItemDtoArray = drmItemDtoList.toArray(new DrmItemDto[0]);
		drmDto.setDrmItemDtos(drmItemDtoArray);

		Object[] objects = new Object[] { accountCheck, drmDto };
		LOGGER.debug("Output parameter objects=[{}]", objects);
		return objects;
	}

	private List<Drm> findDrmTreeAndUpdateDrmValue(int userId, String mediaIsrc, byte newDrmValue) {
		if (mediaIsrc == null)
			throw new NullPointerException("The parameter mediaIsrc is null");
		LOGGER.debug("input parameters mediaIsrc, newDrmValue, userId: [{}], [{}], [{}]", new Object[] { mediaIsrc, newDrmValue, userId });

		List<Drm> drms = drmDao.findDrmTree(userId, mediaIsrc);

		Drm drm = null;
		final int drmSize = drms.size();
		if (drmSize == 1) {
			drm = drms.get(0);
			DrmType drmType = drm.getDrmType();
			String drmTypeName = drmType.getName();
			byte drmValue = drm.getDrmValue();
			if (drmTypeName.equals("TIME") || drmTypeName.equals("PURCHASED") || drmValue > 99 || drmValue < newDrmValue) {
				drm.setDrmValue(newDrmValue);
				entityService.updateEntity(drm);

				drms = drmDao.findDrmTree(userId, mediaIsrc);
			}
		} else if (drmSize > 1)
			throw new ServiceException("Incorrect drms size for [" + drmSize + "] communityId. Expected 1 but there are ["
					+ drmSize + "] found");

		LOGGER.debug("Output parameter drms=[{}]", drms);
		return drms;
	}

	public List<Drm> findDrmAndDrmTypeTree(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		List<Drm> drms = drmDao.findDrmAndDrmTypeTree(userId);
		LOGGER.debug("Output parameter drms=[{}]", drms);
		return drms;
	}

	public void moveDrms(User fromUser, User toUser) {
		List<Drm> drms = findDrmAndDrmTypeTree(fromUser.getId());

		for (Drm drm : drms) {
			drm.setUser(toUser);
			entityService.updateEntity(drm);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Drm> processBuyTrackCommand(User user, List<Media> mediaList) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (mediaList == null)
			throw new ServiceException("The parameter media is null");

		Object[] argArray = new Object[] { user, mediaList };
		LOGGER.debug("input parameters userId, media, communityName: [{}], [{}], [{}]", argArray);

		UserGroup userGroup = user.getUserGroup();
		if (userGroup == null)
			throw new ServiceException("The parameter userGroup is null");

		DrmPolicy drmPolicy = userGroup.getDrmPolicy();

		if (drmPolicy == null)
			throw new ServiceException("The parameter drmPolicy is null");

		int userId = user.getId();
		DrmType drmType = DrmTypeDao.getPURCHASED_DRM_TYPE();
		byte drmValue = drmPolicy.getDrmValue();

		List<Drm> purchasedDrms = new LinkedList<Drm>();
		for (Media media : mediaList) {
			Drm drmForCurrentUser = new Drm();

			drmForCurrentUser.setMedia(media);
			drmForCurrentUser.setUser(user);
			drmForCurrentUser.setDrmType(drmType);
			drmForCurrentUser.setDrmValue(drmValue);

			mediaService.logMediaEvent(userId, media, MediaLogTypeDao.PURCHASE);

			entityService.saveEntity(drmForCurrentUser);

			purchasedDrms.add(drmForCurrentUser);
		}

		return purchasedDrms;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Object[] processBuyTrackCommand(User user, String isrc, String communityName) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (isrc == null)
			throw new ServiceException("The parameter isrc is null");
		if (communityName == null)
			throw new ServiceException("The parameter communityName is null");

		Object[] argArray = new Object[] { user, isrc, communityName };
		LOGGER.debug("input parameters userId, mediaUID, communityName: [{}], [{}], [{}]", argArray);

		boolean isTrackCanBeBoughtAccordingToLicense = chartDetailService.isTrackCanBeBoughtAccordingToLicense(isrc);
		if (!isTrackCanBeBoughtAccordingToLicense)
			throw ServiceException.getInstance("buyTrack.command.error.attemptToBuyBonusTrack");

		int userId = user.getId();

		AccountCheckDTO accountCheck = userService.proceessAccountCheckCommandForAuthorizedUser(userId, null, null);

		final BuyTrackDto buyTrackDto = new BuyTrackDto();
		buyTrackDto.setStatus(BuyTrackDto.Status.FAIL);

		if (user.getSubBalance() > 0) {
			List<Drm> drms = drmDao.findDrmTree(userId, isrc);
			if (drms.size() > 0) {
				Drm drm = drms.get(0);

				DrmType drmType = drm.getDrmType();
				if (isrc.startsWith(CHARTSNOW)) {
					buyTrackDto.setStatus(BuyTrackDto.Status.NOTDOWNLOAD);
				} else if (drmType.getName().equals(DrmTypeDao.PURCHASED)) {
					buyTrackDto.setStatus(BuyTrackDto.Status.ALREADYPURCHASED);
				} else {
					drm.setDrmType(DrmTypeDao.getPURCHASED_DRM_TYPE());
					entityService.updateEntity(drm);

					byte intNewBalance = (byte) (user.getSubBalance() - 1);

					userService.updateUserBalance(user, intNewBalance);

					byte balanceAfter = (byte) (intNewBalance);
					Media relatedMedia = drm.getMedia();
					accountLogService.logAccountEvent(userId, balanceAfter, relatedMedia, null, TransactionType.TRACK_PURCHASE, null);
	
					mediaService.logMediaEvent(userId, relatedMedia, MediaLogTypeDao.PURCHASE);

					buyTrackDto.setStatus(BuyTrackDto.Status.OK);
				}
			}
		} else {
			buyTrackDto.setStatus(BuyTrackDto.Status.BALANCETOOLOW);
		}

		Object[] objects = new Object[] { accountCheck, buyTrackDto };
		LOGGER.debug("Output parameter objects=[{}], [{}]", objects);
		return objects;
	}

	@Transactional(readOnly = true)
	public Object[] getPurchasedContentInfo(User user, String communityName) {
		LOGGER.debug("input parameters user, communityName: [{}], [{}], [{}]", new Object[] { user, communityName });
		int userId = user.getId();

		user = userService.findUserTree(userId);
		AccountCheckDTO accountCheck = user.toAccountCheckDTO(null);

		List<Drm> drms = drmDao.findByUserAndDrmType(user.getId(), DrmTypeDao.getPURCHASED_DRM_TYPE());

		List<PurchasedChartDetailDto> purchasedChartDetailDtos = ChartDetailsAsm.toPurchasedChartDetailDtoList(drms);

		// ---TODO hack for 3.4.1 client
		if (!purchasedChartDetailDtos.isEmpty())
		{
			Map<String, PurchasedChartDetailDto> map = new HashMap<String, PurchasedChartDetailDto>();
			for (PurchasedChartDetailDto dto : purchasedChartDetailDtos)
				map.put(dto.getMedia(), dto);
			List<ChartDetail> chartDetails = chartDetailDao.findContentInfoByIsrc(user, map.keySet());
			for (ChartDetail chartDetail : chartDetails) {
				PurchasedChartDetailDto dto = map.get(chartDetail.getMedia().getIsrc());
				dto.setPosition(chartDetail.getPosition());
				dto.setPreviousPosition(chartDetail.getPrevPosition());
			}
		}
		// --------------------------------------

		PurchasedChartDto purchasedChartDto = new PurchasedChartDto();
		purchasedChartDto.setPurchasedChartDetailDtos(purchasedChartDetailDtos.toArray(new PurchasedChartDetailDto[0]));
		Object[] objects = new Object[] { accountCheck, purchasedChartDto };

		LOGGER.debug("Output parameter objects=[{}]", objects);
		return objects;
	}
}
