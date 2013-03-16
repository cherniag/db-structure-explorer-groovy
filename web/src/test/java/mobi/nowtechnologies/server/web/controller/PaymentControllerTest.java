package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentControllerTest {

    public static class Users {

        public static User LIMITED = mock(User.class);
        public static User SUBSCRIBED = mock(User.class);
        public static User FREE_TRIAL = mock(User.class);
        public static User OVERDUE_LIMITED = mock(User.class);
        public static User UNSUBSCRIBED_WITH_FULL_ACCESS = mock(User.class);
        public static User OVERDUE = mock(User.class);
        public static User SUBSCRIBED_VIA_INAPP = mock(User.class);
        public static User TRIAL_EXPIRED = mock(User.class);

        static {
            when(LIMITED.isLimited()).thenReturn(true);
            when(SUBSCRIBED.isSubscribedStatus()).thenReturn(true);
            when(FREE_TRIAL.isOnFreeTrial()).thenReturn(true);
            when(OVERDUE_LIMITED.isLimitedAfterOverdue()).thenReturn(true);
            when(UNSUBSCRIBED_WITH_FULL_ACCESS.isUnsubscribedWithFullAccess()).thenReturn(true);
            when(OVERDUE.isOverdue()).thenReturn(true);
            when(SUBSCRIBED_VIA_INAPP.isSubscribedViaInApp()).thenReturn(true);
            when(TRIAL_EXPIRED.isTrialExpired()).thenReturn(true);
        }
    }

    @Test
    public void givenSubscribedO2User_whenGetAccountNotesForInAppMobilePage_willReturnMessageForSubscribedUsers() {
        //given
        final String messageCode = "pays.page.note.account.subscribed";
        PaymentsController controller = new PaymentsController();
        //when
        String code = controller.getMessageCodeForAccountNotes(Users.SUBSCRIBED);
        //then
        assertThat(code, equalTo(messageCode));
    }

    @Test
    public void givenLimitedO2User_whenGetAccountNotesForInAppMobilePage_willReturnMessageForLimitedUsers() {
        //given
        final String messageCode = "pays.page.note.account.limited";
        PaymentsController controller = new PaymentsController();
        //when
        String code = controller.getMessageCodeForAccountNotes(Users.LIMITED);
        //then
        assertThat(code, equalTo(messageCode));
    }

    @Test
    public void givenO2UserOnFreeTrial_whenGetAccountNotesForInAppMobilePage_willReturnMessageForUsersOnFreeTrial() {
        //given
        final String messageCode = "pays.page.note.account.freetrial";
        PaymentsController controller = new PaymentsController();
        //when
        String code = controller.getMessageCodeForAccountNotes(Users.FREE_TRIAL);
        //then
        assertThat(code, equalTo(messageCode));

    }

}
