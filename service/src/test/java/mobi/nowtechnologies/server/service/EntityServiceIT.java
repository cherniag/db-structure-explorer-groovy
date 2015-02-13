package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;

/**
 * The class <code>EntityServiceTest</code> contains tests for the class
 * <code>{@link EntityService}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/service-test.xml", "classpath:/META-INF/dao-test.xml", "/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class EntityServiceIT {

    @Autowired
    @Qualifier("service.EntityService")
    private EntityService entityService;

    /**
     * Run the Object findByProperty(Class<T>,String,Object) method test.
     *
     * @throws Exception
     */
    @Test
    public void testFindByProperty_Success() throws Exception {

        Class<Genre> entityClass = Genre.class;
        String fieldName = "name";
        Object fieldValue = "Default";

        Object result = entityService.findByProperty(entityClass, fieldName,
                fieldValue);
        assertNotNull(result);
    }

    /**
     * Run the Object findByProperty(Class<T>,String,Object) method test.
     *
     * @throws Exception
     */
    @Test(expected = ServiceException.class)
    public void testFindByProperty_FieldNameIsEmpty() throws Exception {
        Class<Genre> entityClass = Genre.class;
        String fieldName = "";
        Object fieldValue = new Object();

        Object result = entityService.findByProperty(entityClass, fieldName,
                fieldValue);
        assertNotNull(result);
    }

    /**
     * Run the Object findByProperty(Class<T>,String,Object) method test.
     *
     * @throws Exception
     */
    @Test(expected = ServiceException.class)
    public void testFindByProperty_FieldNameIsNull() throws Exception {
        Class<Genre> entityClass = Genre.class;
        String fieldName = null;
        Object fieldValue = new Object();

        Object result = entityService.findByProperty(entityClass, fieldName,
                fieldValue);

        assertNotNull(result);
    }

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