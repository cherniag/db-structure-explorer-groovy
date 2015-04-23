package mobi.nowtechnologies.applicationtests.features.chart
import cucumber.api.Transform
import cucumber.api.java.en.And
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValues
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValuesTransformer
import mobi.nowtechnologies.applicationtests.features.common.transformers.time.TimeTransformer
import mobi.nowtechnologies.applicationtests.services.RequestFormat
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersions
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import mobi.nowtechnologies.applicationtests.services.http.chart.Response
import mobi.nowtechnologies.applicationtests.services.runner.Runner
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService
import mobi.nowtechnologies.server.persistence.domain.Chart
import mobi.nowtechnologies.server.persistence.domain.ChartDetail
import mobi.nowtechnologies.server.persistence.domain.Media
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository
import mobi.nowtechnologies.server.persistence.repository.ChartRepository
import mobi.nowtechnologies.server.shared.dto.ChartDto
import mobi.nowtechnologies.server.shared.enums.ChgPosition
import org.junit.Assert
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.concurrent.ConcurrentHashMap

/**
 * Author: Gennadii Cherniaiev
 * Date: 12/4/2014
 */
@Component
class GetChartFeature {
    @Resource
    UserDeviceDataService userDeviceDataService
    @Resource
    MQAppClientDeviceSet deviceSet
    @Resource
    ChartRepository chartRepository
    @Resource
    ChartDetailRepository chartDetailRepository
    @Resource
    RunnerService runnerService;
    Runner runner;

    Logger logger = LoggerFactory.getLogger(getClass());

    def userDeviceDatas = [] as ArrayList<UserDeviceData>

    ConcurrentHashMap<String, Chart> chartByName = [:];
    ConcurrentHashMap<UserDeviceData, ResponseEntity<Response>> responses = [:];

    def currentChartPosition

    @Given('Activated user with (.+) using (.+) for (.+) above (.+) and (.+) community')
    def given0(
            @Transform(DictionaryTransformer.class) Word deviceTypes,
            @Transform(DictionaryTransformer.class) Word formats,
            @Transform(DictionaryTransformer.class) Word versions,
            String above,
            String community
    ){
        def versionsAbove = ApiVersions.from(versions.list()).above(above)
        userDeviceDatas = userDeviceDataService.table(versionsAbove, community, deviceTypes.list(), formats.set(RequestFormat));

        runner = runnerService.create(userDeviceDatas)

        runner.parallel {
            deviceSet.singup(it)
            deviceSet.loginUsingFacebook(it)
        }

        def charts = chartRepository.findByCommunityName(community)
        charts.each {
            chartByName[it.name] = it
        }
        currentChartPosition = 0
    }

    @And('chart update for \'(.+)\' with medias \'(.+)\' and publish time (.+) exists in db')
    def and0(String chartName,
             @Transform(ListValuesTransformer) ListValues mediaIds,
             @Transform(TimeTransformer) Long publishTime){
        def chartDetailWithoutMedia = new ChartDetail()
        chartDetailWithoutMedia.chart = chartByName[chartName]
        chartDetailWithoutMedia.title = chartName
        chartDetailWithoutMedia.subtitle = chartName
        chartDetailWithoutMedia.position = currentChartPosition++
        chartDetailWithoutMedia.publishTimeMillis = publishTime
        chartDetailRepository.save(chartDetailWithoutMedia)

        def trackPosition = 1
        mediaIds.ints().each {
            def chartDetailWithMedia = new ChartDetail()
            chartDetailWithMedia.chart = chartByName[chartName]
            chartDetailWithMedia.position = trackPosition++
            chartDetailWithMedia.prevPosition = chartDetailWithMedia.position
            chartDetailWithMedia.chgPosition = ChgPosition.NONE
            chartDetailWithMedia.publishTimeMillis = publishTime
            chartDetailWithMedia.media = new Media(i:it)
            chartDetailWithMedia.locked = false
            chartDetailRepository.save(chartDetailWithMedia)
        }

    }

    @When('the client requests (.+) /GET_CHART with resolution \'(.+)\'')
    def when0(HttpMethod httpMethod, String resolution){
        runner.parallel {
            if(it.format == RequestFormat.XML) {
                // FIXME: we have no support for XML
                return;
            }

            def chartResponse = deviceSet.getChart(it, httpMethod, resolution)
            responses << [(it) : chartResponse]

            def body = chartResponse.getBody()

            logger.info("Response for \n{} is \n{}", it, body);

            Assert.assertNotNull(body.response.data[1].value)
        }

    }

    @Then('the response code should be (.+)')
    def then0(int code){
        runner.parallel {
            if(it.format == RequestFormat.XML) {
                // FIXME: we have no support for XML
                return;
            }

            Assert.assertEquals(code, responses[it].statusCode.value())
        }

    }

    @And('count of playlist and tracks returned should be as in the database')
    def and1(){
        runner.parallel{
            if(it.format == RequestFormat.XML) {
                // FIXME: we have no support for XML
                return;
            }

            def chartDto = responses[it].getBody().response.data[1].value as ChartDto
            def charts = chartRepository.findByCommunityURL(it.communityUrl)
            def trackCount = 0
            charts.each { chart ->
                trackCount += getTracks(chart).size()
            }
            Assert.assertEquals(charts.size(), chartDto.playlistDtos.length)
            Assert.assertEquals(trackCount, chartDto.chartDetailDtos.length)
        }
    }

    @And('playlist content should be as in the database')
    def and2(){
        runner.parallel {
            if(it.format == RequestFormat.XML) {
                // FIXME: we have no support for XML
                return;
            }

            def chartDto = responses[it].getBody().response.data[1].value as ChartDto

            def charts = chartRepository.findByCommunityURL(it.communityUrl)
            def playListById = [:] as Map<Integer, ChartDetail>
            charts.each { chart ->
                def playList = getPlayList(chart)
                playListById[playList.chart.i] = playList
            }

            chartDto.playlistDtos.each { responsePlayList ->
                def dbPlayList = playListById[responsePlayList.id]
                Assert.assertEquals(dbPlayList.title , responsePlayList.playlistTitle)
                Assert.assertEquals(dbPlayList.info , responsePlayList.description)
                Assert.assertEquals(dbPlayList.imageFileName , responsePlayList.image)
                Assert.assertEquals(dbPlayList.imageTitle , responsePlayList.imageTitle)
                Assert.assertEquals(dbPlayList.position , responsePlayList.position)
                Assert.assertEquals(dbPlayList.subtitle , responsePlayList.subtitle)
                Assert.assertEquals(dbPlayList.chartType , responsePlayList.type)
            }
        }
    }

    @And('tracks content should be as in the database')
    def and3(){
        runner.parallel {
            if(it.format == RequestFormat.XML) {
                // FIXME: we have no support for XML
                return;
            }

            def chartDto = responses[it].getBody().response.data[1].value as ChartDto

            def charts = chartRepository.findByCommunityURL(it.communityUrl) as List<Chart>
            def trackByKey = [:] as Map<String, ChartDetail>
            charts.each { chart ->
                getTracks(chart).each { track ->
                    // composed key - "chart id-isrc_track id"
                    String composedKey = "${track.chartId}-${track.media.isrc}_${track.media.trackId}".toString()
                    // trackByKey << [ (composedKey) : (track) ]
                    trackByKey[composedKey] = track
                }
            }

            chartDto.chartDetailDtos.each { responseTrack ->
                def dbTrack = trackByKey["${responseTrack.playlistId}-${responseTrack.media}"]
                assert dbTrack
                //Assert.assertEquals(dbTrack.position , responseTrack.position)    //magic with Windows Phone - position + 40
                Assert.assertEquals(dbTrack.media.isrc + "_" + dbTrack.media.trackId , responseTrack.media)
                Assert.assertEquals(dbTrack.media.artistName , responseTrack.artist)
                Assert.assertEquals(dbTrack.media.audioSize , responseTrack.audioSize)
                Assert.assertEquals(dbTrack.media.imageLargeSize , responseTrack.imageLargeSize)
                Assert.assertEquals(dbTrack.media.imageSmallSize , responseTrack.imageSmallSize)
                Assert.assertEquals(URLEncoder.encode(dbTrack.media.iTunesUrl + "&at=10lui7&ct=MTVTrax_UK").toLowerCase() , responseTrack.iTunesUrl.toLowerCase())
                Assert.assertEquals(URLEncoder.encode(dbTrack.media.amazonUrl) , responseTrack.amazonUrl)
                Assert.assertEquals(dbTrack.prevPosition , responseTrack.previousPosition)
                Assert.assertEquals(dbTrack.chgPosition.name() , responseTrack.changePosition)
                Assert.assertEquals(dbTrack.prevPosition , responseTrack.previousPosition)
                Assert.assertEquals(dbTrack.version , responseTrack.chartDetailVersion)
                Assert.assertEquals(dbTrack.media.areArtistUrls , responseTrack.artistUrl)
                Assert.assertEquals(dbTrack.media.audioFile.duration , responseTrack.duration)
            }
        }
    }

    private List<ChartDetail> getTracks(Chart chart) {
        Long latestPublishTimeMillis = chartDetailRepository.findNearestLatestPublishDate(new Date().time, chart.i)
        chartDetailRepository.findChartDetailTreeForDrmUpdateByChartAndPublishTimeMillis(chart.i, latestPublishTimeMillis)
    }

    private ChartDetail getPlayList(Chart chart) {
        long lastPublishTimeMillis = chartDetailRepository.findNearestLatestChartPublishDate(new Date().time, chart.i)
        chartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(chart.i, lastPublishTimeMillis)
    }

}
