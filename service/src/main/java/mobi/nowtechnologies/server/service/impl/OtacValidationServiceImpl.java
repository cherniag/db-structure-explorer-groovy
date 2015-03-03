package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.OtacValidationService;
import mobi.nowtechnologies.server.service.impl.details.ProviderDetailsExtractor;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * User: Titov Mykhaylo (titov) 27.09.13 14:42
 */
public class OtacValidationServiceImpl implements OtacValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtacValidationServiceImpl.class);

    private static final String PROVIDER_DETAILS_EXTRACTOR_BEAN_NAME = "providerDetailsExtractor.beanName";


    @Resource(name = "serviceMessageSource")
    private CommunityResourceBundleMessageSource messageSource;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber, Community community) {
        LOGGER.info("Attempt to validate otac [{}] for [{}] phone number and community [{}]", otac, phoneNumber, community.getName());
        ProviderDetailsExtractor detailsExtractor = resolveDetailsExtractorForCommunity(community);
        if (detailsExtractor != null) {
            return detailsExtractor.getUserDetails(otac, phoneNumber, community);
        }
        else {
            throw new UnsupportedOperationException("Details extractor is not specified for community [" + community + "]");
        }
    }


    public ProviderDetailsExtractor resolveDetailsExtractorForCommunity(Community community) {
        String beanName = messageSource.getMessage(community.getName(), PROVIDER_DETAILS_EXTRACTOR_BEAN_NAME, null, null);
        if (!isEmpty(beanName) && !beanName.equals(PROVIDER_DETAILS_EXTRACTOR_BEAN_NAME)) {
            LOGGER.info("Use bean for validation: {}", beanName);
            return (ProviderDetailsExtractor) applicationContext.getBean(beanName);
        }
        return null;
    }
}