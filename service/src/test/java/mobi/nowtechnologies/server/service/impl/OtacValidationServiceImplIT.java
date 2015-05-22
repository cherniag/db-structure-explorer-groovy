package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.impl.details.NullProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.impl.details.O2ProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.impl.details.ProviderDetailsExtractor;
import mobi.nowtechnologies.server.service.impl.details.VfNzProviderDetailsExtractor;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 * User: Titov Mykhaylo (titov) 30.09.13 17:20
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class OtacValidationServiceImplIT {


    @Resource
    private OtacValidationServiceImpl otacValidationService;

    @Resource
    private CommunityRepository communityRepository;


    @Resource
    private O2ProviderDetailsExtractor o2ProviderDetailsExtractor;


    @Resource
    private VfNzProviderDetailsExtractor vfnzProviderDetailsExtractor;


    @Resource
    private NullProviderDetailsExtractor alwaysNullDetailsExtractor;


    private void resolveExtractorForCommunity(Community community, ProviderDetailsExtractor detailsExtractor) {
        assertTrue(detailsExtractor == otacValidationService.resolveDetailsExtractorForCommunity(community));
    }

    @Test
    public void checkResolvingForCommunity() {
        resolveExtractorForCommunity(communityRepository.findByName("o2"), o2ProviderDetailsExtractor);
        resolveExtractorForCommunity(communityRepository.findByName("vf_nz"), vfnzProviderDetailsExtractor);
        resolveExtractorForCommunity(new Community().withName("ANYTHING"), null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkNoDetailsExtractorForCommunity() {
        otacValidationService.validate("AA", "AA", new Community().withName("ANYTHING"));
    }

}
