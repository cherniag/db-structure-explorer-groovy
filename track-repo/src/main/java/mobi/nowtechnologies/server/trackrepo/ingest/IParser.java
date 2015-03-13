package mobi.nowtechnologies.server.trackrepo.ingest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.ResourceUtils.getFile;

public abstract class IParser {

    public static final String DELIVERY_COMPLETE = "delivery.complete";
    public static final String INGEST_ACK = "ingest.ack";
    public static final String AUTO_INGEST_ACK = "autoingest.ack";
    private static final Logger LOGGER = LoggerFactory.getLogger(IParser.class);
    protected String root;

    protected IParser(String root) throws FileNotFoundException {
        this.root = getFile(root).getAbsolutePath();
        LOGGER.info("[{}] parser loading from [{}]", getClass().getSimpleName(), root);
    }

    protected boolean isDirectory(File file) {
        try {
            if (file.isDirectory() || file.getCanonicalFile().isDirectory()) {
                boolean symlink = file.getCanonicalPath().equals(file.getParentFile().getCanonicalPath());
                return symlink ?
                       false :
                       true;
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    public void commit(DropData drop, boolean auto) throws IOException, InterruptedException {
        if (!auto) {
            File commitFile = new File(drop.name + "/" + INGEST_ACK);
            try {
                commitFile.createNewFile();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        File commitFile = new File(drop.name + "/" + AUTO_INGEST_ACK);
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public String getRoot() {
        return root;
    }

    public abstract Map<String, DropTrack> ingest(DropData drop);

    public abstract List<DropData> getDrops(boolean auto) throws IOException;
}
