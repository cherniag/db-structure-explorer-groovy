package mobi.nowtechnologies.applicationtests.services;

import mobi.nowtechnologies.server.persistence.domain.Media;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class DbMediaService {
    @PersistenceContext(name = "applicationTestsEntityManager", unitName = "applicationTestsEntityManager")
    EntityManager applicationTestsEntityManager;

    @Transactional(value = "applicationTestsTransactionManager", readOnly = true)
    public Media findByTrackIdAndIsrc(long trackId, String isrc) {
        Query query = applicationTestsEntityManager.createQuery("select m from Media m where m.isrc='" + isrc + "' and m.trackId=" + trackId);
        List resultList = query.getResultList();
        if(resultList.isEmpty()) {
            return null;
        } else {
            return (Media) query.getResultList().get(0);
        }
    }

    @Transactional(value = "applicationTestsTransactionManager", readOnly = true)
    public Media findById(int id) {
        Query query = applicationTestsEntityManager.createQuery("select m from Media m where m.id=" + id);
        List resultList = query.getResultList();
        if(resultList.isEmpty()) {
            return null;
        } else {
            return (Media) query.getResultList().get(0);
        }
    }
}
