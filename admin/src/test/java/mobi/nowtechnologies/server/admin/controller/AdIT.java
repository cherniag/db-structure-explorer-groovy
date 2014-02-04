package mobi.nowtechnologies.server.admin.controller;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import mobi.nowtechnologies.server.service.impl.CloudFileServiceImpl;
import mobi.nowtechnologies.server.shared.enums.AdActionType;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Titov Mykhaylo (titov)
 */
public class AdIT extends AbstractAdminITTest {


    @Autowired
    private CloudFileServiceImpl cloudFileService;

    @Test
    public void testGetAdsPage_Success() throws Exception {
        String requestURI = "/ads";
        String communityUrl = "nowtop40";
        mockMvc.perform(get(requestURI).headers(getHttpHeaders(true)).cookie(getCommunityCoockie(communityUrl))).andExpect(status().isOk()).
                andDo(print()).
                andExpect(jsonPath("$.allAdFilterDtos[0].name").value("J2ME")).
                andExpect(jsonPath("$.allAdFilterDtos[1].name").value("ONE_MONTH_PROMO")).
                andExpect(jsonPath("$.allAdFilterDtos[2].name").value("PAYMENT_ERROR")).
                andExpect(jsonPath("$.allAdFilterDtos[1].name").value("ONE_MONTH_PROMO")).
        andExpect(content().string("{\"AD_ITEM_DTO_LIST\":[{\"id\":81,\"actionType\":\"URL\",\"action\":\"http://www.ukr.net\",\"message\":\"Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!\",\"activated\":true,\"filterDtos\":[{\"name\":\"LAST_TRIAL_DAY\"}],\"file\":null,\"imageFileName\":null,\"position\":1,\"removeImage\":true},{\"id\":82,\"actionType\":\"URL\",\"action\":\"http://google.com.ua\",\"message\":\"The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.\",\"activated\":true,\"filterDtos\":[{\"name\":\"ANDROID\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"}],\"file\":null,\"imageFileName\":null,\"position\":2,\"removeImage\":true},{\"id\":83,\"actionType\":\"ISRC\",\"action\":\"1XLS70CD\",\"message\":\"The NOW! Top 40 Chart delivers all the hits, all the time, straight to your mobile! Subscribed already? Congrats! If not, please get your groove on to keep the hits coming! Click in app or via your email! No email? Check your junk folder! Go for it!\",\"activated\":true,\"filterDtos\":[{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"}],\"file\":null,\"imageFileName\":null,\"position\":3,\"removeImage\":true},{\"id\":84,\"actionType\":\"ISRC\",\"action\":\"2XLS70CD\",\"message\":\"Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.\",\"activated\":true,\"filterDtos\":[{\"name\":\"PAYMENT_ERROR\"}],\"file\":null,\"imageFileName\":null,\"position\":4,\"removeImage\":true},{\"id\":85,\"actionType\":\"ISRC\",\"action\":\"3XLS70CD\",\"message\":\"Grammy producers are keen for Adele to open the legendary awards ceremony in February but will have to wait for an answer as the singer is still recovering from throat surgery. The Someone Like You singer has been nominated for six awards.\",\"activated\":true,\"filterDtos\":[{\"name\":\"IOS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"file\":null,\"imageFileName\":null,\"position\":5,\"removeImage\":true},{\"id\":86,\"actionType\":\"URL\",\"action\":\"http://www.i.ua/\",\"message\":\"MasterCard and the Brits have launched an amazing competition in which fans can duet with either JLS, Emeli Sande or Labrinth. The winner will get VIP tickets to the awards show itself AND appear in an advert with their favourite popstar!\",\"activated\":false,\"filterDtos\":[],\"file\":null,\"imageFileName\":null,\"position\":6,\"removeImage\":true},{\"id\":87,\"actionType\":\"ISRC\",\"action\":\"4XLS70CD\",\"message\":\"Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!\",\"activated\":true,\"filterDtos\":[{\"name\":\"LIMITED\"}],\"file\":null,\"imageFileName\":null,\"position\":7,\"removeImage\":true},{\"id\":88,\"actionType\":\"ISRC\",\"action\":\"5XLS70CD\",\"message\":\"Lady Gaga has apparently taken up darts! The Born This Way singer recently accompanied her Vampire Diaries actor boyfriend, Taylor Kinney, to a pub game and apparently she is hooked! Taylor partnered Gaga as her love interest in her video You and I.\",\"activated\":false,\"filterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"}],\"file\":null,\"imageFileName\":null,\"position\":8,\"removeImage\":true},{\"id\":89,\"actionType\":\"URL\",\"action\":\"https://musicqubed.com/\",\"message\":\"With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?\",\"activated\":true,\"filterDtos\":[{\"name\":\"J2ME\"}],\"file\":null,\"imageFileName\":null,\"position\":9,\"removeImage\":true},{\"id\":90,\"actionType\":\"ISRC\",\"action\":\"6XLS70CD\",\"message\":\"The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk\",\"activated\":true,\"filterDtos\":[{\"name\":\"ANDROID\"}],\"file\":null,\"imageFileName\":null,\"position\":10,\"removeImage\":true}],\"allAdFilterDtos\":[{\"name\":\"J2ME\"},{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"LIMITED_AFTER_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"}],\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}"));
    }


    @Test
    public void testGetAddAdPage_Success() throws Exception {
        String requestURI = "/ads/";
        String communityUrl = "nowtop40";
        mockMvc.perform(get(requestURI).headers(getHttpHeaders(true)).cookie(getCommunityCoockie(communityUrl))).andExpect(status().isOk()).
                andDo(print()).
                andExpect(jsonPath("$.allAdFilterDtos[0].name").value("J2ME")).
                andExpect(jsonPath("$.allAdFilterDtos[1].name").value("ONE_MONTH_PROMO")).
                andExpect(jsonPath("$.allAdFilterDtos[2].name").value("PAYMENT_ERROR")).
                andExpect(jsonPath("$.allAdFilterDtos[1].name").value("ONE_MONTH_PROMO"))
                .andExpect(content().string("{\"allAdFilterDtos\":[{\"name\":\"J2ME\"},{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"LIMITED_AFTER_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"}],\"AD_ITEM_DTO\":{\"id\":null,\"actionType\":null,\"action\":null,\"message\":null,\"activated\":false,\"filterDtos\":null,\"file\":null,\"imageFileName\":null,\"position\":null,\"removeImage\":false},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}"));

    }


    @SuppressWarnings("unchecked")
    @Test
    public void testSaveAd_Success() throws Exception {
        String requestURI = "/ads/";
        String communityUrl = "nowtop40";

        FilesClient filesClient = Mockito.mock(FilesClient.class);
        cloudFileService.setFilesClient(filesClient);

        when(filesClient.login()).thenReturn(Boolean.TRUE);
        when(filesClient.storeStreamedObject(anyString(), any(InputStream.class), Mockito.matches("application/octet-stream"), anyString(), Mockito.anyMap())).thenReturn("");

        mockMvc.perform(fileUpload(requestURI).file(new MockMultipartFile("file", "1".getBytes())).
                cookie(getCommunityCoockie(communityUrl)).headers(getHttpHeaders(true)).
                param("actionType", AdActionType.URL.name()).
                param("message", "some message").
                param("activated", "true").
                param("filterDtos", "ONE_MONTH_PROMO", "LIMITED").
                param("action", "https://i.ua")).andExpect(status().isMovedTemporarily()).
                andExpect(redirectedUrl("/ads?filesURL=http%3A%2F%2Fc1129449.r49.cf3.rackcdn.com%2F"));

        verify(filesClient, times(1)).login();
        verify(filesClient, times(1)).storeStreamedObject(anyString(), any(InputStream.class), Mockito.matches("application/octet-stream"), anyString(), Mockito.anyMap());
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateAd_Success() throws Exception {
        Integer id = 81;
        String requestURI = "/ads/" + id;
        String communityUrl = "nowtop40";
        String imageFileName = "someImageFileName";


        FilesClient filesClient = Mockito.mock(FilesClient.class);
        cloudFileService.setFilesClient(filesClient);

        when(filesClient.login()).thenReturn(Boolean.TRUE);
        when(filesClient.storeStreamedObject(Mockito.eq("test-storage"), any(ByteArrayInputStream.class), Mockito.eq("application/octet-stream"), Mockito.endsWith(id.toString()), Mockito.eq(Collections.EMPTY_MAP))).thenReturn("");

        mockMvc.perform(fileUpload(requestURI).file(new MockMultipartFile("file", "1".getBytes())).
                cookie(getCommunityCoockie(communityUrl)).headers(getHttpHeaders(true)).
                param("id", id.toString()).
                param("actionType", AdActionType.URL.name()).
                param("message", "some message").
                param("activated", "true").
                param("filterDtos", "ONE_MONTH_PROMO", "LIMITED").
                param("imageFileName", imageFileName).
                param("action", "https://i.ua")).andExpect(status().isMovedTemporarily()).
                andExpect(redirectedUrl("/ads?filesURL=http%3A%2F%2Fc1129449.r49.cf3.rackcdn.com%2F")
                );


        verify(filesClient, times(1)).login();
        verify(filesClient, times(1)).storeStreamedObject(Mockito.eq("test-storage"), any(ByteArrayInputStream.class), Mockito.eq("application/octet-stream"), Mockito.endsWith(id.toString()), Mockito.eq(Collections.EMPTY_MAP));
    }


    @Test
    public void testGetUpdateAdPage_Success() throws Exception {
        Integer id = 81;
        String requestURI = "/ads/" + id;
        String communityUrl = "nowtop40";

        mockMvc.perform(get(requestURI).cookie(getCommunityCoockie(communityUrl)).headers(getHttpHeaders(true))).
                andExpect(status().isOk()).
                andDo(print()).
                andExpect(
                        content().string("{\"allAdFilterDtos\":[{\"name\":\"J2ME\"},{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"LIMITED_AFTER_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"}],\"AD_ITEM_DTO\":{\"id\":81,\"actionType\":\"URL\",\"action\":\"http://www.ukr.net\",\"message\":\"Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!\",\"activated\":true,\"filterDtos\":[{\"name\":\"LAST_TRIAL_DAY\"}],\"file\":null,\"imageFileName\":null,\"position\":1,\"removeImage\":true},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}"));

    }


    @Test
    public void testDelete_Success() throws Exception {
        String requestURI = "/ads/81";
        String communityUrl = "nowtop40";
        mockMvc.perform(delete(requestURI).headers(getHttpHeaders(true)).cookie(getCommunityCoockie(communityUrl)))
                .andExpect(status().isMovedTemporarily());
    }

}
