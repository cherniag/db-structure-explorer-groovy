package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.DrmDao;
import mobi.nowtechnologies.server.persistence.dao.DrmTypeDao;
import mobi.nowtechnologies.server.persistence.dao.MediaLogTypeDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.DrmRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

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
	private MediaService mediaService;
	
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

	public void setMediaService(MediaService mediaService) {
		this.mediaService = mediaService;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy, boolean createDrmIfNotExists) {
		LOGGER.debug("input parameters findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy): [{}]", new Object[] { user, media, drmPolicy });

		if (user == null)
			throw new IllegalArgumentException("The parameter user is null");
		if (media == null)
			throw new IllegalArgumentException("The parameter media is null");

		Drm drm = null;
		if(createDrmIfNotExists && media.getI() != null){
			if (user.getDrms() != null) {
                try{
                    for(Drm drmOfUser : user.getDrms()){
                        if(drmOfUser.getMediaId() == media.getI().intValue()){
                            drm = drmOfUser;
                            break;
                        }
                    }
                } catch (LazyInitializationException e){
                    drm = null;
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
}
