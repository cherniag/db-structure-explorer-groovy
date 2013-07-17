package mobi.nowtechnologies.server.trackrepo.ingest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class IParser {

	public abstract Map<String, DropTrack> ingest(DropData drop);

	public void commit(DropData drop, boolean auto) throws IOException, InterruptedException {
		if (!auto) {
			String commitFileName = drop.name + "/ingest.ack";
			File commitFile = new File(commitFileName);
			try {
				commitFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String commitFileName = drop.name + "/autoingest.ack";
		File commitFile = new File(commitFileName);
		try {
			commitFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract List<DropData> getDrops(boolean auto);

	protected boolean isDirectory(File file) {
		try {
			if (file.isDirectory() || file.getCanonicalFile().isDirectory()) {
				if (file.getCanonicalPath().equals(file.getParentFile().getCanonicalPath())) { // Link
																								// loop.....
																								// don't
																								// follow
					return false;
				} else {
					return true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
