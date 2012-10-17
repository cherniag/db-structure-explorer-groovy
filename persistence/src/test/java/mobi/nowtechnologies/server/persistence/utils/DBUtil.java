package mobi.nowtechnologies.server.persistence.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;


/**
 * Utility class for creating local DB
 * 
 * Please don't use it for now it's not finished
 * @author dmytro
 *
 */
public class DBUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
	
	public final static String mainDbName="cn_service";
	public final static String adminDbName="cn_service_admin";
	public final static String dbUser = "root";
	public final static String dbPassword = "root";
	
	public static void main(String[] args) throws JSchException, IOException, SftpException {
		
		String tmpDir = "/home/dmytro/tmp/sql/";
		String remoteFolder = "prodddl/";
		
		scp(tmpDir, remoteFolder);
		

		JdbcTemplate mainJdbcTemplate = new JdbcTemplate(getMainDataSource());
		Resource resource = new FileSystemResource(new File("/home/dmytro/tmp/sql/"+mainDbName+".ddl"));
		executeSqlScript(mainJdbcTemplate, new EncodedResource(resource), false);
		logger.info("Database {} is up to date", mainDbName);
		
		Iterator<File> iterateFiles = FileUtils.iterateFiles(new File(tmpDir), new SqlFilter(mainDbName), null);
		for (;iterateFiles.hasNext();) {
			File sqlScript = (File) iterateFiles.next();
			executeSqlScript(mainJdbcTemplate, new EncodedResource(new FileSystemResource(sqlScript)), false);
			logger.info("Script {} processed", sqlScript);
		}
	}	

	/**
	 * @param tmpDir
	 * @param remoteFolder
	 * @throws JSchException
	 * @throws SftpException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void scp(String tmpDir, String remoteFolder) throws JSchException, SftpException, FileNotFoundException, IOException {
		JSch jsch = new JSch();
		Session session = jsch.getSession("fs", "fs", 22);
		session.setUserInfo(new RemoteUserInfo());
		Properties config = new Properties();
        	config.setProperty("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword("Cha3t5N0w");
		session.connect();
			ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
			channel.connect();
			@SuppressWarnings("unchecked")
			Vector<LsEntry> ls = channel.ls("prodddl");
			for (LsEntry object : ls) {
				String filename = object.getFilename();
				if (filename.endsWith(".sql") || filename.endsWith(".ddl")) {
					OutputStream output = new FileOutputStream(new File(tmpDir+filename));
					InputStream inputStream = channel.get(remoteFolder+filename);
					IOUtils.copy(inputStream, output);
					IOUtils.closeQuietly(inputStream);
					IOUtils.closeQuietly(output);
					logger.info(filename + " - donwloaded");
				}
			}
		channel.disconnect();
	    session.disconnect();
	}
	
	public static DataSource getMainDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
	    	dataSource.setUrl("jdbc:mysql://localhost:3306/"+mainDbName+"?useUnicode=yes&amp;characterEncoding=UTF-8");
	    	dataSource.setUsername(dbUser);
	    	dataSource.setPassword(dbPassword);
	    	dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    	return dataSource;
	}
	
	public static DataSource getAdminDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
	    	dataSource.setUrl("jdbc:mysql://localhost:3306/"+adminDbName+"?useUnicode=yes&amp;characterEncoding=UTF-8");
	    	dataSource.setUsername(dbUser);
	    	dataSource.setPassword(dbPassword);
	    	dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    	return dataSource;
	}
	
	protected static class RemoteUserInfo implements UserInfo {
		@Override
		public String getPassphrase() {
			return null;
		}
		@Override
		public String getPassword() {
			return "Cha3t5N0w";
		}
		@Override
		public boolean promptPassphrase(String arg0) {
			return false;
		}
		@Override
		public boolean promptPassword(String arg0) {
			return false;
		}
		@Override
		public boolean promptYesNo(String arg0) {
			return false;
		}
		@Override
		public void showMessage(String arg0) {
		}
	}
	
	/**
	 * Read a script from the LineNumberReader and build a String containing the
	 * lines.
	 * 
	 * @param lineNumberReader the <code>LineNumberReader</code> containing the
	 * script to be processed
	 * @return <code>String</code> containing the script lines
	 * @throws IOException
	 */
	public static String readScript(LineNumberReader lineNumberReader) throws IOException {
		String currentStatement = lineNumberReader.readLine();
		StringBuilder scriptBuilder = new StringBuilder();
		while (currentStatement != null) {
			if (StringUtils.hasText(currentStatement) && !currentStatement.startsWith("--")) {
				if (scriptBuilder.length() > 0) {
					scriptBuilder.append('\n');
				}
				
				scriptBuilder.append(currentStatement);
			}
			currentStatement = lineNumberReader.readLine();
		}
		return scriptBuilder.toString();
	}
	
	/**
	 * Does the provided SQL script contain the specified delimiter?
	 * 
	 * @param script the SQL script
	 * @param delim character delimiting each statement - typically a ';'
	 * character
	 */
	public static boolean containsSqlScriptDelimiters(String script, char delim) {
		boolean inLiteral = false;
		char[] content = script.toCharArray();
		for (int i = 0; i < script.length(); i++) {
			if (content[i] == '\'') {
				inLiteral = !inLiteral;
			}
			if (content[i] == delim && !inLiteral) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Split an SQL script into separate statements delimited with the provided
	 * delimiter character. Each individual statement will be added to the
	 * provided <code>List</code>.
	 * 
	 * @param script the SQL script
	 * @param delim character delimiting each statement - typically a ';'
	 * character
	 * @param statements the List that will contain the individual statements
	 */
	public static void splitSqlScript(String script, char delim, List<String> statements) {
		StringBuilder sb = new StringBuilder();
		boolean inLiteral = false;
		char[] content = script.toCharArray();
		String prevChar = "";
		for (int i = 0; i < script.length(); i++) {
			if (content[i] == '\'' && !prevChar.equals("\\")) {
				inLiteral = !inLiteral;
			}
			if (content[i] == delim && !inLiteral) {
				if (sb.length() > 0) {
					statements.add(sb.toString());
					sb = new StringBuilder();
				}
			}
			else {
				sb.append(content[i]);
			}
			prevChar=String.valueOf(content[i]);
		}
		if (sb.length() > 0) {
			statements.add(sb.toString());
		}
	}
	
	/**
	 * Execute the given SQL script.
	 * <p>The script will normally be loaded by classpath. There should be one statement
	 * per line. Any semicolons will be removed. <b>Do not use this method to execute
	 * DDL if you expect rollback.</b>
	 * @param jdbcTemplate the JdbcTemplate with which to perform JDBC operations
	 * @param resource the resource (potentially associated with a specific encoding)
	 * to load the SQL script from.
	 * @param continueOnError whether or not to continue without throwing an
	 * exception in the event of an error.
	 * @throws DataAccessException if there is an error executing a statement
	 * and continueOnError was <code>false</code>
	 */
	public static void executeSqlScript(JdbcTemplate jdbcTemplate, EncodedResource resource,
			boolean continueOnError) throws DataAccessException {

		if (logger.isInfoEnabled()) {
			logger.info("Executing SQL script from " + resource);
		}

		long startTime = System.currentTimeMillis();
		List<String> statements = new LinkedList<String>();
		LineNumberReader reader = null;
		try {
			reader = new LineNumberReader(resource.getReader());
			String script = readScript(reader);
			char delimiter = ';';
			if (!containsSqlScriptDelimiters(script, delimiter)) {
				delimiter = '\n';
			}
			splitSqlScript(script, delimiter, statements);
			for (String statement : statements) {
				try {
					int rowsAffected = jdbcTemplate.update(statement);
					if (logger.isDebugEnabled()) {
						logger.debug(rowsAffected + " rows affected by SQL: " + statement);
					}
				}
				catch (DataAccessException ex) {
					if (continueOnError) {
						if (logger.isWarnEnabled()) {
							logger.warn("SQL: " + statement + " failed", ex);
						}
					}
					else {
						throw ex;
					}
				}
			}
			long elapsedTime = System.currentTimeMillis() - startTime;
			if (logger.isInfoEnabled()) {
				logger.info("Done executing SQL scriptBuilder from " + resource + " in " + elapsedTime + " ms.");
			}
		}
		catch (IOException ex) {
			throw new DataAccessResourceFailureException("Failed to open SQL script from " + resource, ex);
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	
	public static class SqlFilter implements IOFileFilter {
		
		private String dbName;

		public SqlFilter(String dbName) {
			this.dbName = dbName;
		}

		@Override
		public boolean accept(File file) {
			return file.isFile() && file.getName().startsWith(dbName) && file.getName().endsWith(".sql");
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(dbName) && name.endsWith(".sql");
		}
	}
}