package mobi.nowtechnologies.server.persistence.utils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

/**
 * User: Alexsandr_Kolpakov Date: 12/20/13 Time: 12:32 PM
 */
public class SQLUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public void importScript(String... importFiles) throws Exception {
        for (String importFile : importFiles) {
            LOGGER.info("Executing import script: " + importFile);
            InputStreamReader streamReader = new InputStreamReader(new FileInputStream(ResourceUtils.getFile(importFile)));
            BufferedReader reader = new BufferedReader(streamReader);
            long lineNo = 0;
            StringBuilder sqlQuery = new StringBuilder();
            for (String sql = reader.readLine(); sql != null; sql = reader.readLine()) {
                try {
                    lineNo++;
                    String trimmedLine = sql.trim();
                    if (trimmedLine.length() == 0 ||
                        trimmedLine.startsWith("--") ||
                        trimmedLine.startsWith("//") ||
                        trimmedLine.startsWith("/*")) {
                        continue;
                    } else {
                        if (trimmedLine.endsWith(";")) {
                            trimmedLine = trimmedLine.substring(0, trimmedLine.length() - 1);
                            sqlQuery.append(trimmedLine);

                            LOGGER.debug(sqlQuery.toString());

                            Query query = entityManager.createNativeQuery(sqlQuery.toString());
                            query.executeUpdate();

                            sqlQuery = new StringBuilder();
                        } else {
                            sqlQuery.append(trimmedLine).append(" ");
                        }
                    }
                } catch (Exception e) {
                    throw new HibernateException("Error during import script execution at line " + lineNo, e);
                }
            }
        }
    }
}
