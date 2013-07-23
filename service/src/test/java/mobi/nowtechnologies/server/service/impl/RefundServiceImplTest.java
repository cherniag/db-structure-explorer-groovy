package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.RefundRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static mobi.nowtechnologies.server.shared.enums.Tariff.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: Titov Mykhaylo (titov)
 * 16.07.13 9:02
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {Utils.class, User.class})
public class RefundServiceImplTest {

    RefundServiceImpl refundService;
    RefundRepository refundRepositoryMock;
    private User userOnOldBoughtPeriodMock;
    private PaymentPolicy newPaymentPolicyMock;
    private Refund resultRefund;
    private PaymentDetails lastSuccessfulPaymentDetails;
    private Date nextSubPaymentDate;
    private Tariff newUserTariff;

    @Before
    public void setUp(){
        userOnOldBoughtPeriodMock = mock(User.class);
        newPaymentPolicyMock = mock(PaymentPolicy.class);

        refundRepositoryMock = mock(RefundRepository.class);
        refundService = new RefundServiceImpl();
        refundService.setRefundRepository(refundRepositoryMock);
    }

    @Test
    public void shouldLogSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(){

        given().userOnAudionOldBoughtPeriod().and().with4GVideoAudioSubscription(true);
        whenLogSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio();
        then().logSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(){

        given().userNotOnAudionOldBoughtPeriod().and().with4GVideoAudioSubscription(true);
        whenLogSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GNotVideoAudio(){

        given().userOnAudionOldBoughtPeriod().and().with4GVideoAudioSubscription(false);
        whenLogSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
    public void shouldLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G(){
        given().userOn4GVideoAudioBoughtPeriod().and().oldTariff(_4G).and().newUserTariff(_3G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().logSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo4G(){
        given().userOn4GVideoAudioBoughtPeriod().and().oldTariff(_4G).and().newUserTariff(_4G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
     public void shouldDoNotLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom3GTo3G(){
        given().userOn4GVideoAudioBoughtPeriod().and().oldTariff(_3G).and().newUserTariff(_3G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedBoughtPeriodOnTariffMigrationFrom4GTo3G(){
        given().userOnAudionOldBoughtPeriod().and().oldTariff(_4G).and().newUserTariff(_3G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedBoughtPeriodOnTariffMigrationFrom4GTo4G(){
        given().userOnAudionOldBoughtPeriod().and().oldTariff(_4G).and().newUserTariff(_4G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedBoughtPeriodOnTariffMigrationFrom3GTo3G(){
        given().userOnAudionOldBoughtPeriod().and().oldTariff(_3G).and().newUserTariff(_3G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().doNotLogSkippedBoughtPeriod();
    }

    @Test
    public void shouldDoNotLogSkippedBoughtPeriodOnTariffMigrationFrom3GTo4G(){
        given().userOnAudionOldBoughtPeriod().and().oldTariff(_3G).and().newUserTariff(_4G);
        whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G();
        then().doNotLogSkippedBoughtPeriod();
    }

    private void whenLogSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G() {
        mockSaveMethodOfRefundRepository();

        resultRefund = refundService.logSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G(userOnOldBoughtPeriodMock, newUserTariff);
    }

    private RefundServiceImplTest userOn4GVideoAudioBoughtPeriod() {
        simulateBoughtPeriod();

        doReturn(true).when(userOnOldBoughtPeriodMock).isOn4GVideoAudioBoughtPeriod();
        doReturn(false).when(userOnOldBoughtPeriodMock).isOnAudioBoughtPeriod();
        return this;
    }

    private RefundServiceImplTest oldTariff(Tariff oldTariff) {
        doReturn(oldTariff).when(userOnOldBoughtPeriodMock).getTariff();
        return this;
    }
    private RefundServiceImplTest newUserTariff(Tariff newTariff) {
        this.newUserTariff = newTariff;
        return this;
    }

    private void logSkippedBoughtPeriod() {
        assertNotNull(resultRefund);
        assertEquals(userOnOldBoughtPeriodMock, resultRefund.user);
        assertEquals(lastSuccessfulPaymentDetails, resultRefund.paymentDetails);
        assertEquals(nextSubPaymentDate.getTime(), resultRefund.nextSubPaymentMillis);

        verify(refundRepositoryMock, times(1)).save(any(Refund.class));
    }

    private void doNotLogSkippedBoughtPeriod() {
        assertNotNull(resultRefund);
        assertEquals(Refund.NullObjectRefund.class, resultRefund.getClass());

        verify(refundRepositoryMock, times(0)).save(any(Refund.class));
    }


    private void whenLogSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio() {
        mockSaveMethodOfRefundRepository();

        resultRefund = refundService.logSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(userOnOldBoughtPeriodMock, newPaymentPolicyMock);
    }

    private void mockSaveMethodOfRefundRepository() {
        doAnswer(new Answer<Refund>() {

            @Override
            public Refund answer(InvocationOnMock invocation) throws Throwable {
                return (Refund) invocation.getArguments()[0];
            }
        }).when(refundRepositoryMock).save((Refund)any());
    }

    private void with4GVideoAudioSubscription(boolean with4GVideoAudioSubscription) {
        doReturn(with4GVideoAudioSubscription).when(newPaymentPolicyMock).is4GVideoAudioSubscription();
    }

    private RefundServiceImplTest userOnAudionOldBoughtPeriod() {
        simulateBoughtPeriod();

        doReturn(true).when(userOnOldBoughtPeriodMock).isOnAudioBoughtPeriod();
        doReturn(false).when(userOnOldBoughtPeriodMock).isOn4GVideoAudioBoughtPeriod();

        return this;
    }

    private void simulateBoughtPeriod() {
        nextSubPaymentDate = new Date(Long.MAX_VALUE);

        PowerMockito.mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(0L);

        lastSuccessfulPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        doReturn(nextSubPaymentDate).when(userOnOldBoughtPeriodMock).getNextSubPaymentAsDate();
        doReturn(lastSuccessfulPaymentDetails).when(userOnOldBoughtPeriodMock).getLastSuccessfulPaymentDetails();
    }

    private RefundServiceImplTest userNotOnAudionOldBoughtPeriod() {

        nextSubPaymentDate = new Date(Long.MIN_VALUE);

        PowerMockito.mockStatic(Utils.class);
        when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);

        lastSuccessfulPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

        doReturn(nextSubPaymentDate).when(userOnOldBoughtPeriodMock).getNextSubPaymentAsDate();
        doReturn(lastSuccessfulPaymentDetails).when(userOnOldBoughtPeriodMock).getLastSuccessfulPaymentDetails();

        doReturn(false).when(userOnOldBoughtPeriodMock).isOnAudioBoughtPeriod();

        return this;
    }


    private RefundServiceImplTest given(){
        return this;
    }

    private RefundServiceImplTest and(){
        return this;
    }

    private RefundServiceImplTest then(){
        return this;
    }

}
