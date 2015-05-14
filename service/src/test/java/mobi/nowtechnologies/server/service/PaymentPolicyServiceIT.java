package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.payment.PaymentPolicyDto;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.Contract;
import static mobi.nowtechnologies.common.dto.UserRegInfo.PaymentType.PAY_PAL;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.DAYS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.MONTHS;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.GOOGLE_PLUS;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Titov Mykhaylo (titov) 08.03.14 19:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class PaymentPolicyServiceIT {

    @Resource(name = "service.PaymentPolicyService")
    private PaymentPolicyService paymentPolicyService;

    @Resource(name = "paymentPolicyRepository")
    private PaymentPolicyRepository paymentPolicyRepository;

    @Resource(name = "paymentDetailsRepository")
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    private PaymentPolicy paymentPolicy110;
    private PaymentPolicy paymentPolicy109;
    private PaymentPolicy paymentPolicy108;
    private PaymentPolicy paymentPolicy107;
    private PaymentPolicy paymentPolicy106;
    private PaymentPolicy paymentPolicy105;
    private PaymentPolicy paymentPolicy104;
    private PaymentPolicy paymentPolicy103;
    private PaymentPolicy paymentPolicy102;
    private PaymentPolicy paymentPolicy101;
    private PaymentPolicy paymentPolicy100;
    private PaymentPolicy paymentPolicy99;
    private PaymentPolicy paymentPolicy98;
    private PaymentPolicy paymentPolicy97;
    private PaymentPolicy paymentPolicy96;
    private PaymentPolicy paymentPolicy95;
    private PaymentPolicy paymentPolicy93;
    private PaymentPolicy paymentPolicy92;
    private UserGroup o2UserGroup;
    private UserGroup vfUserGroup;
    private Community o2Community;
    private Community vfCommunity;

    @Before
    public void setUp() {
        o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        vfUserGroup = userGroupRepository.findByCommunityRewriteUrl("vf_nz");

        o2Community = o2UserGroup.getCommunity();
        vfCommunity = vfUserGroup.getCommunity();

        deletePaymentPolicies();

        paymentPolicy92 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("4.99")).withPaymentType("creditCard")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_O2).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

        paymentPolicy93 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("4.99")).withPaymentType("PAY_PAL")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_O2).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

        paymentPolicy95 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("4.99")).withPaymentType("iTunesSubscription")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId("com.musicqubed.o2.subscription").withContract(null)
                               .withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_O2).withTariff(_3G)
                               .withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy96 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(5).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("5")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107056").withContentDescription("Description of content").withSubMerchantId("O2 Tracks").withProvider(O2)
                               .withTariff(_3G).withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy97 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(2).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("2")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107055").withContentDescription("Description of content").withSubMerchantId("O2 Tracks").withProvider(O2)
                               .withTariff(_3G).withMediaType(AUDIO).withDefault(true)).withOnline(true);

        paymentPolicy98 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("1")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107054").withContentDescription("Description of content").withSubMerchantId("O2 Tracks").withProvider(O2)
                               .withTariff(_3G).withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy99 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(5).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("5")).withPaymentType("creditCard")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(BUSINESS)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(O2).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

        paymentPolicy100 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(5).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("5")).withPaymentType("PAY_PAL")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(BUSINESS)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(O2).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

        paymentPolicy101 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(5).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("5")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107056").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                               .withTariff(_4G).withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy102 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(2).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("2")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107055").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                               .withTariff(_4G).withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy103 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("1")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107054").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                               .withTariff(_4G).withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy104 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(3).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("4.5")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107059").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                               .withTariff(_4G).withMediaType(VIDEO_AND_AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy105 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(2).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("3")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107058").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                               .withTariff(_4G).withMediaType(VIDEO_AND_AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy106 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("1.5")).withPaymentType("o2Psms")
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(CONSUMER)
                               .withContentCategory("other").withContentType("mqbed_tracks_3107057").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                               .withTariff(_4G).withMediaType(VIDEO_AND_AUDIO).withDefault(true)).withOnline(true);

        paymentPolicy107 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(vfCommunity).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("8.29")).withPaymentType("PAY_PAL")
                               .withOperator(null).withShortCode("").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_VF).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

        paymentPolicy108 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(vfCommunity).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("8.29")).withPaymentType("iTunesSubscription")
                               .withOperator(null).withShortCode("").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId("com.musicqubed.vfnz.ios").withContract(null)
                               .withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_VF).withTariff(_3G)
                               .withMediaType(AUDIO).withDefault(false)).withOnline(true);

        paymentPolicy109 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(vfCommunity).withPeriod(new Period().withDuration(1).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("1.5")).withPaymentType("vfPsms")
                               .withOperator(null).withShortCode("3313").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(VF).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

        paymentPolicy110 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(vfCommunity).withPeriod(new Period().withDuration(4).withDurationUnit(WEEKS)).withSubCost(new BigDecimal("6")).withPaymentType("vfPsms")
                               .withOperator(null).withShortCode("3006").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(VF).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);

    }

    private void deletePaymentPolicies() {
        paymentPolicyRepository.deleteAll();

        List<User> users = userRepository.findAll();
        for (User user : users) {
            //user.setLastSuccessfulPaymentDetails(null);
            userRepository.save(user.withCurrentPaymentDetails(null));
        }

        paymentDetailsRepository.deleteAll();
    }

    @Test
    public void shouldNotReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsNullAndSegmentIsNullAnd3GTariff() {
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(null).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(0));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsVFAndSegmentIsNullAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(vfUserGroup).withProvider(VF).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy110.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy109.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsNonVFAndSegmentIsNullAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(vfUserGroup).withProvider(NON_VF).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy107.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy108.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsVFAndSegmentIsBusinessAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(vfUserGroup).withProvider(VF).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy110.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy109.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsVFAndSegmentIsConsumerAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(vfUserGroup).withProvider(VF).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy110.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy109.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsNonVFAndSegmentIsBusinessAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(vfUserGroup).withProvider(NON_VF).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy107.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy108.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInVfNzCommunityWithProviderIsNonVFAndSegmentIsConsumerAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(vfUserGroup).withProvider(NON_VF).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy107.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy108.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2CommunitywithProviderIsNullAndSegmentIsNullAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(null).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2CommunityWithProviderIsO2AndSegmentIsNullAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2CommunityWithProviderIsO2AndSegmentIsBusinessAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2CommunityWithProviderINullAndSegmentIsBusinessAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(null).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderINullAndSegmentIsConsumerAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(null).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy96.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy97.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy98.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsConsumerAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy96.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy97.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy98.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsNonO2AndSegmentIsNullAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(NON_O2).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy92.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy93.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy95.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsNonO2AndSegmentIsConsumerAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(NON_O2).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy92.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy93.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy95.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsNonO2AndSegmentIsBusinessAnd3GTariff() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(NON_O2).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy92.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy93.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy95.getId()));
    }

    @Test
    public void shouldNotReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsNullAnd4GTariff() {
        //given
        User user = new User().withTariff(_4G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(0));
    }

    @Test
    public void shouldNotReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsBusinessAnd4GTariff() {
        //given
        User user = new User().withTariff(_4G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(0));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsConsumerAnd4GTariff() {
        //given
        User user = new User().withTariff(_4G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy101.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy102.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy103.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsNullAnd4GTariffAndVideoFreeTrialHasBeenActivatedIsTrue() {
        //given
        User user = new User().withVideoFreeTrialHasBeenActivated(true).withTariff(_4G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(0));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsBusinessAnd4GTariffAndVideoFreeTrialHasBeenActivatedIsTrue() {
        //given
        User user = new User().withVideoFreeTrialHasBeenActivated(true).withTariff(_4G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(0));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForUserInO2WithProviderIsO2AndSegmentIsConsumerAnd4GTariffAndVideoFreeTrialHasBeenActivatedIsTrue() {
        //given
        User user = new User().withVideoFreeTrialHasBeenActivated(true).withTariff(_4G).withUserGroup(o2UserGroup).withProvider(O2).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(6));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy101.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy104.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy102.getId()));
        assertThat(paymentPolicyDtos.get(3).getId(), is(paymentPolicy105.getId()));
        assertThat(paymentPolicyDtos.get(4).getId(), is(paymentPolicy103.getId()));
        assertThat(paymentPolicyDtos.get(5).getId(), is(paymentPolicy106.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForO2PAYGConsumer() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(O2).withContract(PAYG).withSegment(CONSUMER);

        PaymentPolicy o2PAYGConsumer5PoundsPaymentPolicy = newSavedPaymentPolicyAs(paymentPolicy96, PAYG);
        PaymentPolicy o2PAYGConsumer2PoundsPaymentPolicy = newSavedPaymentPolicyAs(paymentPolicy97, PAYG);
        PaymentPolicy o2PAYGConsumer1PoundPaymentPolicy = newSavedPaymentPolicyAs(paymentPolicy98, PAYG);

        turnOffOldO2ConsumerO2PsmsPaymentPolicies();

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(o2PAYGConsumer5PoundsPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(o2PAYGConsumer2PoundsPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(o2PAYGConsumer1PoundPaymentPolicy.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosForO2PAYMConsumer() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(O2).withContract(PAYM).withSegment(CONSUMER);

        PaymentPolicy o2PAYMConsumer5PoundsPaymentPolicy = newSavedPaymentPolicyAs(paymentPolicy96, PAYM);
        PaymentPolicy o2PAYMConsumer2PoundsPaymentPolicy = newSavedPaymentPolicyAs(paymentPolicy97, PAYM);
        PaymentPolicy o2PAYMConsumer1PoundPaymentPolicy = newSavedPaymentPolicyAs(paymentPolicy98, PAYM);

        turnOffOldO2ConsumerO2PsmsPaymentPolicies();

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(o2PAYMConsumer5PoundsPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(o2PAYMConsumer2PoundsPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(o2PAYMConsumer1PoundPaymentPolicy.getId()));
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void testGetPaymentPolicyFor2SamePolicies() {
        PaymentPolicy paymentPolicy1 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("4.99")).withPaymentType(PAY_PAL)
                                                                                       .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                                                                                       .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(GOOGLE_PLUS).withTariff(_3G).withMediaType(AUDIO)
                                                                                       .withDefault(false)).withOnline(true);
        paymentPolicyRepository.save(paymentPolicy1);
        PaymentPolicy paymentPolicy2 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("4.99")).withPaymentType(PAY_PAL)
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(GOOGLE_PLUS).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);
        paymentPolicyRepository.save(paymentPolicy2);
        PaymentPolicy resultPolicy = paymentPolicyService.getPaymentPolicy(o2Community, GOOGLE_PLUS, PAY_PAL);
        assertEquals(resultPolicy, paymentPolicy2);
    }

    @Test
    public void testGetPaymentPolicyFor1Policy() {
        PaymentPolicy paymentPolicy1 = paymentPolicyRepository.save(
            new PaymentPolicy().withCommunity(o2Community).withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withSubCost(new BigDecimal("4.99")).withPaymentType(PAY_PAL)
                               .withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract(null).withSegment(null)
                               .withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(GOOGLE_PLUS).withTariff(_3G).withMediaType(AUDIO)
                               .withDefault(false)).withOnline(true);
        paymentPolicyRepository.save(paymentPolicy1);
        PaymentPolicy resultPolicy = paymentPolicyService.getPaymentPolicy(o2Community, GOOGLE_PLUS, PAY_PAL);
        assertEquals(resultPolicy, paymentPolicy1);
    }

    @Test
    public void shouldReturnSortedPaymentPolicies() {
        //given
        User user = new User().withTariff(_3G).withUserGroup(o2UserGroup).withProvider(O2).withContract(PAYG).withSegment(CONSUMER);

        PaymentPolicy twoMonthsPaymentPolicy = new PaymentPolicy();
        PaymentPolicy sevenWeeksPaymentPolicy = new PaymentPolicy();
        PaymentPolicy fortyDaysPaymentPolicy = new PaymentPolicy();
        PaymentPolicy oneMonthPaymentPolicy = new PaymentPolicy();

        BeanUtils.copyProperties(paymentPolicy96, twoMonthsPaymentPolicy);
        BeanUtils.copyProperties(paymentPolicy96, sevenWeeksPaymentPolicy);
        BeanUtils.copyProperties(paymentPolicy96, fortyDaysPaymentPolicy);
        BeanUtils.copyProperties(paymentPolicy96, oneMonthPaymentPolicy);

        twoMonthsPaymentPolicy =
            paymentPolicyRepository.save(twoMonthsPaymentPolicy.withPeriod(new Period().withDuration(2).withDurationUnit(MONTHS)).withContract(PAYG).withOnline(true).withId(null));
        sevenWeeksPaymentPolicy =
            paymentPolicyRepository.save(sevenWeeksPaymentPolicy.withPeriod(new Period().withDuration(7).withDurationUnit(WEEKS)).withContract(PAYG).withOnline(true).withId(null));
        fortyDaysPaymentPolicy = paymentPolicyRepository.save(fortyDaysPaymentPolicy.withPeriod(new Period().withDuration(40).withDurationUnit(DAYS)).withContract(PAYG).withOnline(true).withId(null));
        oneMonthPaymentPolicy = paymentPolicyRepository.save(oneMonthPaymentPolicy.withPeriod(new Period().withDuration(1).withDurationUnit(MONTHS)).withContract(PAYG).withOnline(true).withId(null));

        turnOffOldO2ConsumerO2PsmsPaymentPolicies();

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(4));
        assertThat(paymentPolicyDtos.get(0).getId(), is(twoMonthsPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(oneMonthPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(sevenWeeksPaymentPolicy.getId()));
        assertThat(paymentPolicyDtos.get(3).getId(), is(fortyDaysPaymentPolicy.getId()));
    }

    private void turnOffOldO2ConsumerO2PsmsPaymentPolicies() {
        paymentPolicy96 = paymentPolicyRepository.save(paymentPolicy96.withOnline(false));
        paymentPolicy97 = paymentPolicyRepository.save(paymentPolicy97.withOnline(false));
        paymentPolicy98 = paymentPolicyRepository.save(paymentPolicy98.withOnline(false));
    }

    private PaymentPolicy newSavedPaymentPolicyAs(PaymentPolicy targetPaymentPolicy, Contract contract) {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        BeanUtils.copyProperties(targetPaymentPolicy, paymentPolicy);
        return paymentPolicyRepository.save(paymentPolicy.withContract(contract).withOnline(true).withId(null));
    }
}
