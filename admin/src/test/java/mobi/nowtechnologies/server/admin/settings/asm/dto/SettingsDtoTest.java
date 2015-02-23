package mobi.nowtechnologies.server.admin.settings.asm.dto;

import mobi.nowtechnologies.server.admin.settings.asm.dto.playlisttype.PlaylistTypeInfoDto;
import mobi.nowtechnologies.server.dto.context.ContentBehaviorType;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.BehaviorConfigType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class SettingsDtoTest {

    @Test
    public void createDtoForDefaultModel() throws Exception {
        SettingsDto dto = new SettingsDto(BehaviorConfigType.DEFAULT);
        assertEquals(BehaviorConfigType.DEFAULT, dto.getBehaviorConfigType());

        Map<ChartBehaviorType, PlaylistTypeInfoDto> playlistTypeSettings = dto.getPlaylistTypeSettings();
        assertEquals(2, playlistTypeSettings.size());
        assertNotNull(playlistTypeSettings.get(ChartBehaviorType.NORMAL));
        assertNotNull(playlistTypeSettings.get(ChartBehaviorType.PREVIEW));

        Map<UserStatusType, ContentBehaviorType> favourites = dto.getFavourites();
        assertEquals(3, favourites.size());
        assertNotNull(favourites.get(UserStatusType.FREE_TRIAL));
        assertNotNull(favourites.get(UserStatusType.SUBSCRIBED));
        assertNotNull(favourites.get(UserStatusType.LIMITED));

        Map<UserStatusType, ContentBehaviorType> ads = dto.getAds();
        assertEquals(3, ads.size());
        assertNotNull(ads.get(UserStatusType.FREE_TRIAL));
        assertNotNull(ads.get(UserStatusType.SUBSCRIBED));
        assertNotNull(ads.get(UserStatusType.LIMITED));

        Set<ChartBehaviorType> chartBehaviorTypes = dto.getChartBehaviorTypes();
        assertEquals(2, chartBehaviorTypes.size());
        assertTrue(chartBehaviorTypes.contains(ChartBehaviorType.NORMAL));
        assertTrue(chartBehaviorTypes.contains(ChartBehaviorType.PREVIEW));
    }

    @Test
    public void createDtoForFreemiumModel() throws Exception {
        SettingsDto dto = new SettingsDto(BehaviorConfigType.FREEMIUM);
        assertEquals(BehaviorConfigType.FREEMIUM, dto.getBehaviorConfigType());

        Map<ChartBehaviorType, PlaylistTypeInfoDto> playlistTypeSettings = dto.getPlaylistTypeSettings();
        assertEquals(3, playlistTypeSettings.size());
        assertNotNull(playlistTypeSettings.get(ChartBehaviorType.NORMAL));
        assertNotNull(playlistTypeSettings.get(ChartBehaviorType.PREVIEW));
        assertNotNull(playlistTypeSettings.get(ChartBehaviorType.SHUFFLED));

        Map<UserStatusType, ContentBehaviorType> favourites = dto.getFavourites();
        assertEquals(3, favourites.size());
        assertNotNull(favourites.get(UserStatusType.FREE_TRIAL));
        assertNotNull(favourites.get(UserStatusType.SUBSCRIBED));
        assertNotNull(favourites.get(UserStatusType.LIMITED));

        Map<UserStatusType, ContentBehaviorType> ads = dto.getAds();
        assertEquals(3, ads.size());
        assertNotNull(ads.get(UserStatusType.FREE_TRIAL));
        assertNotNull(ads.get(UserStatusType.SUBSCRIBED));
        assertNotNull(ads.get(UserStatusType.LIMITED));

        Set<ChartBehaviorType> chartBehaviorTypes = dto.getChartBehaviorTypes();
        assertEquals(3, chartBehaviorTypes.size());
        assertTrue(chartBehaviorTypes.contains(ChartBehaviorType.NORMAL));
        assertTrue(chartBehaviorTypes.contains(ChartBehaviorType.PREVIEW));
        assertTrue(chartBehaviorTypes.contains(ChartBehaviorType.SHUFFLED));
    }
}