package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.AppsFlyerData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.AppsFlyerDataRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppsFlyerDataServiceTest {

    @Mock
    private AppsFlyerDataRepository appsFlyerDataRepository;

    @InjectMocks
    private AppsFlyerDataService appsFlyerDataService;

    private ArgumentCaptor<AppsFlyerData> appsFlyerDataArgumentCaptor = ArgumentCaptor.forClass(AppsFlyerData.class);

    @Test
    public void saveAppsFlyerData() throws Exception {
        when(appsFlyerDataRepository.save(appsFlyerDataArgumentCaptor.capture())).thenReturn(null);
        User user = mock(User.class);
        when(user.getId()).thenReturn(100);

        appsFlyerDataService.saveAppsFlyerData(user, "SOME-ID");

        AppsFlyerData saved = appsFlyerDataArgumentCaptor.getValue();
        assertEquals("SOME-ID", saved.getAppsFlyerUid());
        verify(appsFlyerDataRepository, times(1)).save(saved);
    }

    @Test
    public void mergeExistingFromDataIntoExistingToData() throws Exception {
        User fromUser = mock(User.class);
        User toUser = mock(User.class);
        when(fromUser.getId()).thenReturn(100);
        when(toUser.getId()).thenReturn(200);
        AppsFlyerData fromData = mock(AppsFlyerData.class);
        when(fromData.getAppsFlyerUid()).thenReturn("FROM-ID");
        AppsFlyerData toData = mock(AppsFlyerData.class);
        when(toData.getAppsFlyerUid()).thenReturn("TO-ID");
        when(appsFlyerDataRepository.findDataByUserId(fromUser.getId())).thenReturn(fromData);
        when(appsFlyerDataRepository.findDataByUserId(toUser.getId())).thenReturn(toData);
        when(appsFlyerDataRepository.save(appsFlyerDataArgumentCaptor.capture())).thenReturn(null);

        appsFlyerDataService.mergeAppsFlyerData(fromUser, toUser);

        verify(appsFlyerDataRepository).delete(fromData);
        verify(appsFlyerDataRepository, never()).delete(toData);
        verify(toData).setAppsFlyerUid("FROM-ID");
        verify(toData, never()).setUserId(anyInt());
    }

    @Test
    public void mergeExistingFromDataIntoNotExistingToData() throws Exception {
        User fromUser = mock(User.class);
        User toUser = mock(User.class);
        when(fromUser.getId()).thenReturn(100);
        when(toUser.getId()).thenReturn(200);
        AppsFlyerData fromData = mock(AppsFlyerData.class);
        when(fromData.getAppsFlyerUid()).thenReturn("FROM-ID");
        when(fromData.getUserId()).thenReturn(100);
        when(appsFlyerDataRepository.findDataByUserId(fromUser.getId())).thenReturn(fromData);
        when(appsFlyerDataRepository.findDataByUserId(toUser.getId())).thenReturn(null);
        when(appsFlyerDataRepository.save(appsFlyerDataArgumentCaptor.capture())).thenReturn(null);

        appsFlyerDataService.mergeAppsFlyerData(fromUser, toUser);

        AppsFlyerData saved = appsFlyerDataArgumentCaptor.getValue();
        verify(appsFlyerDataRepository).delete(fromData);
        assertEquals(toUser.getId(), saved.getUserId());
        assertEquals("FROM-ID", saved.getAppsFlyerUid());
        verify(appsFlyerDataRepository, times(1)).save(saved);
    }

    @Test
    public void mergeNotExistingFromDataIntoExistingToData() throws Exception {
        User fromUser = mock(User.class);
        User toUser = mock(User.class);
        when(fromUser.getId()).thenReturn(100);
        when(toUser.getId()).thenReturn(200);
        AppsFlyerData toData = mock(AppsFlyerData.class);
        when(toData.getAppsFlyerUid()).thenReturn("TO-ID");
        when(toData.getUserId()).thenReturn(200);
        when(appsFlyerDataRepository.findDataByUserId(fromUser.getId())).thenReturn(null);
        when(appsFlyerDataRepository.findDataByUserId(toUser.getId())).thenReturn(toData);

        appsFlyerDataService.mergeAppsFlyerData(fromUser, toUser);

        verify(appsFlyerDataRepository, never()).delete(any(AppsFlyerData.class));
        verify(appsFlyerDataRepository, never()).save(any(AppsFlyerData.class));
    }

    @Test
    public void mergeNotExistingFromDataIntoNotExistingToData() throws Exception {
        User fromUser = mock(User.class);
        User toUser = mock(User.class);
        when(fromUser.getId()).thenReturn(100);
        when(toUser.getId()).thenReturn(200);
        when(appsFlyerDataRepository.findDataByUserId(fromUser.getId())).thenReturn(null);
        when(appsFlyerDataRepository.findDataByUserId(toUser.getId())).thenReturn(null);

        appsFlyerDataService.mergeAppsFlyerData(fromUser, toUser);

        verify(appsFlyerDataRepository, never()).delete(any(AppsFlyerData.class));
        verify(appsFlyerDataRepository, never()).save(any(AppsFlyerData.class));
    }
}