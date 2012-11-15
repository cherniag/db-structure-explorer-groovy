package mobi.nowtechnologies.server.trackrepo.security.service.impl;

import mobi.nowtechnologies.server.trackrepo.security.service.TrackRepoUserService;
import mobi.nowtechnologies.server.trackrepo.security.userdetails.TrackRepoUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class TrackRepoUserServiceImpl extends JdbcDaoImpl implements TrackRepoUserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoUserServiceImpl.class);

	private final String loadUserByUsernameQuery = "select userName, password, enabled from users where userName=?";
	private final String loadAuthoritiesByUsernameQuery = "select authority from authorities where userName=?";

	private final PreparedStatementCreator loadUserByUsernamePreparedStatementCreator;
	private final ResultSetExtractor<List<TrackRepoUserDetails>> loadUserByUsernameResultSetExtractor;

	private final PreparedStatementCreator loadAuthoritiesByUsernamePreparedStatementCreator;
	private final ResultSetExtractor<List<GrantedAuthority>> loadAuthoritiesByUsernameResultSetExtractor;

	public TrackRepoUserServiceImpl() {
		loadUserByUsernamePreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(loadUserByUsernameQuery);

				return preparedStatement;
			}
		};

		loadUserByUsernameResultSetExtractor = new ResultSetExtractor<List<TrackRepoUserDetails>>() {

			@Override
			public List<TrackRepoUserDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {

				List<TrackRepoUserDetails> adminUserDetailsList = new ArrayList<TrackRepoUserDetails>();

				while (rs.next()) {
					String userName = rs.getString(1);
					String password = rs.getString(2);
					boolean enabled = rs.getBoolean(3);

					TrackRepoUserDetails trackRepoUserDetails = new TrackRepoUserDetails();
					trackRepoUserDetails.setUserName(userName);
					trackRepoUserDetails.setPassword(password);
					trackRepoUserDetails.setEnabled(enabled);

					adminUserDetailsList.add(trackRepoUserDetails);
				}

				return adminUserDetailsList;
			}

		};

		loadAuthoritiesByUsernamePreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(loadAuthoritiesByUsernameQuery);

				return preparedStatement;
			}
		};

		loadAuthoritiesByUsernameResultSetExtractor = new ResultSetExtractor<List<GrantedAuthority>>() {

			@Override
			public List<GrantedAuthority> extractData(ResultSet rs) throws SQLException, DataAccessException {

				List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();

				while (rs.next()) {
					String authority = rs.getString(1);

					GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);

					grantedAuthorityList.add(grantedAuthority);
				}

				return grantedAuthorityList;
			}

		};

	}

	@Override
	public UserDetails loadUserByUsername(final String userName) throws UsernameNotFoundException {

		LOGGER.debug("input parameters userName: [{}]", userName);

		PreparedStatementSetter loadUserByUsernamePreparedStatementSetter = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				StatementCreatorUtils.setParameterValue(ps, 1, SqlTypeValue.TYPE_UNKNOWN, userName);
			}
		};

		List<TrackRepoUserDetails> trackRepoUserDetailsList = getJdbcTemplate().query(loadUserByUsernamePreparedStatementCreator,
				loadUserByUsernamePreparedStatementSetter, loadUserByUsernameResultSetExtractor);

		final TrackRepoUserDetails trackRepoUserDetails;

		if (trackRepoUserDetailsList.isEmpty())
			throw new UsernameNotFoundException("Couldn't find user with userName [{" + userName + "}]");
		else
			trackRepoUserDetails = trackRepoUserDetailsList.get(0);

		PreparedStatementSetter loadAuthoritiesByUsernamePreparedStatementSetter = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				StatementCreatorUtils.setParameterValue(ps, 1, SqlTypeValue.TYPE_UNKNOWN, userName);
			}
		};

		List<GrantedAuthority> grantedAuthorityList = getJdbcTemplate().query(loadAuthoritiesByUsernamePreparedStatementCreator,
				loadAuthoritiesByUsernamePreparedStatementSetter, loadAuthoritiesByUsernameResultSetExtractor);

		trackRepoUserDetails.setGrantedAuthorities(grantedAuthorityList);

		LOGGER.debug("Output parameter [{}]", trackRepoUserDetails);
		return trackRepoUserDetails;
	}

}
