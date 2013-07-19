package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.DataToDoRefundRepository;
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
    DataToDoRefundServiceImpl dataToDoRefundService;
    DataToDoRefundRepository dataToDoRefundRepositoryMock;
    long expectedLogTimeMillis;
    DataToDoRefund resultDataToDoRefund;
    int nextSubPayment;
    Tariff userTariff;
    Tariff newUserTariff;

    @Before
    public void setUp(){
        dataToDoRefundRepositoryMock = mock(DataToDoRefundRepository.class);

        dataToDoRefundService = new DataToDoRefundServiceImpl();
        dataToDoRefundService.setDataToDoRefundRepository(dataToDoRefundRepositoryMock);
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
        assertNotNull(resultDataToDoRefund);
        assertEquals(user, resultDataToDoRefund.user);
        assertEquals(user.getCurrentPaymentDetails(), resultDataToDoRefund.paymentDetails);
        assertEquals(expectedLogTimeMillis, resultDataToDoRefund.logTimeMillis);
        assertEquals(user.getNextSubPayment() * 1000L, resultDataToDoRefund.nextSubPaymentMillis);
    }

    private void verifyUnsuccessfulCase() {
        assertNotNull(resultDataToDoRefund);
        assertTrue(resultDataToDoRefund instanceof DataToDoRefund.NullObjectDataToDoRefund);
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
        when(dataToDoRefundRepositoryMock.save(any(DataToDoRefund.class))).thenAnswer(new Answer<DataToDoRefund>() {
            @Override
            public DataToDoRefund answer(InvocationOnMock invocation) throws Throwable {
                DataToDoRefund arg = (DataToDoRefund) invocation.getArguments()[0];
                return arg;
            }
        });

        resultDataToDoRefund = dataToDoRefundService.logOnTariffMigration(user);

        validate();
    }

    @Test
    public void testLogOnTariffMigration_nextSubPaymentInThePast_Success() throws Exception {
        nextSubPayment = Integer.MIN_VALUE;
        prepareDataWithDifTariffs();

        expectedLogTimeMillis = Long.MAX_VALUE;
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(expectedLogTimeMillis);
        when(dataToDoRefundRepositoryMock.save(any(DataToDoRefund.class))).thenAnswer(new Answer<DataToDoRefund>() {
            @Override
            public DataToDoRefund answer(InvocationOnMock invocation) throws Throwable {
                DataToDoRefund arg = (DataToDoRefund) invocation.getArguments()[0];
                return arg;
            }
        });

        resultDataToDoRefund = dataToDoRefundService.logOnTariffMigration(user);

        verifyUnsuccessfulCase();
    }

    @Test
    public void testLogOnTariffMigration_NoTariffMigration_Success() throws Exception {
        nextSubPayment = Integer.MAX_VALUE;
        prepareDataWithTheSameTariffs();

        expectedLogTimeMillis = Long.MAX_VALUE;
        mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(expectedLogTimeMillis);
        when(dataToDoRefundRepositoryMock.save(any(DataToDoRefund.class))).thenAnswer(new Answer<DataToDoRefund>() {
            @Override
            public DataToDoRefund answer(InvocationOnMock invocation) throws Throwable {
                DataToDoRefund arg = (DataToDoRefund) invocation.getArguments()[0];
                return arg;
            }
        });

        resultDataToDoRefund = dataToDoRefundService.logOnTariffMigration(user);

        verifyUnsuccessfulCase();
    }
}
