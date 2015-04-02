package mobi.nowtechnologies.server.service.nz;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

import org.junit.*;
import org.mockito.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NZSubscriberInfoServiceTest {
    @Mock
    NZSubscriberInfoRepository subscriberInfoRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    NZSubscriberInfoProvider subscriberInfoProvider;
    @InjectMocks
    NZSubscriberInfoService nzSubscriberInfoService;

    @Test
    public void testConfirm() throws Exception {
        MockitoAnnotations.initMocks(this);

        Integer id = 1;
        User user = mock(User.class);
        NZSubscriberInfo existing = mock(NZSubscriberInfo.class);
        NZSubscriberInfo forNewOne = mock(NZSubscriberInfo.class);

        when(user.getId()).thenReturn(id);

        String msisdn = "64123456789";

        when(subscriberInfoRepository.findSubscriberInfoByUserId(id)).thenReturn(existing);
        when(subscriberInfoRepository.findSubscriberInfoByMsisdn(msisdn)).thenReturn(forNewOne);

        nzSubscriberInfoService.confirm(user, msisdn);

        verify(existing).unassignUser();
        verify(forNewOne).setUserId(id);
        verify(user).setMobile(eq("+" + msisdn));
    }
}
