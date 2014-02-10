package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class ExternalCommand {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(ExternalCommand.class);

	public String executeCommand(String ... params) throws IOException, InterruptedException {
		
		ExternalCommandThread thread = new ExternalCommandThread();
		thread.setCommand(command.getFile().getAbsolutePath());
		
		for (String param : params) {
			thread.addParam(param);
		}
		
		thread.run();
		
		LOGGER.debug("ExternalCommand.executeCommand start runnig: " + command.getFilename());

		if (thread.getExitCode() == 0) {
			LOGGER.debug("ExternalCommand.executeCommand successful finihed: " + command.getFilename());
			return thread.getOutBuffer();
		} else {
			LOGGER.error("ExternalCommand.executeCommand failed : {}, with exit code {}", command.getFilename(), thread.getExitCode());
			throw new RuntimeException("Cannot encode track files or create zip package: execution of " +thread.getExitCode() + " returned exit code " + thread.getExitCode());
		}
	}
	
	private Resource command;

	public void setCommand(Resource command) {
		this.command = command;
	}
}
