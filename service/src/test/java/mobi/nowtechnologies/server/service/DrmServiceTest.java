package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Drm;
import mobi.nowtechnologies.server.persistence.domain.DrmPolicy;
import mobi.nowtechnologies.server.persistence.domain.DrmType;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DrmRepository;

import java.util.Collections;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * The class <code>DrmServiceTest</code> contains tests for the class <code>{@link DrmService}</code>.
 *
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
public class DrmServiceTest {

    @Mock
    private ChartDetailService chartDetailService;

    private DrmService fixture;

    @Mock
    private DrmRepository drmRepository;

    public static Drm anyDrm() {
        User user = new User();
        user.setId((int) (Math.random() * 100));
        Media media = new Media();
        media.setI((int) (Math.random() * 100));

        Drm drm = new Drm();
        drm.setI((int) (Math.random() * 100));
        drm.setUser(user);
        drm.setMedia(media);

        return drm;
    }

    /**
     * Run the Drm findDrmByUserAndMedia(User userWithCommunity, Media media, DrmPolicy drmPolicy) method test with not null search result by media and userWithCommunity.
     *
     * @throws Exception
     */
    @Test
    public void testFindDrmByUserAndMedia_WithNotNullSearch_Successful() throws Exception {
        User user = new User();
        Media media = new Media();
        user.setId(1);
        media.setI(1);

        Drm drm = anyDrm();

        when(drmRepository.findByUserAndMedia(anyInt(), anyInt())).thenReturn(drm);

        Drm result = fixture.findDrmByUserAndMedia(user, media, null, true);

        assertEquals(drm, result);
    }

    /**
     * Run the Drm findDrmByUserAndMedia(User userWithCommunity, Media media, DrmPolicy drmPolicy) method test with not null drms of userWithCommunity.
     *
     * @throws Exception
     */
    @Test
    public void testFindDrmByUserAndMedia_WithNotNullUserDrms_Successful() throws Exception {
        User user = new User();
        Media media = new Media();
        Drm drm = new Drm();
        user.setId(1);
        media.setI(1);
        drm.setMedia(media);
        drm.setUser(user);
        user.setDrms(Collections.singletonList(drm));

        Drm result = fixture.findDrmByUserAndMedia(user, media, null, true);

        assertEquals(drm, result);

        verify(drmRepository, times(0)).findByUserAndMedia(anyInt(), anyInt());
    }

    /**
     * Run the Drm findDrmByUserAndMedia(User userWithCommunity, Media media, DrmPolicy drmPolicy) method test with null search result by media and userWithCommunity and not null drmPolicy.
     *
     * @throws Exception
     */
    @Test
    public void testFindDrmByUserAndMedia_WithNullSearchByNotNullDrmPolicy_Successful() throws Exception {
        DrmType drmType = new DrmType();
        DrmPolicy drmPolicy = new DrmPolicy();
        User user = new User();
        Media media = new Media();
        user.setId(1);
        media.setI(1);
        drmPolicy.setDrmType(drmType);
        drmPolicy.setDrmValue((byte) 30);

        when(drmRepository.findByUserAndMedia(anyInt(), anyInt())).thenReturn(null);
        when(drmRepository.save(any(Drm.class))).thenAnswer(new Answer<Drm>() {
            @Override
            public Drm answer(InvocationOnMock invocation) throws Throwable {
                Drm drm = (Drm) invocation.getArguments()[0];
                drm.setI((int) Math.random() * 100);
                return drm;
            }
        });

        Drm result = fixture.findDrmByUserAndMedia(user, media, drmPolicy, true);

        assertNotNull(result);
        assertNotNull(result.getI());
        assertEquals(drmPolicy.getDrmType(), result.getDrmType());
        assertEquals(drmPolicy.getDrmValue(), result.getDrmValue());
        assertEquals(user, result.getUser());
        assertEquals(media, result.getMedia());
    }

    @Test
    public void testFindDrmByUserAndMedia_WithNotCreateDrmIfNotExists_Successful() throws Exception {
        DrmType drmType = new DrmType();
        DrmPolicy drmPolicy = new DrmPolicy();
        User user = new User();
        Media media = new Media();
        user.setId(1);
        media.setI(1);
        drmPolicy.setDrmType(drmType);
        drmPolicy.setDrmValue((byte) 30);

        when(drmRepository.findByUserAndMedia(anyInt(), anyInt())).thenReturn(null);
        when(drmRepository.save(any(Drm.class))).thenAnswer(new Answer<Drm>() {
            @Override
            public Drm answer(InvocationOnMock invocation) throws Throwable {
                Drm drm = (Drm) invocation.getArguments()[0];
                drm.setI((int) Math.random() * 100);
                return drm;
            }
        });

        Drm result = fixture.findDrmByUserAndMedia(user, media, drmPolicy, false);

        assertNotNull(result);
        assertNotNull(result.getI());
        assertEquals(drmPolicy.getDrmType(), result.getDrmType());
        assertEquals(drmPolicy.getDrmValue(), result.getDrmValue());
        assertEquals(user, result.getUser());
        assertEquals(media, result.getMedia());

        verify(drmRepository, times(0)).save(any(Drm.class));
    }

    /**
     * Run the Drm findDrmByUserAndMedia(User userWithCommunity, Media media, DrmPolicy drmPolicy) method test with null search result by media and userWithCommunity and null drmPolicy.
     *
     * @throws Exception
     */
    @Test
    public void testFindDrmByUserAndMedia_WithNullSearchByNullDrmPolicy_Successful() throws Exception {
        User user = new User();
        Media media = new Media();
        user.setId(1);
        media.setI(1);

        when(drmRepository.findByUserAndMedia(anyInt(), anyInt())).thenReturn(null);

        Drm result = fixture.findDrmByUserAndMedia(user, media, null, true);

        assertNull(result);
    }

    /**
     * Run the Drm findDrmByUserAndMedia(User userWithCommunity, Media media, DrmPolicy drmPolicy) method test by null media.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindDrmByUserAndMedia_ByNullMedia_Failure() throws Exception {
        User user = new User();
        user.setId(1);

        fixture.findDrmByUserAndMedia(user, null, null, true);
    }

    /**
     * Run the Drm findDrmByUserAndMedia(User userWithCommunity, Media media, DrmPolicy drmPolicy) method test by null userWithCommunity.
     *
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindDrmByUserAndMedia_ByNullUser_Failure() throws Exception {
        Media media = new Media();
        media.setI(1);

        fixture.findDrmByUserAndMedia(null, media, null, true);
    }

    /**
     * Perform pre-test initialization.
     *
     * @throws Exception if the initialization fails for some reason
     */
    @Before
    public void setUp() throws Exception {

        fixture = new DrmService();
        fixture.setDrmRepository(drmRepository);
    }

    /**
     * Perform post-test clean-up.
     *
     * @throws Exception if the clean-up fails for some reason
     */
    @After
    public void tearDown() throws Exception {
    }
}