package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The class <code>EntityServiceTest</code> contains tests for the class
 * <code>{@link EntityService}</code>.
 * 
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class EntityServiceTest {

	private static EntityService entityService;

	// /**
	// * Run the Object find(Class<T>,int,String) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test
	// public void testFindSuccess() throws Exception {
	// int userId = 1;
	// String communityName = "";
	//
	// Object result = entityService.find(Genre.class, userId, communityName);
	// assertNotNull(result);
	// }
	//
	// /**
	// * Run the Object find(Class<T>,int,String) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test
	// public void testFind_2() throws Exception {
	// Class<Object> entityClass = Object.class;
	// int userId = 1;
	// String communityName = "";
	//
	// Object result = entityService.find(entityClass, userId, communityName);
	// assertNotNull(result);
	// }
	//
	// /**
	// * Run the Object find(Class<T>,int,String) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test(expected = java.lang.NullPointerException.class)
	// public void testFind_3() throws Exception {
	// Class<Object> entityClass = Object.class;
	// int userId = 1;
	// String communityName = "";
	//
	// entityService.find(entityClass, userId, communityName);
	// }
	//
	// /**
	// * Run the Object find(Class<T>,int,String) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test(expected = java.lang.NullPointerException.class)
	// public void testFind_4() throws Exception {
	// Class<Object> entityClass = Object.class;
	// int userId = 1;
	// String communityName = null;
	//
	// entityService.find(entityClass, userId, communityName);
	// }
	//
	// /**
	// * Run the Object findById(Class<T>,Object) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test
	// public void testFindById_1() throws Exception {
	// Class<Object> entityClass = Object.class;
	// Object id = new Object();
	//
	// Object result = entityService.findById(entityClass, id);
	// assertNotNull(result);
	// }
	//
	// /**
	// * Run the Object findById(Class<T>,Object) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test(expected = java.lang.NullPointerException.class)
	// public void testFindById_2() throws Exception {
	// Class<Object> entityClass = Object.class;
	// Object id = null;
	//
	// entityService.findById(entityClass, id); }
	//
	// /**
	// * Run the Object findById(Class<T>,Object) method test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test(expected = java.lang.NullPointerException.class)
	// public void testFindById_3() throws Exception {
	// Class<Object> entityClass = Object.class;
	// Object id = new Object();
	//
	// entityService.findById(entityClass, id);
	// }

	/**
	 * Run the Object findByProperty(Class<T>,String,Object) method test.
	 * 
	 * @throws Exception
	 * 
	 * 
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
	 * 
	 * 
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
	 * 
	 * 
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

	/**
	 * Run the List<Object> findListByProperties(Class<T>,Map<String,Object>)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	public void testFindListByProperties_Success() throws Exception {
		Class<Genre> entityClass = Genre.class;
		Map<String, Object> fieldNameValueMap = new HashMap();
		fieldNameValueMap.put("name", "Default");

		List<Genre> result = entityService.findListByProperties(entityClass,
				fieldNameValueMap);
		assertNotNull(result);
	}

	/**
	 * Run the List<Object> findListByProperties(Class<T>,Map<String,Object>)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	public void testFindListByProperties_SuccessAndCondtions() throws Exception {
		Class<Country> entityClass = Country.class;
		Map<String, Object> fieldNameValueMap = new HashMap();
		fieldNameValueMap.put(Country.Fields.fullName.toString(),
				"Great Britain");
		fieldNameValueMap.put(Country.Fields.name.toString(), "GB");

		List<Country> result = entityService.findListByProperties(entityClass,
				fieldNameValueMap);
		assertNotNull(result);
	}

	/**
	 * Run the List<Object> findListByProperties(Class<T>,Map<String,Object>)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test(expected = ServiceException.class)
	public void testFindListByProperties_fieldNameValueMapIsNull()
			throws Exception {
		Class<Object> entityClass = Object.class;
		Map<String, Object> fieldNameValueMap = null;

		List<Object> result = entityService.findListByProperties(entityClass,
				fieldNameValueMap);

		assertNotNull(result);
	}

	/**
	 * Run the List<Object> findListByProperty(Class<T>,String,Object[]) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test(expected = ServiceException.class)
	public void testFindListByProperty_fieldNameIsEmpty() throws Exception {
		Class<Object> entityClass = Object.class;
		String fieldName = "";
		Object[] values = new Object[] {};

		List<Object> result = entityService.findListByProperty(entityClass,
				fieldName, values);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the List<Object> findListByProperty(Class<T>,String,Object[]) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	public void testFindListByProperty_valuesIsEmpty() throws Exception {
		Class<Country> entityClass = Country.class;
		String fieldName = Country.Fields.fullName.toString();
		Object[] values = new Object[] {};

		List<Country> result = entityService.findListByProperty(entityClass,
				fieldName, values);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the List<Object> findListByProperty(Class<T>,String,Object[]) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	public void testFindListByProperty_SuccessOrConditions() throws Exception {
		Class<Media> entityClass = Media.class;
		String fieldName = Media.Fields.i.toString();
		Object[] values = new Object[] { 47, 48, 49, 50, 51 };

		List<Media> result = entityService.findListByProperty(entityClass,
				fieldName, values);

		assertNotNull(result);
		assertEquals(values.length, result.size());
	}

	/**
	 * Run the List<Object> findListByProperty(Class<T>,String,Object[]) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	public void testFindListByProperty_valuesContainsNull() throws Exception {
		Class<Media> entityClass = Media.class;
		String fieldName = Media.Fields.i.toString();
		Object[] values = new Object[] { 47, null };

		List<Media> result = entityService.findListByProperty(entityClass,
				fieldName, values);
		assertNotNull(result);
	}

	/**
	 * Run the List<Object> findListByProperty(Class<T>,String,Object[]) method
	 * test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test(expected = ServiceException.class)
	public void testFindListByProperty_fieldNameIsNull() throws Exception {
		Class<Object> entityClass = Object.class;
		String fieldName = null;
		Object[] values = new Object[] {};

		List<Object> result = entityService.findListByProperty(entityClass,
				fieldName, values);

		assertNotNull(result);
	}

	// /**
	// * Run the List<Object> findMultiple(List<Class<?>>,int,String) method
	// test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test
	// public void testFindMultiple_1() throws Exception {
	// List<Class<Object>> entityClasses = new LinkedList();
	// int userId = 1;
	// String communityName = "";
	//
	// List<Object> result = entityService.findMultiple(entityClasses, userId,
	// communityName);
	//
	// assertNotNull(result);
	// assertEquals(0, result.size());
	// }
	//
	// /**
	// * Run the List<Object> findMultiple(List<Class<?>>,int,String) method
	// test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test
	// public void testFindMultiple_2() throws Exception {
	// List<Class<Object>> entityClasses = new LinkedList();
	// int userId = 1;
	// String communityName = "";
	//
	// List<Object> result = entityService.findMultiple(entityClasses, userId,
	// communityName);
	//
	// assertNotNull(result);
	// assertEquals(0, result.size());
	// }
	//
	// /**
	// * Run the List<Object> findMultiple(List<Class<?>>,int,String) method
	// test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test(expected = java.lang.NullPointerException.class)
	// public void testFindMultiple_3() throws Exception {
	// List<Class<Object>> entityClasses = new LinkedList();
	// int userId = 1;
	// String communityName = null;
	//
	// List<Object> result = entityService.findMultiple(entityClasses, userId,
	// communityName);
	//
	// assertNotNull(result);
	// }
	//
	// /**
	// * Run the List<Object> findMultiple(List<Class<?>>,int,String) method
	// test.
	// *
	// * @throws Exception
	// *
	// *
	// */
	// @Test(expected = java.lang.RuntimeException.class)
	// public void testFindMultiple_4() throws Exception {
	// List<Class<Object>> entityClasses = null;
	// int userId = 1;
	// String communityName = "";
	//
	// List<Object> result = entityService.findMultiple(entityClasses, userId,
	// communityName);
	//
	// assertNotNull(result);
	// }

	/**
	 * Run the void saveEntity(Object) method test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test
	@Ignore
	public void testSaveEntity_Success() throws Exception {
		Genre genre = new Genre();
		genre.setName("Blues Rock");

		entityService.saveEntity(genre);
	}

	/**
	 * Run the void saveEntity(Object) method test.
	 * 
	 * @throws Exception
	 * 
	 * 
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
	 * 
	 * 
	 */
	@Test
	@Ignore
	public void testUpdateEntity_1() throws Exception {
		Object entity = new Object();

		entityService.updateEntity(entity);
	}

	/**
	 * Run the void updateEntity(Object) method test.
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test(expected = ServiceException.class)
	public void testUpdateEntity_entityIsNull() throws Exception {
		Object entity = null;

		entityService.updateEntity(entity);

	}

//	@Test
//	public void testFindListByProperty_Success() {
//		Class<PaymentPolicy> entityClass = PaymentPolicy.class;
//		String fieldName = PaymentPolicy.Fields.communityId.name()
//				+"."+ Community.Fields.name.name();
//		String fieldValue = "CN Commercial Beta";
//		List<PaymentPolicy> paymentPolicies = entityService.findListByProperty(entityClass,
//				fieldName, fieldValue);
//		assertNotNull(paymentPolicies);
//	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * 
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		ClassPathXmlApplicationContext appServiceContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/dao-test.xml",
						"/META-INF/service-test.xml","/META-INF/shared.xml" });
		entityService = (EntityService) appServiceContext
				.getBean("service.EntityService");
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * 
	 */
	@AfterClass
	public static void tearDown() throws Exception {
	}
}