package mobi.nowtechnologies.server.transport.referrals;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.shared.enums.ProviderType;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReferralAsmTest {

    @InjectMocks
    private ReferralAsm referralAsm;

    @Test
    public void testFromIncomingDto() throws Exception {
        ReferralDto r1 = mock(ReferralDto.class);
        when(r1.getId()).thenReturn("contact1@dot.com");
        when(r1.getSource()).thenReturn(ProviderType.FACEBOOK);
        ReferralDto r2 = mock(ReferralDto.class);
        when(r2.getId()).thenReturn("contact2@dot.com");
        when(r2.getSource()).thenReturn(ProviderType.GOOGLE_PLUS);

        User user = mock(User.class);
        when(user.getCommunityId()).thenReturn(17);
        when(user.getId()).thenReturn(111);

        List<Referral> referrals = referralAsm.fromDtos(Lists.newArrayList(r1, r2), user);

        Referral referral1 = referrals.get(0);
        assertEquals("contact1@dot.com", referral1.getContact());
        assertEquals(111, referral1.getUserId());
        assertEquals(17, referral1.getCommunityId());
        assertEquals(ProviderType.FACEBOOK, referral1.getProviderType());

        Referral referral2 = referrals.get(1);
        assertEquals("contact2@dot.com", referral2.getContact());
        assertEquals(111, referral2.getUserId());
        assertEquals(17, referral2.getCommunityId());
        assertEquals(ProviderType.GOOGLE_PLUS, referral2.getProviderType());
    }
}