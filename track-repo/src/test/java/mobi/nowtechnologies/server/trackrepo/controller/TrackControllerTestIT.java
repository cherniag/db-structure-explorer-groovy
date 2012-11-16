package mobi.nowtechnologies.server.trackrepo.controller;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import junit.framework.TestCase;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.impl.TrackServiceImpl;
import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommandThread;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>TrackControllerTest</code> contains tests for the class <code>{@link TrackController}</code>.
 * 
 * @generatedBy CodePro at 11/13/12 5:09 PM, using the Spring generator
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/application-test.xml",
		"file:src/main/webapp/WEB-INF/trackrepo-servlet.xml" })
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
@PrepareForTest(TrackServiceImpl.class)
public class TrackControllerTestIT extends TestCase {
	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Autowired
	private TrackController fixture;

	@Autowired
	private TrackRepository trackRepository;

	/**
	 * Test data
	 */
	private Track ingestedTrack;
	private Track encodeTrack;

	/**
	 * Run the  TrackDto encode(Long trackId, Boolean isHighRate, Boolean licensed) test
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	@Test
	public void testEncode()
			throws Exception {

		Long trackId = ingestedTrack.getId();
		boolean isHighRate = false;
		boolean licensed = true;

		TrackDto trackDto = fixture.encode(trackId, isHighRate, licensed);

		assertNotNull(trackDto);
		assertEquals(trackDto.getId(), trackId);
		assertEquals(trackDto.getStatus(), TrackStatus.ENCODED);
	}
	
	/**
	 * Run the  PageListDto<? extends TrackDto> find(String query, SearchTrackDto searchTrackDto, Pageable page) test
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	@Test
	public void testFind()
			throws Exception {

		SearchTrackDto searchTrackDto = new SearchTrackDto();
		searchTrackDto.setIsrc(ingestedTrack.getIsrc());
		
		PageListDto<? extends TrackDto> trackDtos = fixture.find(null, searchTrackDto, new PageRequest(0, 10));

		assertNotNull(trackDtos);
		assertEquals(trackDtos.getList().size(), 2);
		assertEquals(trackDtos.getTotal(), 1);
		assertEquals(trackDtos.getSize(), 10);
		assertEquals(trackDtos.getPage(), 0);
	}
	
	/**
	 * Run the  TrackDto pull(Long trackId) test
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	@Test
	public void testPull()
			throws Exception {

		Long trackId = encodeTrack.getId();
		
		TrackDto trackDto = fixture.pull(trackId);

		assertNotNull(trackDto);
		assertEquals(trackDto.getId(), trackId);
		assertNotNull(trackDto.getFiles());
		assertEquals(trackDto.getFiles().size(), 15);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @see TestCase#setUp()
	 * 
	 * @generatedBy CodePro at 11/13/12 5:09 PM
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		ExternalCommandThread command = mock(ExternalCommandThread.class);
		whenNew(ExternalCommandThread.class).withNoArguments().thenReturn(command);
		doNothing().when(command).run();

		TrackFactory trackFactory = new TrackFactory();
		encodeTrack = trackFactory.anyTrack();
		encodeTrack.setStatus(TrackStatus.ENCODED);
		ingestedTrack = trackFactory.anyTrack();

		trackRepository.save(ingestedTrack);
		trackRepository.save(encodeTrack);
	}
}