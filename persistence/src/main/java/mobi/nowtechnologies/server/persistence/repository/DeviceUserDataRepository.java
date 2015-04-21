package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeviceUserDataRepository extends JpaRepository<DeviceUserData, Integer> {

    @Query("select data from DeviceUserData data where data.xtifyToken = ?")
    DeviceUserData findByXtifyToken(String token);

    @Query("select data from DeviceUserData data where data.userId=:userId and data.communityUrl=:communityUrl and data.deviceUid=:deviceUID")
    DeviceUserData find(@Param("userId") int userId, @Param("communityUrl") String communityUrl, @Param("deviceUID") String deviceUID);

    @Modifying
    @Query("delete from DeviceUserData data where data.xtifyToken = :token")
    int deleteByXtifyToken(@Param("token") String token);

    @Modifying
    @Query("delete from DeviceUserData data where data.userId=:userId and data.communityUrl=:communityUrl and data.deviceUid=:deviceUID")
    int deleteByUser(@Param("userId") int userId, @Param("communityUrl") String communityUrl, @Param("deviceUID") String deviceUID);

}
