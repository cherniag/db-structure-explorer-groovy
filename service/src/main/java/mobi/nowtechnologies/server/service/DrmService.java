package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.assembler.ChartDetailsAsm;
import mobi.nowtechnologies.server.persistence.dao.ChartDetailDao;
import mobi.nowtechnologies.server.persistence.dao.DrmDao;
import mobi.nowtechnologies.server.persistence.dao.DrmTypeDao;
import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.DrmRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDetailDto;
import mobi.nowtechnologies.server.shared.dto.PurchasedChartDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.assembler.UserAsm.toAccountCheckDTO;

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
	
	private DrmRepository drmRepository;

	public void setDrmRepository(DrmRepository drmRepository) {
		this.drmRepository = drmRepository;
	}

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
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy, boolean createDrmIfNotExists) {
		LOGGER.debug("input parameters findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy): [{}]", new Object[] { user, media, drmPolicy });

		if (user == null)
			throw new IllegalArgumentException("The parameter user is null");
		if (media == null)
			throw new IllegalArgumentException("The parameter media is null");

		Drm drm = null;
		if(media.getI() != null){
			if (user.getDrms() != null) {
				for(Drm drmOfUser : user.getDrms()){
					if(drmOfUser.getMediaId() == media.getI().intValue()){
						drm = drmOfUser;
						break;
					}
				}
			} else {
				drm = drmRepository.findByUserAndMedia(user.getId(), media.getI());
			}
		}

		if (drm == null && drmPolicy != null) {
			drm = new Drm();

			drm.setMedia(media);
			drm.setUser(user);
			drm.setDrmType(drmPolicy.getDrmType());
			drm.setDrmValue(drmPolicy.getDrmValue());

			if(createDrmIfNotExists)
				drmRepository.save(drm);
		}

		LOGGER.info("Output parameter findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy)=[{}]", drm);
		return drm;
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

	@Transactional(readOnly = true)
	public Object[] getPurchasedContentInfo(User user, String communityName) {
		LOGGER.debug("input parameters user, communityName: [{}], [{}], [{}]", new Object[] { user, communityName });
		int userId = user.getId();

		user = userService.findUserTree(userId);
		AccountCheckDTO accountCheck = toAccountCheckDTO(user, null, null, userService.canActivateVideoTrial(user));

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
