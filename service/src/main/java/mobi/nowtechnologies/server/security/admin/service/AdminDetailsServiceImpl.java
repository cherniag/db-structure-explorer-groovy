package mobi.nowtechnologies.server.security.admin.service;

import mobi.nowtechnologies.server.security.admin.userdetail.AdminUserDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 */
public class AdminDetailsServiceImpl extends JdbcDaoImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDetailsServiceImpl.class);

    private final String loadUserByUsernameQuery = "select userName, password, enabled from users where userName=?";
    private final String loadAuthoritiesByUsernameQuery = "select authority from authorities where userName=?";

    private final PreparedStatementCreator loadUserByUsernamePreparedStatementCreator;
    private final ResultSetExtractor<List<AdminUserDetails>> loadUserByUsernameResultSetExtractor;

    private final PreparedStatementCreator loadAuthoritiesByUsernamePreparedStatementCreator;
    private final ResultSetExtractor<List<GrantedAuthority>> loadAuthoritiesByUsernameResultSetExtractor;


    public AdminDetailsServiceImpl() {
        loadUserByUsernamePreparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preparedStatement = con.prepareStatement(loadUserByUsernameQuery);

                return preparedStatement;
            }
        };

        loadUserByUsernameResultSetExtractor = new ResultSetExtractor<List<AdminUserDetails>>() {

            @Override
            public List<AdminUserDetails> extractData(ResultSet rs) throws SQLException, DataAccessException {

                List<AdminUserDetails> adminUserDetailsList = new ArrayList<AdminUserDetails>();

                while (rs.next()) {
                    String userName = rs.getString(1);
                    String password = rs.getString(2);
                    boolean enabled = rs.getBoolean(3);

                    AdminUserDetails adminUserDetails = new AdminUserDetails();
                    adminUserDetails.setUserName(userName);
                    adminUserDetails.setPassword(password);
                    adminUserDetails.setEnabled(enabled);

                    adminUserDetailsList.add(adminUserDetails);
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

        List<AdminUserDetails> adminUserDetailsList =
            getJdbcTemplate().query(loadUserByUsernamePreparedStatementCreator, loadUserByUsernamePreparedStatementSetter, loadUserByUsernameResultSetExtractor);

        final AdminUserDetails adminUserDetails;

        if (adminUserDetailsList.isEmpty()) {
            throw new UsernameNotFoundException("Couldn't find user with userName [{" + userName + "}]");
        } else {
            adminUserDetails = adminUserDetailsList.get(0);
        }

        PreparedStatementSetter loadAuthoritiesByUsernamePreparedStatementSetter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                StatementCreatorUtils.setParameterValue(ps, 1, SqlTypeValue.TYPE_UNKNOWN, userName);
            }
        };

        List<GrantedAuthority> grantedAuthorityList =
            getJdbcTemplate().query(loadAuthoritiesByUsernamePreparedStatementCreator, loadAuthoritiesByUsernamePreparedStatementSetter, loadAuthoritiesByUsernameResultSetExtractor);

        adminUserDetails.setGrantedAuthorities(grantedAuthorityList);

        LOGGER.debug("Output parameter [{}]", adminUserDetails);
        return adminUserDetails;
    }

}
