package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.RefundRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * User: Titov Mykhaylo (titov)
 * 16.07.13 9:02
 */
@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = Utils.class)
public class DataToDoRefundServiceImplTest {

    User user;
    RefundServiceImpl dataToDoRefundService;
    RefundRepository refundRepositoryMock;
    long expectedLogTimeMillis;
    Refund resultRefund;
    int nextSubPayment;
    Tariff userTariff;
    Tariff newUserTariff;

    @Before
    public void setUp(){
        refundRepositoryMock = mock(RefundRepository.class);

        dataToDoRefundService = new RefundServiceImpl();
        dataToDoRefundService.setRefundRepository(refundRepositoryMock);
    }

    private void prepareData() {
        user = UserFactory.createUserWithVideoPaymentDetails(userTariff);
        user.setNextSubPayment(nextSubPayment);
        user.setLastSubscribedPaymentSystem("o2Psms");
        user.getCurrentPaymentDetails().setActivated(false);
        user.setTariff(newUserTariff);
    }

    private void prepareDataWithTheSameTariffs() {
        userTariff = Tariff._3G;
        newUserTariff = Tariff._3G;
        prepareData();
    }

    private void prepareDataWithDifTariffs() {
        userTariff = Tariff._3G;
        newUserTariff = Tariff._4G;
        prepareData();
    }

    private void validate() {
        assertNotNull(resultRefund);
        assertEquals(user, resultRefund.user);
        assertEquals(user.getCurrentPaymentDetails(), resultRefund.paymentDetails);
        assertEquals(expectedLogTimeMillis, resultRefund.logTimeMillis);
        assertEquals(user.getNextSubPayment() * 1000L, resultRefund.nextSubPaymentMillis);
    }

    private void verifyUnsuccessfulCase() {
        assertNotNull(resultRefund);
        assertTrue(resultRefund instanceof Refund.NullObjectRefund);
    }

    @Test
    public void testLogOnTariffMigration_from4GVideoSubscriptionTo3GWithNoActivePaymentDetails_Success() throws Exception {
        nextSubPayment = Integer.MAX_VALUE;
        userTariff = Tariff._4G;
        newUserTariff = Tariff._3G;
        prepareData();
        //prepareDataWithDifTariffs();

        expectedLogTimeMillis = Long.MAX_VALUE;
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(expectedLogTimeMillis);
        when(refundRepositoryMock.save(any(Refund.class))).thenAnswer(new Answer<Refund>() {
            @Override
            public Refund answer(InvocationOnMock invocation) throws Throwable {
                Refund arg = (Refund) invocation.getArguments()[0];
                return arg;
            }
        });

        resultRefund = dataToDoRefundService.logOnTariffMigration(user);

        validate();
    }

    @Test
    public void testLogOnTariffMigration_nextSubPaymentInThePast_Success() throws Exception {
        nextSubPayment = Integer.MIN_VALUE;
        prepareDataWithDifTariffs();

        expectedLogTimeMillis = Long.MAX_VALUE;
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(expectedLogTimeMillis);
        when(refundRepositoryMock.save(any(Refund.class))).thenAnswer(new Answer<Refund>() {
            @Override
            public Refund answer(InvocationOnMock invocation) throws Throwable {
                Refund arg = (Refund) invocation.getArguments()[0];
                return arg;
            }
        });

        resultRefund = dataToDoRefundService.logOnTariffMigration(user);

        verifyUnsuccessfulCase();
    }

    @Test
    public void testLogOnTariffMigration_NoTariffMigration_Success() throws Exception {
        nextSubPayment = Integer.MAX_VALUE;
        prepareDataWithTheSameTariffs();

        expectedLogTimeMillis = Long.MAX_VALUE;
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(expectedLogTimeMillis);
        when(refundRepositoryMock.save(any(Refund.class))).thenAnswer(new Answer<Refund>() {
            @Override
            public Refund answer(InvocationOnMock invocation) throws Throwable {
                Refund arg = (Refund) invocation.getArguments()[0];
                return arg;
            }
        });

        resultRefund = dataToDoRefundService.logOnTariffMigration(user);

        verifyUnsuccessfulCase();
    }
}
