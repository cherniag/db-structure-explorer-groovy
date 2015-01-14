package mobi.nowtechnologies.applicationtests.features.social.email;

import com.google.common.collect.Sets;
import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.mail.EmailChecker;
import mobi.nowtechnologies.applicationtests.services.runner.Invoker;
import mobi.nowtechnologies.applicationtests.services.runner.Runner;
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService;
import mobi.nowtechnologies.server.persistence.apptests.domain.Email;
import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@Component
public class EmailSuccessRegistrationFeature {
    @Resource
    private UserDbService userDbService;
    @Resource
    private UserDeviceDataService userDeviceDataService;
    @Resource
    private ActivationEmailRepository activationEmailRepository;
    @Resource
    private EmailChecker emailChecker;
    @Resource
    private RunnerService runnerService;
    @Resource
    private MQAppClientDeviceSet deviceSet;

    private List<UserDeviceData> currentUserDevices;

    private Map<UserDeviceData, User> lastRegisteredUsers = new ConcurrentHashMap<UserDeviceData, User>();
    private Runner runner;

    @Given("^First time user with device using (\\w+) format for (.+) and (.+) and (.+) available$")
    public void firstTimeUserUsingJsonAndXmlFormats(RequestFormat format,
                                                    @Transform(DictionaryTransformer.class) Word versions,
                                                    @Transform(DictionaryTransformer.class) Word communities,
                                                    @Transform(DictionaryTransformer.class) Word devices) throws Throwable {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), Sets.newHashSet(format));
        runner = runnerService.create(currentUserDevices);
    }

    @When("^User signs up the device$")
    public void userSignsUpTheDevice() throws Throwable {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                deviceSet.singup(deviceData);
            }
        });
    }

    @Then("^Temporary registration info is available and the status is (\\w+) and username is the same as device uid$")
    public void temporaryRegistrationInfoIsAvailable(final UserStatus status) throws Throwable {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                PhoneState phoneState = deviceSet.getPhoneState(deviceData);
                assertFalse(phoneState.getLastAccountCheckResponse().userToken.isEmpty());

                User user = userDbService.findUser(phoneState, deviceData);
                assertEquals(user.getStatus().getName(), status.name());
                assertEquals(phoneState.getDeviceUID(), user.getDeviceUID());
            }
        });
    }

    @When("^User mobile calls server to generate the email$")
    public void userMobileGenerateEmail() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                deviceSet.registerEmail(deviceData);
            }
        });
    }

    @Then("^registration info is in (\\w+) status$")
    public void registrationInfoIsInStatus(final ActivationStatus activationStatus) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                PhoneState phoneState = deviceSet.getPhoneState(deviceData);

                User user = userDbService.findUser(phoneState, deviceData);
                assertEquals(activationStatus, user.getActivationStatus());

                lastRegisteredUsers.put(deviceData, user);
            }
        });
    }

    @And("^email was sent$")
    public void emailWasSent() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                PhoneState phoneState = deviceSet.getPhoneState(deviceData);

                // email was sent:
                ActivationEmail activationEmail = activationEmailRepository.findOne(phoneState.getLastActivationEmailToken());
                assertFalse(activationEmail.isActivated());

                // email text contains correct parameters to sign in by email: id and token
                Pair<String, String> idTokenPair = extractIdAndTokenFromTheEmailText(
                        findSentEmailText(activationEmail)
                );

                assertEquals(activationEmail.getId().toString(), idTokenPair.getLeft());
                assertEquals(activationEmail.getToken(), idTokenPair.getRight());
            }
        });
    }

    @When("^User hits the link in email$")
    public void userHitsTheLinkInEmail() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                PhoneState phoneState = deviceSet.getPhoneState(deviceData);

                String emailText = userOpensEmailText(phoneState);

                Pair<String, String> idTokenPair = extractIdAndTokenFromTheEmailText(emailText);
                String idFromTheLinkInEmailText = idTokenPair.getLeft();
                String tokenFromTheLinkInEmailText = idTokenPair.getRight();

                deviceSet.signInEmail(deviceData, idFromTheLinkInEmailText, tokenFromTheLinkInEmailText);
            }
        });
    }

    @Then("^User has (\\w+) status$")
    public void userHasRegistrationInfoInSubscriberStatus(final UserStatus userStatus) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                PhoneState phoneState = deviceSet.getPhoneState(deviceData);

                User user = userDbService.findUser(phoneState, deviceData);
                assertEquals(userStatus.name(), user.getStatus().getName());
            }
        });
    }

    @And("^device uid of previous user contains (\\w+) value$")
    public void deviceUidContains(final String smth) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                User prevUser = lastRegisteredUsers.get(deviceData);

                User user = userDbService.getUserByUserNameAndCommunity(prevUser.getUserName(), deviceData.getCommunityUrl());
                assertTrue(user.getDeviceUID().contains(smth));
            }
        });
    }

    @And("^user changes email$")
    public void whenUserChangesEmail() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                deviceSet.changeEmail(deviceData);
            }
        });
    }

    @And("^user changes device$")
    public void whenUserChangesDevice() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                deviceSet.changePhone(deviceData);
            }
        });
    }

    @And("^user has username as new email$")
    public void userChangedUserName() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData deviceData) {
                PhoneState phoneState = deviceSet.getPhoneState(deviceData);
                User user = userDbService.findUser(phoneState, deviceData);
                assertEquals(phoneState.getEmail(), user.getUserName());
            }
        });
    }

    @After
    public void cleanup() {
        deviceSet.cleanup();
        lastRegisteredUsers.clear();
    }

    //
    // Internals
    //
    private Pair<String, String> extractIdAndTokenFromTheEmailText(String text) {
        String href = extractLink(text);

        MultiValueMap<String, String> queryParams = extractQueryParams(href);

        String id = queryParams.get(ActivationEmail.ID).get(0);
        String token = queryParams.get(ActivationEmail.TOKEN).get(0);

        return new ImmutablePair<String, String>(id, token);
    }

    private String userOpensEmailText(PhoneState phoneState) {
        ActivationEmail activationEmail = activationEmailRepository.findOne(phoneState.getLastActivationEmailToken());
        return findSentEmailText(activationEmail);
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

    private String findSentEmailText(ActivationEmail activationEmail) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(ActivationEmail.ID, activationEmail.getId().toString());
        params.put(ActivationEmail.TOKEN, activationEmail.getToken());

        Email byModel = emailChecker.findByModel(params);
        return byModel.getBody();
    }
}
