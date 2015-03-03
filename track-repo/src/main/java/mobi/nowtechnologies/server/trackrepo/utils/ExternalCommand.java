package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;

public class ExternalCommand {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExternalCommand.class);
    private Resource command;

    public String executeCommand(String... params) throws IOException, InterruptedException {

        LOGGER.debug("ExternalCommand.executeCommand start running : {} with params: {}", command.getFilename(), Arrays.toString(params));

        ExternalCommandThread thread = new ExternalCommandThread();
        appendCodeDependsFromOS(thread);
        for (String param : params) {
            thread.addParam(param);
        }

        thread.run();


        if (thread.getExitCode() == 0) {
            LOGGER.debug("ExternalCommand.executeCommand successful finihed: " + command.getFilename());
            return thread.getOutBuffer();
        }
        else {
            LOGGER.error("ExternalCommand.executeCommand failed : {}, with exit code {}", command.getFilename(), thread.getExitCode());
            throw new RuntimeException("Cannot encode track files or create zip package: execution of " + thread.getExitCode() + " returned exit code " + thread.getExitCode());
        }
    }

    private void appendCodeDependsFromOS(ExternalCommandThread thread) throws IOException {
        if (!SystemUtils.IS_OS_WINDOWS) {
            thread.addParam("bash");
        }
        thread.addParam(command.getFile().getAbsolutePath());
    }

    public void setCommand(Resource command) {
        this.command = command;
    }
}
