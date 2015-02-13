package mobi.nowtechnologies.applicationtests.services.mail;

import mobi.nowtechnologies.server.apptests.email.MailModelSerializer;
import mobi.nowtechnologies.server.persistence.apptests.domain.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Service
public class EmailChecker {
    @Resource
    MailModelSerializer mailModelSerializer;

    @PersistenceContext(name = "applicationTestsEntityManager", unitName = "applicationTestsEntityManager")
    EntityManager applicationTestsEntityManager;

    @Transactional(value = "applicationTestsTransactionManager", readOnly = true)
    public Email findByModel(Map<String, String> model) {
        String modelId = mailModelSerializer.serialize(model);
        Query query = applicationTestsEntityManager.createQuery("select e from Email e where e.model='" + modelId + "'");
        List resultList = query.getResultList();
        if(resultList.isEmpty()) {
            return null;
        } else {
            return (Email) query.getResultList().get(0);
        }
    }
}
