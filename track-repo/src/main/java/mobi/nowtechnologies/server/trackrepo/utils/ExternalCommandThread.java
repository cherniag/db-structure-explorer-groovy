package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalCommandThread implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalCommandThread.class);

    private String key;
    private List<String> command = new ArrayList<String>();
    private int exitCode;
    private StringBuffer outBuffer = new StringBuffer();

    public String getCommand() {
        return command.get(0);
    }

    public void setCommand(String command) {
        this.command.clear();
        this.command.add(command);
    }

    public void addParam(String param) {
        command.add(param);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void run() throws IOException, InterruptedException {

        LOGGER.info("Executing {}", command);
        Process p = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));

        // clean up if any output in stderr
        String line;
        BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        while ((line = brCleanUp.readLine()) != null) {
            LOGGER.error("[Stderr] {}", line);
        }

        brCleanUp.close();

        brCleanUp = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = brCleanUp.readLine()) != null) {
            LOGGER.info("[Stout] {}", line);
            outBuffer.append(line);
        }
        brCleanUp.close();
        exitCode = p.waitFor();
        p.destroy();
        LOGGER.info("Process finished with exit code {}", exitCode);
    }

    @Override
    public String call() {
        return key;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutBuffer() {
        return outBuffer.toString();
    }

}
