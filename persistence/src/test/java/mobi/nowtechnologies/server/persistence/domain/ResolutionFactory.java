package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import static org.mockito.Mockito.*;

public class ResolutionFactory {

    public static Resolution create(String deviceType, int width, int height) {
        Resolution r = mock(Resolution.class);
        when(r.getDeviceType()).thenReturn(deviceType);
        when(r.getWidth()).thenReturn(width);
        when(r.getHeight()).thenReturn(height);
        when(r.getId()).thenReturn(1L);
        return r;
    }
}
