package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

public class ResolutionRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private ResolutionRepository resolutionRepository;

    @Test
    public void testFindAllSorted() throws Exception {
        resolutionRepository.save(new Resolution(UserRegInfo.DeviceType.ANDROID, 40, 40));
        resolutionRepository.save(new Resolution(UserRegInfo.DeviceType.ANDROID, 50, 50));
        resolutionRepository.save(new Resolution(UserRegInfo.DeviceType.ANDROID, 70, 70));
        resolutionRepository.save(new Resolution(UserRegInfo.DeviceType.IOS, 10, 10));

        List<Resolution> sorted = resolutionRepository.findAllSorted();

        assertEquals(UserRegInfo.DeviceType.ANDROID, sorted.get(0).getDeviceType());
        assertEquals(40, sorted.get(0).getWidth());

        assertEquals(UserRegInfo.DeviceType.ANDROID, sorted.get(1).getDeviceType());
        assertEquals(50, sorted.get(1).getWidth());

        assertEquals(UserRegInfo.DeviceType.ANDROID, sorted.get(2).getDeviceType());
        assertEquals(70, sorted.get(2).getWidth());

        assertEquals(UserRegInfo.DeviceType.IOS, sorted.get(3).getDeviceType());
    }

    @Test
    public void testFind() throws Exception {
        resolutionRepository.saveAndFlush(new Resolution(UserRegInfo.DeviceType.ANDROID, 40, 40));
        Resolution r = resolutionRepository.saveAndFlush(new Resolution(UserRegInfo.DeviceType.ANDROID, 50, 50));
        resolutionRepository.saveAndFlush(new Resolution(UserRegInfo.DeviceType.ANDROID, 70, 70));
        resolutionRepository.saveAndFlush(new Resolution(UserRegInfo.DeviceType.IOS, 10, 10));

        Resolution found = resolutionRepository.find(UserRegInfo.DeviceType.ANDROID, 50, 50);

        assertEquals(r, found);
    }
}