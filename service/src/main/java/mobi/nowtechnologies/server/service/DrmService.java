package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.DrmPolicy;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DrmRepository;

import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 */
public class DrmService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrmService.class);

    private DrmRepository drmRepository;

    public void setDrmRepository(DrmRepository drmRepository) {
        this.drmRepository = drmRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Drm findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy, boolean createDrmIfNotExists) {
        LOGGER.debug("input parameters findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy): [{}]", new Object[] {user, media, drmPolicy});

        if (user == null) {
            throw new IllegalArgumentException("The parameter user is null");
        }
        if (media == null) {
            throw new IllegalArgumentException("The parameter media is null");
        }

        Drm drm = null;
        if (createDrmIfNotExists && media.getI() != null) {
            if (user.getDrms() != null) {
                try {
                    for (Drm drmOfUser : user.getDrms()) {
                        if (drmOfUser.getMediaId() == media.getI().intValue()) {
                            drm = drmOfUser;
                            break;
                        }
                    }
                } catch (LazyInitializationException e) {
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

            if (createDrmIfNotExists) {
                drmRepository.save(drm);
            }
        }

        LOGGER.info("Output parameter findDrmByUserAndMedia(User user, Media media, DrmPolicy drmPolicy)=[{}]", drm);
        return drm;
    }

}
