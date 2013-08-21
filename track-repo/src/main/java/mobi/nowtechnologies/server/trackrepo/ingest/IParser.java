package mobi.nowtechnologies.server.trackrepo.ingest;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class IParser {

	public abstract Map<String, DropTrack> ingest(DropData drop);

    protected String root;

    protected IParser(String root) throws FileNotFoundException {
        this.root = ResourceUtils.getFile(root).getAbsolutePath();
    }

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
                boolean symlink = file.getCanonicalPath().equals(file.getParentFile().getCanonicalPath());
                return symlink ? false : true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
