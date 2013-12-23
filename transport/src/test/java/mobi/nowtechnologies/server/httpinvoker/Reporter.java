package mobi.nowtechnologies.server.httpinvoker;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reporter {
    private File dir;

    public void setDir(Resource dir) throws IOException {
        this.dir = new File(dir.getFile(), new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()));
        this.dir.mkdirs();
    }

    public void report(String key, ResponseEntity<String> response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        writeTotalReport(key, statusCode);

        File report = new File(dir, key + ".xml");
        Files.write(response.getBody(), report, Charsets.UTF_8);
    }

    public void reportError(String key, HttpServerErrorException error) throws IOException {
        HttpStatus statusCode = error.getStatusCode();
        writeTotalReport(key, statusCode);

        File report = new File(dir, key + ".error");
        Files.write(error.getResponseBodyAsString(), report, Charsets.UTF_8);
    }

    private void writeTotalReport(String key, HttpStatus statusCode) throws IOException {
        File totalReport = new File(dir, "report.csv");
        Files.append(createRecord(key, statusCode), totalReport, Charsets.UTF_8);
    }

    private String createRecord(String key, HttpStatus statusCode) {
        return key + ";" + statusCode.value();
    }
}
