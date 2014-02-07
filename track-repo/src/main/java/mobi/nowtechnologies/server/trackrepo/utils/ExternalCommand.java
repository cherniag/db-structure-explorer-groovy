package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.IOException;

import org.springframework.core.io.Resource;

public class ExternalCommand {

	public String executeCommand(String ... params) throws IOException, InterruptedException {
		
		ExternalCommandThread thread = new ExternalCommandThread();
		thread.setCommand(command.getFile().getAbsolutePath());
		
		for (String param : params) {
			thread.addParam(param);
		}
		
		thread.run();
		
		if (thread.getExitCode() == 0) {
			return thread.getOutBuffer();
		}

		return null;
	}

	
	private Resource command;

	public void setCommand(Resource command) {
		this.command = command;
	}
}
