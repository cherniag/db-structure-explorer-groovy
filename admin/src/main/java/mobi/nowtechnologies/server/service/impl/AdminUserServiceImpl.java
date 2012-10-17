package mobi.nowtechnologies.server.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import mobi.nowtechnologies.server.dto.CommunityDto;
import mobi.nowtechnologies.server.service.AdminUserService;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AdminUserServiceImpl extends JdbcDaoSupport implements AdminUserService {

	@Override
	public List<CommunityDto> getCommunitiesbyUser(String username) {
		return getJdbcTemplate().query("select communityURL from users where username=?", new Object[]{username}, new ParameterizedRowMapper<CommunityDto>() {
			@Override
			public CommunityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new CommunityDto(rs.getString("communityURL"));
			}
		});
	}

}