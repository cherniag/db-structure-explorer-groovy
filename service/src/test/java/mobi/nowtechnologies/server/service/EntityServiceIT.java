package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * The class <code>EntityServiceTest</code> contains tests for the class <code>{@link EntityService}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class EntityServiceIT {

    @Autowired
    @Qualifier("service.EntityService")
    private EntityService entityService;

    @Test
    public void testSaveEntity_Success() throws Exception {
        Genre genre = new Genre();
        genre.setName("Blues Rock");

        entityService.saveEntity(genre);
    }

    /**
     * Run the void saveEntity(Object) method test.
     *
     * @throws Exception
     */
    @Test(expected = ServiceException.class)
    public void testSaveEntity_entityIsNull() throws Exception {
        Object entity = null;

        entityService.saveEntity(entity);

    }

    /**
     * Run the void updateEntity(Object) method test.
     *
     * @throws Exception
     */
    @Test(expected = ServiceException.class)
    public void testUpdateEntity_entityIsNull() throws Exception {
        Object entity = null;

        entityService.updateEntity(entity);

    }

}