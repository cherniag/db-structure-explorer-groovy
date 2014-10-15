package mobi.nowtechnologies.applicationtests.features.streamzine;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cucumber.api.Transform;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.ValidType;
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValues;
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValuesTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableString;
import mobi.nowtechnologies.applicationtests.features.common.transformers.util.NullableStringTransformer;
import mobi.nowtechnologies.applicationtests.features.streamzine.transform.AccessPolicyTransformer;
import mobi.nowtechnologies.applicationtests.features.streamzine.transform.IncludedTransformer;
import mobi.nowtechnologies.applicationtests.services.DbMediaService;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.common.standard.StandardResponse;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.GetStreamzineHttpService;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json.ContentItemDto;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json.StreamzimeResponse;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.json.StreamzineUpdateDto;
import mobi.nowtechnologies.applicationtests.services.streamzine.PositionGenerator;
import mobi.nowtechnologies.applicationtests.services.streamzine.StreamzineUpdateCreator;
import mobi.nowtechnologies.applicationtests.services.util.SimpleInterpolator;
import mobi.nowtechnologies.server.dto.streamzine.DeeplinkType;
import mobi.nowtechnologies.server.dto.streamzine.VisualBlock;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.*;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.Opener;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.GrantedToType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.Permission;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@Component
public class GetStreamzineFeature {
    @Resource
    UserDataCreator userDataCreator;
    @Resource
    StreamzineUpdateCreator streamzineUpdateCreator;
    @Resource
    GetStreamzineHttpService getStreamzineHttpService;
    @Resource
    UserDeviceDataService userDeviceDataService;
    @Resource
    SimpleInterpolator interpolator;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    ChartRepository chartRepository;
    @Resource
    DbMediaService dbMediaService;

    @Resource
    MQAppClientDeviceSet deviceSet;

    private List<UserDeviceData> currentUserDevices;

    private Map<UserDeviceData, ResponseEntity<StandardResponse>> errorResponses = new HashMap<UserDeviceData, ResponseEntity<StandardResponse>>();
    private Map<UserDeviceData, StreamzineUpdateDto> okResponses = new HashMap<UserDeviceData, StreamzineUpdateDto>();

    private Map<UserDeviceData, String> spoiledOrNotUserNames = new HashMap<UserDeviceData, String>();

    private Map<UserDeviceData, Update> updates = new HashMap<UserDeviceData, Update>();
    private PositionGenerator positionGenerator = new PositionGenerator();

    private String validResolution = "400x400";

    //
    // Given and After
    //
    @Given("^First time user with device using (.+) format for (.+) and (.+) and for (.+) available$")
    public void firstTimeUserUsingFormat(RequestFormat requestFormat,
                                        @Transform(DictionaryTransformer.class) Word versions,
                                        @Transform(DictionaryTransformer.class) Word communities,
                                        @Transform(DictionaryTransformer.class) Word devices) throws Throwable {
        currentUserDevices = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), Sets.newHashSet(requestFormat));
        for (UserDeviceData data : currentUserDevices) {
            deviceSet.singup(data);
            deviceSet.loginUsingFacebook(data);
        }
        positionGenerator.init(currentUserDevices);
    }

    @After
    public void cleanDevicesSet() {
        okResponses.clear();
        errorResponses.clear();
        deviceSet.cleanup();
    }


    //
    // Error codes Scenario
    //
    @When("^user invokes get streamzine for the (.+), (.+), (.+), (.+) parameters$")
    public void userSendsParameters(@Transform(NullableStringTransformer.class) NullableString nullable,
                                    ValidType timestamp,
                                    ValidType userName,
                                    ValidType userToken) {
        for (UserDeviceData data : currentUserDevices) {
            PhoneState state = deviceSet.getPhoneState(data);
            UserDataCreator.TimestampTokenData token = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);

            String userNameWrongOrCorrect = userName.decide(state.getLastFacebookInfo().getUserName());
            spoiledOrNotUserNames.put(data, userNameWrongOrCorrect);

            ResponseEntity<StandardResponse> response = deviceSet.getStreamzineErrorEntity(
                    data,
                    userToken.decide(token.getTimestampToken()),
                    timestamp.decide(token.getTimestamp()),
                    nullable.value(),
                    userNameWrongOrCorrect);

            errorResponses.put(data, response);
        }
    }

    @Then("^user gets (.+) code in response and (.+), (.+) also (.+) in the message body$")
    public void errorCodeAndMessages(final int httpCode,
                                     final int errorCode,
                                     @Transform(NullableStringTransformer.class) NullableString messageValue,
                                     @Transform(NullableStringTransformer.class) NullableString displayMessageValue) {
        for (UserDeviceData data : currentUserDevices) {
            ResponseEntity<StandardResponse> response = errorResponses.get(data);

            assertEquals(getErrorMessage(data),
                    Integer.valueOf(httpCode),
                    Integer.valueOf(response.getStatusCode().value())
            );

            if(errorCode > 0) {
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("username", spoiledOrNotUserNames.get(data));
                model.put("community", data.getCommunityUrl());

                final String message = interpolator.interpolate(messageValue.value(), model);
                final String displayMessage = interpolator.interpolate(displayMessageValue.value(), model);

                assertEquals(getErrorMessage(data),
                        Integer.valueOf(errorCode),
                        Integer.valueOf(response.getBody().getErrorMessage().getErrorCode())
                );
                assertEquals(getErrorMessage(data),
                        message,
                        response.getBody().getErrorMessage().getMessage()
                );
                assertEquals(getErrorMessage(data),
                        displayMessage,
                        response.getBody().getErrorMessage().getDisplayMessage()
                );
            }
        }

        spoiledOrNotUserNames.clear();
    }

    //
    // Successful Scenario
    //
    @When("^update is prepared$")
    @Transactional(value = "applicationTestsTransactionManager", readOnly = true)
    public void updateIsPrepared() {
        for (UserDeviceData data : currentUserDevices) {
            Community c = communityRepository.findByRewriteUrlParameter(data.getCommunityUrl());
            updates.put(data, new Update(DateUtils.addMilliseconds(new Date(), 100), c));
        }
    }

    @And("^(.+) block (.+) with (.+) access policy, with '(.+)' title and '(.+)' subtitle which contains (.+), (.+) opened '(.+)' in (.+)")
    public void block0(ShapeType shapeType,
                       @Transform(IncludedTransformer.class) Boolean included,
                       @Transform(AccessPolicyTransformer.class) AccessPolicy accessPolicy,
                       @Transform(NullableStringTransformer.class) NullableString title,
                       @Transform(NullableStringTransformer.class) NullableString subTitle,
                       ContentType contentType,
                       LinkLocationType linkLocationType,
                       String url,
                       Opener opener) {
        Assert.isTrue(ContentType.PROMOTIONAL == contentType, "Only " + ContentType.PROMOTIONAL  + " allowed for this test method");

        for (UserDeviceData data : currentUserDevices) {
            NotificationDeeplinkInfo deeplinkInfo = new NotificationDeeplinkInfo(linkLocationType, url, opener);
            Block block = new Block(positionGenerator.nextPosition(data), shapeType, deeplinkInfo);
            block.setTitle(title.value());
            block.setSubTitle(subTitle.value());
            if(included) {
                block.include();
            }
            block.setAccessPolicy(accessPolicy);

            updates.get(data).addBlock(block);
        }
    }

    @And("^(.+) block (.+) with (.+) access policy, with '(.+)' title and '(.+)' subtitle which contains (.+), (.+) with '(.+)' page and '(.+)' action")
    public void block1(ShapeType shapeType,
                       @Transform(IncludedTransformer.class) Boolean included,
                       @Transform(AccessPolicyTransformer.class) AccessPolicy accessPolicy,
                       @Transform(NullableStringTransformer.class) NullableString title,
                       @Transform(NullableStringTransformer.class) NullableString subTitle,
                       ContentType contentType,
                       LinkLocationType linkLocationType,
                       String page,
                       @Transform(NullableStringTransformer.class) NullableString action) {
        Assert.isTrue(ContentType.PROMOTIONAL == contentType, "Only " + ContentType.PROMOTIONAL  + " allowed for this test method");

        for (UserDeviceData data : currentUserDevices) {
            NotificationDeeplinkInfo deeplinkInfo = new NotificationDeeplinkInfo(linkLocationType, page);
            deeplinkInfo.setAction(action.value());

            Block block = new Block(positionGenerator.nextPosition(data), shapeType, deeplinkInfo);
            block.setTitle(title.value());
            block.setSubTitle(subTitle.value());
            block.setAccessPolicy(accessPolicy);
            if(included) {
                block.include();
            }

            updates.get(data).addBlock(block);
        }
    }

    @And("^(.+) block (.+) with (.+) access policy, with '(.+)' title and '(.+)' subtitle which contains (.+), (.+) with '(.+)' isrc and (\\d+) id")
    public void block2(ShapeType shapeType,
                       @Transform(IncludedTransformer.class) Boolean included,
                       @Transform(AccessPolicyTransformer.class) AccessPolicy accessPolicy,
                       @Transform(NullableStringTransformer.class) NullableString title,
                       @Transform(NullableStringTransformer.class) NullableString subTitle,
                       ContentType contentType,
                       MusicType musicType,
                       String isrc,
                       long trackId) {
        Assert.isTrue(ContentType.MUSIC == contentType && MusicType.TRACK == musicType);

        Media media = dbMediaService.findByTrackIdAndIsrc(trackId, isrc);

        for (UserDeviceData data : currentUserDevices) {
            MusicTrackDeeplinkInfo deeplinkInfo = new MusicTrackDeeplinkInfo(media);

            Block block = new Block(positionGenerator.nextPosition(data), shapeType, deeplinkInfo);
            block.setTitle(title.value());
            block.setSubTitle(subTitle.value());
            block.setAccessPolicy(accessPolicy);
            if(included) {
                block.include();
            }

            updates.get(data).addBlock(block);
        }
    }

    @And("^(.+) block (.+) with (.+) access policy, with '(.+)' title and '(.+)' subtitle which contains (.+), (.+) with (.+) ids$")
    @Transactional(value = "applicationTestsTransactionManager", readOnly = true)
    public void block3(ShapeType shapeType,
                       @Transform(IncludedTransformer.class) Boolean included,
                       @Transform(AccessPolicyTransformer.class) AccessPolicy accessPolicy,
                       @Transform(NullableStringTransformer.class) NullableString title,
                       @Transform(NullableStringTransformer.class) NullableString subTitle,
                       ContentType contentType,
                       MusicType musicType,
                       @Transform(ListValuesTransformer.class) ListValues listValues) {
        Assert.isTrue(ContentType.MUSIC == contentType);

        for (UserDeviceData data : currentUserDevices) {
            if(MusicType.PLAYLIST == musicType) {
                Chart chart = chartRepository.findOne(listValues.ints().get(0));
                DeeplinkInfo deeplinkInfo = new MusicPlayListDeeplinkInfo(chart.getI());
                Block block = new Block(positionGenerator.nextPosition(data), shapeType, deeplinkInfo);
                block.setTitle(title.value());
                block.setSubTitle(subTitle.value());
                block.setAccessPolicy(accessPolicy);
                if(included) {
                    block.include();
                }
                updates.get(data).addBlock(block);
            }

            if(MusicType.MANUAL_COMPILATION == musicType) {
                DeeplinkInfo deeplinkInfo = new ManualCompilationDeeplinkInfo(toMedias(listValues.ints()));
                Block block = new Block(positionGenerator.nextPosition(data), shapeType, deeplinkInfo);
                block.setTitle(title.value());
                block.setSubTitle(subTitle.value());
                block.setAccessPolicy(accessPolicy);
                if(included) {
                    block.include();
                }
                updates.get(data).addBlock(block);
            }
        }
    }

    @When("^user invokes get streamzine command$")
    public void userInvokesGetStreamzineCommand() {
        for (UserDeviceData data : currentUserDevices) {
            streamzineUpdateCreator.create(data, updates.get(data));

            PhoneState state = deviceSet.getPhoneState(data);
            UserDataCreator.TimestampTokenData token = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);


            if(data.getFormat() == RequestFormat.JSON) {
                ResponseEntity<StreamzimeResponse> response = deviceSet.getStreamzine(
                        data.getCommunityUrl(),
                        data,
                        token.getTimestampToken(),
                        token.getTimestamp(),
                        validResolution,
                        state.getLastFacebookInfo().getUserName(),
                        StreamzimeResponse.class);

                okResponses.put(data, response.getBody().getResponse().get().getValue());
            } else {
                // XML:
                ResponseEntity<mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.xml.StreamzimeResponse> response = deviceSet.getStreamzine(
                        data.getCommunityUrl(),
                        data,
                        token.getTimestampToken(),
                        token.getTimestamp(),
                        validResolution,
                        state.getLastFacebookInfo().getUserName(),
                        mobi.nowtechnologies.applicationtests.services.http.streamzine.dto.xml.StreamzimeResponse.class);

                okResponses.put(data, response.getBody().getValue());
            }
        }
    }


    //
    // Incorrect community scenario
    //
    @When("^user invokes get streamzine command with incorrect community$")
    public void userInvokesGetStreamzineCommandWithIncorrectCommunity() {
        for (UserDeviceData data : currentUserDevices) {
            PhoneState state = deviceSet.getPhoneState(data);
            UserDataCreator.TimestampTokenData token = userDataCreator.createUserToken(state.getLastAccountCheckResponse().userToken);

            ResponseEntity<StandardResponse> response = deviceSet.getStreamzine(
                    "some_unknown_community",
                    data,
                    token.getTimestampToken(),
                    token.getTimestamp(),
                    validResolution,
                    state.getLastFacebookInfo().getUserName(),
                    StandardResponse.class);

            errorResponses.put(data, response);
        }
    }

    @Then("^user gets (.+) code in response$")
    public void userGetsHttpErrorCodeInResponse(final int code) {
        for (UserDeviceData data : currentUserDevices) {
            ResponseEntity<StandardResponse> response = errorResponses.get(data);
            assertEquals(getErrorMessage(data) + ", body: " + response.getBody(), code, response.getStatusCode().value());
        }
    }

    @And("^block on (\\d) position is (.+), (.+), \\[(.+)\\] with (.+) permission granted to (.+)$")
    public void checkResultWithPermissions(int position,
                             ShapeType shape,
                             DeeplinkType deeplinkType,
                             String deeplinkValue,
                             Permission permission,
                             @Transform(ListValuesTransformer.class) ListValues grantedToTypes) {
        doCheckResult(position, shape, deeplinkType, ListValues.from(deeplinkValue), permission, grantedToTypes.enums(GrantedToType.class));
    }

    @And("^block on (\\d) position is (.+), (.+), \\[(.+)\\] with no permissions$")
    public void checkResultWithNoPermissions(int position,
                                           ShapeType shape,
                                           DeeplinkType deeplinkType,
                                           @Transform(ListValuesTransformer.class) ListValues deeplinkValue) {
        doCheckResult(position, shape, deeplinkType, deeplinkValue, null, null);
    }

    @And("^block on (\\d) has title equal to (.+)$")
    public void checkTitlesRules(int position, @Transform(NullableStringTransformer.class) NullableString title) {
        for (UserDeviceData data : currentUserDevices) {
            Pair<VisualBlock, ContentItemDto> pair = okResponses.get(data).get(position - 1);
            if(title.isNull()) {
                assertNull(pair.getValue().getTitle());
            } else {
                assertEquals(getErrorMessage(data), title.value(), pair.getValue().getTitle());
            }
        }
    }

    @And("^block on (\\d) has subtitle equal to (.+)$")
    public void checkTitlesAndSubtitleRules(int position, @Transform(NullableStringTransformer.class) NullableString subTitle) {
        for (UserDeviceData data : currentUserDevices) {
            Pair<VisualBlock, ContentItemDto> pair = okResponses.get(data).get(position - 1);
            if(subTitle.isNull()) {
                assertNull(pair.getValue().getSubTitle());
            } else {
                assertEquals(getErrorMessage(data), subTitle.value(), pair.getValue().getSubTitle());
            }
        }
    }

    private void doCheckResult(int position, ShapeType shape, DeeplinkType deeplinkType, ListValues deeplinkValue, Permission permission, List<GrantedToType> grantedToTypes) {
        for (UserDeviceData data : currentUserDevices) {
            Pair<VisualBlock, ContentItemDto> pair = okResponses.get(data).get(position - 1);
            // shape type:
            assertEquals(getErrorMessage(data), shape, pair.getKey().getShapeType());
            // permissions:
            if(permission != null) {
                assertEquals(getErrorMessage(data), permission, pair.getKey().getPolicyDto().getPermission());
                Collections.sort(grantedToTypes);
                Collections.sort(pair.getKey().getPolicyDto().getGrantedTo());
                assertEquals(getErrorMessage(data), grantedToTypes, pair.getKey().getPolicyDto().getGrantedTo());
            }
            // deep link type:
            assertEquals(getErrorMessage(data), deeplinkType, pair.getValue().getLinkType());
            // deep link value(s):
            if(deeplinkType == DeeplinkType.DEEPLINK) {
                assertEquals(getErrorMessage(data), deeplinkValue.firstString(), pair.getValue().getLinkValue().getValue());
            } else {
                assertEquals(getErrorMessage(data), deeplinkValue.ints(), pair.getValue().getLinkValue().getValues());
            }
        }
    }


    //
    // Helpers
    //
    private String getErrorMessage(UserDeviceData data) {
        return "Failed to check for " + data;
    }

    private List<Media> toMedias(List<Integer> longs) {
        return Lists.transform(longs, new Function<Integer, Media>() {
            @Override
            public Media apply(Integer id) {
                return dbMediaService.findById(id);
            }
        });
    }

    Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

}
