package mobi.nowtechnologies.applicationtests.features.referrals
import cucumber.api.Transform
import cucumber.api.java.After
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValues
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValuesTransformer
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator
import mobi.nowtechnologies.applicationtests.services.mail.EmailChecker
import mobi.nowtechnologies.server.persistence.apptests.domain.Email
import mobi.nowtechnologies.server.persistence.domain.ActivationEmail
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository
import mobi.nowtechnologies.server.shared.enums.ProviderType
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.commons.lang3.tuple.Pair
import org.junit.Assert
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.Resource
import java.util.regex.Matcher
import java.util.regex.Pattern

import static org.junit.Assert.assertTrue

@Component
class ReferralsTransitionsFeature {
    @Resource
    MQAppClientDeviceSet firstUserDeviceSet
    @Resource
    MQAppClientDeviceSet secondUserDeviceSet
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    UserDataCreator userDataCreator
    @Resource
    ReferralRepository referralRepository
    @Resource
    CommunityRepository communityRepository
    @Resource
    UserDbService userDbService
    @Resource
    ActivationEmailRepository activationEmailRepository
    @Resource
    EmailChecker emailChecker;

    List<ReferralNotation> currentReferrals = [] as List;
    Map<String, ReferralNotation> referralNotationByKey = [:]
    UserDeviceData referringUser
    UserDeviceData referredUser
    def referredUsers = [
            'U1': new UserDeviceData("6.5", "hl_uk", "ANDROID", RequestFormat.JSON),
            'U2': new UserDeviceData("6.5", "hl_uk", "IOS", RequestFormat.JSON)
    ]

    @Given('activated user references (.+)')
    def given(@Transform(ListValuesTransformer.class) ListValues values) {
        referringUser = new UserDeviceData("6.4", "hl_uk", "ANDROID", RequestFormat.JSON);
        firstUserDeviceSet.singup(referringUser)
        firstUserDeviceSet.loginUsingFacebook(referringUser)

        def state = firstUserDeviceSet.getPhoneState(referringUser);
        def user = userDbService.findUser(state, referringUser)

        values.strings().each {
            ReferralNotation notation = ReferralNotation.create(it)
            referralRepository.save(notation.toReferral(user))

            currentReferrals.add(notation)

            referralNotationByKey[notation.key] = notation
        }
   }

    @When('referenced user activates via (.+) with data (.+)')
    def when0(ProviderType providerType, @Transform(ListValuesTransformer.class) ListValues values) {
        values.strings().each {
            def userNotation = UserNotation.create(it)
            activate(providerType, userNotation)
        }

    }

    @Then('in database (.+)')
    def then0(@Transform(ListValuesTransformer.class) ListValues values) {
        def state = firstUserDeviceSet.getPhoneState(referredUser);
        def user = userDbService.findUser(state, referredUser)

        values.strings().each {
            ReferralNotation notation = ReferralNotation.create(it)
            // do not take the contact from notation because it could be absent ( the case for newly created with DUPLICATED status)
            // get the contact basing on provider type from current data:
            def c;

            if(notation.providerType == ProviderType.EMAIL) {
                c = state.email;
            } else {
                if(notation.providerType == ProviderType.FACEBOOK) {
                    c = state.lastFacebookInfo?.userDetails?.facebookId
                } else {
                    // G+
                    c = state.lastGooglePlusInfo?.userDetails?.googlePlusId
                }
            }

            if (c == null) {
                c = referralNotationByKey[notation.key].contact
            }
            def referral = referralRepository.findByContactAndCommunityId(c, user.userGroup.community.id)

            Assert.assertEquals(notation.providerType, referral.providerType)
            Assert.assertEquals(notation.state, referral.state)
        }
    }


    @After
    def cleanUp() {
        firstUserDeviceSet.cleanup();
        currentReferrals.clear()
        referralNotationByKey.clear()
    }

    //
    // Internals
    //
    def activate(ProviderType providerType, UserNotation userNotation) {
            if(providerType == ProviderType.FACEBOOK) {
                def e = referralNotationByKey[userNotation.emailRef];
                def id = referralNotationByKey[userNotation.idRef];

                referredUser = referredUsers[userNotation.key]
                firstUserDeviceSet.singup(referredUser)
                // for the cases when notation reference key is not valid which means any:
                def email =  (e == null) ? "eny.email." + System.nanoTime() : e.contact;
                def contact = (id == null) ? "any.contact." + System.nanoTime() : id.contact;
                firstUserDeviceSet.loginUsingFacebookWithDefinedAccountIdAndEmail(referredUser, email, contact)
            } else if(providerType == ProviderType.EMAIL) {
                def e = referralNotationByKey[userNotation.emailRef];
                def email =  (e == null) ? "any.email." + System.nanoTime() : e.contact;

                referredUser = referredUsers[userNotation.key]
                firstUserDeviceSet.singup(referredUser)
                firstUserDeviceSet.registerEmail(referredUser, email)

                def phoneState = firstUserDeviceSet.getPhoneState(referredUser);
                String emailText = userOpensEmailText(phoneState);
                Pair<String, String> idTokenPair = extractIdAndTokenFromTheEmailText(emailText);
                String idFromTheLinkInEmailText = idTokenPair.getLeft();
                String tokenFromTheLinkInEmailText = idTokenPair.getRight();
                firstUserDeviceSet.signInEmail(referredUser, email, idFromTheLinkInEmailText, tokenFromTheLinkInEmailText);
            }else{
                def e = referralNotationByKey[userNotation.emailRef];
                def id = referralNotationByKey[userNotation.idRef];

                referredUser = referredUsers[userNotation.key]
                firstUserDeviceSet.singup(referredUser)
                // for the cases when notation reference key is not valid which means any:
                def email =  (e == null) ? "eny.email." + System.nanoTime() : e.contact;
                def contact = (id == null) ? "any.contact." + System.nanoTime() : id.contact;
                firstUserDeviceSet.loginUsingGooglePlusWithExactEmailAndGooglePlusId(referredUser, email, contact)
            }
    }

    private Pair<String, String> extractIdAndTokenFromTheEmailText(String text) {
        String href = extractLink(text);

        MultiValueMap<String, String> queryParams = extractQueryParams(href);

        String id = queryParams.get(ActivationEmail.ID).get(0);
        String token = queryParams.get(ActivationEmail.TOKEN).get(0);

        return new ImmutablePair<String, String>(id, token);
    }

    private MultiValueMap<String, String> extractQueryParams(String href) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(href);
        UriComponents build = uriComponentsBuilder.build();
        return build.getQueryParams();
    }

    private String extractLink(String emailBody) {
        Pattern attribPattern = Pattern.compile("<a (\\b[^>]*)>(.+?)</a>");
        Matcher attribMatcher = attribPattern.matcher(emailBody);
        assertTrue(attribMatcher.find());

        String attribs = attribMatcher.group(1);

        Pattern hrefPattern = Pattern.compile("(href)=[\"']?((?:.(?![\"']?\\s+(?:\\S+)=|[>\"']))+.)[\"']?");
        Matcher hrefMatcher = hrefPattern.matcher(attribs);
        assertTrue(hrefMatcher.find());

        return hrefMatcher.group(2);
    }

    private String userOpensEmailText(PhoneState phoneState) {
        ActivationEmail activationEmail = activationEmailRepository.findOne(phoneState.getLastActivationEmailToken());
        return findSentEmailText(activationEmail);
    }

    private String findSentEmailText(ActivationEmail activationEmail) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ActivationEmail.ID, activationEmail.getId().toString());
        params.put(ActivationEmail.TOKEN, activationEmail.getToken());

        Email byModel = emailChecker.findByModel(params);
        return byModel.getBody();
    }


}
