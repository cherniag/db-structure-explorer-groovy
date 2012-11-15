package mobi.nowtechnologies.server.trackrepo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ExternalCommandThread implements Callable<String> {
	private Exception ex = null;
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

			InputStream stderr = null;
			InputStream stdout = null;
			String line;
			System.out.println("Executing " + (String[]) command.toArray(new String[command.size()]));
			Process p = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
			stdout = p.getInputStream();
			stderr = p.getErrorStream();

			// clean up if any output in stderr
			BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stderr));
			while ((line = brCleanUp.readLine()) != null) {
				System.err.println("[Stderr] {}" + line);
			}

			brCleanUp.close();

			brCleanUp = new BufferedReader(new InputStreamReader(stdout));
			while ((line = brCleanUp.readLine()) != null) {
				System.out.println("[Stout] {}" + line);
				outBuffer.append(line);
			}
			brCleanUp.close();
			exitCode = p.waitFor();
			System.out.println("Process finished with exit code {}" + exitCode);
	}

	public Exception getException() {
		return ex;
	}

	public String call() throws Exception {
		return key;
	}

	public String call2() throws Exception {
		try {
			InputStream stderr = null;
			InputStream stdout = null;

			Process p = Runtime.getRuntime().exec((String[]) command.toArray());
			stdout = p.getInputStream();
			stderr = p.getErrorStream();

			// clean up if any output in stderr
			BufferedReader brErrCleanUp = new BufferedReader(new InputStreamReader(stderr));
			BufferedReader brOutCleanUp = new BufferedReader(new InputStreamReader(stdout));
			String lineErr = brErrCleanUp.readLine();
			String lineOut = brOutCleanUp.readLine();
			while (lineErr != null && lineOut != null) {
				if (lineErr != null)
					System.err.println("[Stderr] " + lineErr);
				if (lineOut != null)
					System.err.println("[Stdout] " + lineOut);
				 lineErr = brErrCleanUp.readLine();
				 lineOut = brOutCleanUp.readLine();
			}

			brErrCleanUp.close();
			brOutCleanUp.close();

			int exitCode = p.waitFor();
			System.out.println("Process finished with exit code {}" + exitCode);
			if (exitCode == 0)
				return key;
			else
				return null;
		} catch (Exception e) {
			System.err.println("Cannot start {}, Exception: {}" + e.toString());
			throw e;
		}
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}
	public String getOutBuffer() {
		return outBuffer.toString();
	}

}
