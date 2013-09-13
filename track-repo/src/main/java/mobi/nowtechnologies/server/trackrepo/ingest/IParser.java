package mobi.nowtechnologies.server.trackrepo.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ResourceUtils.*;

public abstract class IParser{

    private static final Logger LOGGER = LoggerFactory.getLogger(IParser.class);

    protected String root;

    protected IParser(String root) throws FileNotFoundException {
        this.root = getFile(root).getAbsolutePath();
    }

    protected boolean isDirectory(File file) {
        try {
            if (file.isDirectory() || file.getCanonicalFile().isDirectory()) {
                boolean symlink = file.getCanonicalPath().equals(file.getParentFile().getCanonicalPath());
                return symlink ? false : true;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

	public void commit(DropData drop, boolean auto) throws IOException, InterruptedException {
		if (!auto) {
            File commitFile = new File(drop.name + "/ingest.ack");
			try {
				commitFile.createNewFile();
			} catch (IOException e) {
                LOGGER.error(e.getMessage());
			}
		}
        File commitFile = new File(drop.name + "/autoingest.ack");
		try {
			commitFile.createNewFile();
		} catch (IOException e) {
            LOGGER.error(e.getMessage());
		}
	}

    public abstract Map<String, DropTrack> ingest(DropData drop);

    public abstract List<DropData> getDrops(boolean auto);

}
