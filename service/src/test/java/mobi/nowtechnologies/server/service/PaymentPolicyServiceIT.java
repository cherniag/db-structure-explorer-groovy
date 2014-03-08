package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Titov Mykhaylo (titov)
 *         08.03.14 19:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentPolicyServiceIT {

    public static Logger LOGGER = LoggerFactory.getLogger(PaymentPolicyServiceIT.class);

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
    public void setUp(){
        o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        vfUserGroup = userGroupRepository.findByCommunityRewriteUrl("vf_nz");

        o2Community = o2UserGroup.getCommunity();
        vfCommunity = vfUserGroup.getCommunity();

        deletePaymentPolicies();

        paymentPolicy92 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)0).withSubCost(new BigDecimal("4.99")
        ).withPaymentType("creditCard").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_O2)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy93 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)0).withSubCost(new BigDecimal("4.99")
        ).withPaymentType("PAY_PAL").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_O2)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy95 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)0).withSubCost(new BigDecimal("4.99")
        ).withPaymentType("iTunesSubscription").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId("com.musicqubed.o2.subscription").withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_O2)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy96 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)5).withSubCost(new BigDecimal("5")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null)
                .withContract(null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107056").withContentDescription("Description of content").withSubMerchantId("O2 Tracks")
                .withProvider(O2).withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy97 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)2).withSubCost(new BigDecimal("2")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null)
                .withContract(null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107055").withContentDescription("Description of content").withSubMerchantId("O2 Tracks")
                .withProvider(O2).withTariff(_3G).withMediaType(AUDIO).withDefault(true));

        paymentPolicy98 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)1).withSubCost(new BigDecimal("1")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null)
                .withContract(null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107054").withContentDescription("Description of content").withSubMerchantId("O2 Tracks")
                .withProvider(O2).withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy99 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)5).withSubCost(new BigDecimal("5")
        ).withPaymentType("creditCard").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(BUSINESS).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(O2)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy100 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)5).withSubCost(new BigDecimal("5")
        ).withPaymentType("PAY_PAL").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(BUSINESS).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(O2)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy101 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)5).withSubCost(new BigDecimal("5")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107056").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                .withTariff(_4G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy102 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)2).withSubCost(new BigDecimal("2")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107055").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                .withTariff(_4G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy103 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)1).withSubCost(new BigDecimal("1")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107054").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                .withTariff(_4G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy104 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)3).withSubCost(new BigDecimal("4.5")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107059").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                .withTariff(_4G).withMediaType(VIDEO_AND_AUDIO).withDefault(false));

        paymentPolicy105 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)2).withSubCost(new BigDecimal("3")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107058").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                .withTariff(_4G).withMediaType(VIDEO_AND_AUDIO).withDefault(false));

        paymentPolicy106 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(o2Community).withSubWeeks((byte)1).withSubCost(new BigDecimal("1.5")
        ).withPaymentType("o2Psms").withOperator(null).withShortCode("").withCurrencyISO("GBP").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(CONSUMER).withContentCategory("other").withContentType("mqbed_tracks_3107057").withContentDescription("Description of content").withSubMerchantId(null).withProvider(O2)
                .withTariff(_4G).withMediaType(VIDEO_AND_AUDIO).withDefault(true));

        paymentPolicy107 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(vfCommunity).withSubWeeks((byte) 4).withSubCost(new BigDecimal("8.29")
        ).withPaymentType("PAY_PAL").withOperator(null).withShortCode("").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_VF)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy108 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(vfCommunity).withSubWeeks((byte)4).withSubCost(new BigDecimal("8.29")
        ).withPaymentType("iTunesSubscription").withOperator(null).withShortCode("").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId("com.musicqubed.vfnz.ios").withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(NON_VF)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy109 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(vfCommunity).withSubWeeks((byte) 1).withSubCost(new BigDecimal("1.5")
        ).withPaymentType("vfPsms").withOperator(null).withShortCode("3313").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(VF)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

        paymentPolicy110 = paymentPolicyRepository.save(new PaymentPolicy().withCommunity(vfCommunity).withSubWeeks((byte) 4).withSubCost(new BigDecimal("6")
        ).withPaymentType("vfPsms").withOperator(null).withShortCode("3006").withCurrencyISO("NZD").withAvailableInStore(true).withAppStoreProductId(null).withContract
                (null).withSegment(null).withContentCategory(null).withContentType(null).withContentDescription(null).withSubMerchantId(null).withProvider(VF)
                .withTariff(_3G).withMediaType(AUDIO).withDefault(false));

    }

    private void deletePaymentPolicies() {
        paymentPolicyRepository.deleteAll();

        List<User> users = userRepository.findAll();
        for(User user:users){
            userRepository.save(user.withCurrentPaymentDetails(null));
        }

        paymentDetailsRepository.deleteAll();
    }

    @Test
    public void shouldNotReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsNullAndSegmentIsNull(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(null).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        //then
        assertThat(paymentPolicyDtos.size(), is(0));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsVFAndSegmentIsNull(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(VF).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy110.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy109.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsNonVFAndSegmentIsNull(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(NON_VF).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy107.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy108.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsVFAndSegmentIsBusiness(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(VF).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy110.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy109.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsVFAndSegmentIsConsumer(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(VF).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy110.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy109.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsNonVFAndSegmentIsBusiness(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(NON_VF).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy107.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy108.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityVfNzAndProviderIsNonVFAndSegmentIsConsumer(){
        //given
        User user = new User().withUserGroup(vfUserGroup).withProvider(NON_VF).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy107.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy108.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsNullAndSegmentIsNull(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(null).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsO2AndSegmentIsNull(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(O2).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsO2AndSegmentIsBusiness(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(O2).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderINullAndSegmentIsBusiness(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(null).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(2));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy99.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy100.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderINullAndSegmentIsConsumer(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(null).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy96.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy97.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy98.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsO2AndSegmentIsConsumer(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(O2).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy96.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy97.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy98.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsNonO2AndSegmentIsNull(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(NON_O2).withSegment(null);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy92.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy93.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy95.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsNonO2AndSegmentIsConsumer(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(NON_O2).withSegment(CONSUMER);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy92.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy93.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy95.getId()));
    }

    @Test
    public void shouldReturnPaymentPolicyDtosWhenCommunityIsO2AndProviderIsNonO2AndSegmentIsBusiness(){
        //given
        User user = new User().withUserGroup(o2UserGroup).withProvider(NON_O2).withSegment(BUSINESS);

        //when
        List<PaymentPolicyDto> paymentPolicyDtos = paymentPolicyService.getPaymentPolicyDtos(user);

        LOGGER.info(paymentPolicyDtos.toString());

        //then
        assertThat(paymentPolicyDtos.size(), is(3));
        assertThat(paymentPolicyDtos.get(0).getId(), is(paymentPolicy92.getId()));
        assertThat(paymentPolicyDtos.get(1).getId(), is(paymentPolicy93.getId()));
        assertThat(paymentPolicyDtos.get(2).getId(), is(paymentPolicy95.getId()));
    }
}
