package mobi.nowtechnologies.applicationtests.features.referrals
import cucumber.api.Transform
import cucumber.api.java.After
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.db.UserDbService
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator
import mobi.nowtechnologies.server.dto.ReferralDto
import mobi.nowtechnologies.server.persistence.domain.referral.Referral
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository
import mobi.nowtechnologies.server.shared.enums.ProviderType
import org.junit.Assert
import org.springframework.stereotype.Component

import javax.annotation.Resource

import static junit.framework.Assert.assertEquals
/**
 * Author: Gennadii Cherniaiev
 * Date: 11/25/2014
 */
@Component
class SendReferralsFeature {
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

    def userDeviceDatas = [] as List
    def toSend = [:]
    def responses = [:]
    def stateBeforeCommands = [:];

    @Given('^Activated user with (.+) using (.+) for (.+) above (.+) and (.+)$')
    def given(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String aboveVersion,
            @Transform(DictionaryTransformer.class) Word communities) {

        // cache
        if(userDeviceDatas.isEmpty()) {
            def above = ApiVersions.from(versions.list()).above(aboveVersion)
            userDeviceDatas = userDeviceDataService.table(above, communities.set(), deviceTypes.set(), RequestFormat.from(formats.set()))

            userDeviceDatas.each {
                firstUserDeviceSet.singup(it)
                firstUserDeviceSet.loginUsingFacebook(it)
                toSend[it] = new ReferralDto()
            }
        }
    }

    @When('User wants to send new referral with (.+) and (.+) for specified community')
    def "User wants to send new referral with <Source> and <Contact> for specified community"(ProviderType source, ExistingContactValue contact){
        userDeviceDatas.each {
            def state = firstUserDeviceSet.getPhoneState(it);

            toSend[it].source = source;
            toSend[it].id = (contact == ExistingContactValue.SameAsOwner) ? state.lastFacebookInfo.userDetails.facebookId : "another.facebook.id." + System.nanoTime();
        }
    }

    @And('contact (.+) was in database before with (.+) for this community')
    def "contact <ExistingContact> was in database before with <Source> for this community"(@Transform(NullableStringTransformer.class) NullableString contact, ProviderType providerType){
        userDeviceDatas.each {
            if(!contact.isNull()) {
                if(ExistingContactValue.SameAsContact == contact.value(ExistingContactValue)) {
                    def state = firstUserDeviceSet.getPhoneState(it);
                    def user = userDbService.findUser(state, it)

                    def r = new Referral()
                    r.setCommunityId(user.userGroup.community.id)
                    r.setUserId(user.getId())
                    r.setContact(toSend[it].id)
                    r.setProviderType(providerType)

                    referralRepository.save(r)
                }
            }
        }
    }

    @Then('User invokes post referral command')
    def "User invokes post referral command"(){
        userDeviceDatas.each {
            def state = firstUserDeviceSet.getPhoneState(it);
            def user = userDbService.findUser(state, it)

            final def userId = user.id;
            final def communityId = user.userGroup.community.id

            stateBeforeCommands[it] = referralRepository.findByCommunityIdUserId(communityId, userId)
        }

        userDeviceDatas.each {
            def dto = toSend[it];

            def responseEntity = firstUserDeviceSet.postReferrals(it, [dto]);
            responses[it] = responseEntity
        }
    }

    @Then('Response has (.+) http response code')
    def "Response has 200 http response code"(int code) {
        userDeviceDatas.each {
            assertEquals("Response for data $it should be $code but was ${responses[it].statusCode.value}", code, responses[it].statusCode.value)
        }
    }

    @And('it should (.+) with (.+), (.+) and (.+) for this user and community')
    def "it should <Exists> with <New Contact>, <Provider> and <State> for this user and community"(
            boolean exists,
            @Transform(NullableStringTransformer.class) NullableString newContact,
            @Transform(NullableStringTransformer.class) NullableString providerType,
            @Transform(NullableStringTransformer.class) NullableString state){
        userDeviceDatas.each {
            def before = stateBeforeCommands[it] as List;

            def s = firstUserDeviceSet.getPhoneState(it);
            def user = userDbService.findUser(s, it)

            final def userId = user.id;
            final def communityId = user.userGroup.community.id

            // assertions:
            def acrtualContacts = referralRepository.findByCommunityIdUserIdAndContact(communityId, userId, toSend[it].id)

            // existence
            Assert.assertEquals(exists, !acrtualContacts.isEmpty())

            // contact value
            if(!newContact.isNull()) {
                def newContct = newContact.value(ExistingContactValue)
                if(newContct == ExistingContactValue.SameAsBefore) {
                    Assert.assertEquals(before[0].contact, acrtualContacts[0].contact)
                }
                if(newContct == ExistingContactValue.SameAsContact) {
                    Assert.assertEquals(toSend[it].id, acrtualContacts[0].contact)
                }
            }

            // provider
            if(!providerType.isNull()) {
                def provider = providerType.value(ProviderType)
                Assert.assertEquals(provider, acrtualContacts[0].providerType)
            }

            // state
            if(!state.isNull()) {
                if(state.belongs(ExistingContactValue)) {
                    def sb = state.value(ExistingContactValue)
                    if(sb == ExistingContactValue.SameAsBefore) {
                        Assert.assertEquals(before[0].providerType, acrtualContacts[0].providerType)
                    }
                }

                if(state.belongs(ProviderType)) {
                    def pr = state.value(ProviderType)
                    Assert.assertEquals(pr, acrtualContacts[0].providerType)
                }
            }
        }
    }

    @After
    def cleanUp(){
        responses.clear()
    }
}
