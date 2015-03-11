package mobi.nowtechnologies.server.trackrepo.ingest.warner;

import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarnerParser extends DDEXParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarnerParser.class);

    public WarnerParser(String root) throws FileNotFoundException {
        super(root);
    }

    public Map<String, DropTrack> ingest(DropData drop) {
        Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
        try {
            File folder = new File(drop.name);
            File[] content = folder.listFiles();
            for (File file : content) {
                String xmlFileName = file.getName() + ".xml";
                Map<String, DropTrack> result = loadXml(new File(file.getAbsolutePath() + "/" + xmlFileName));

                if (result != null) {
                    tracks.putAll(result);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return tracks;
    }

    @Override
    public List<DropData> getDrops(boolean auto) {
        List<DropData> result = new ArrayList<DropData>();
        File rootFolder = new File(root);
        result.addAll(getDrops(rootFolder, auto));

        return result;
    }

    public List<DropData> getDrops(File folder, boolean auto) {
        List<DropData> result = new ArrayList<DropData>();
        if (!folder.exists()) {
            LOGGER.warn("Skipping drops scanning: folder [{}] does not exists!", folder.getAbsolutePath());
            return result;
        }

        File[] content = folder.listFiles();
        boolean deliveryComplete = false;
        boolean processed = false;
        for (File file : content) {
            if (isDirectory(file)) {
                LOGGER.info("Scanning directory [{}]", file.getAbsolutePath());
                result.addAll(getDrops(file, auto));
            } else if (DELIVERY_COMPLETE.equals(file.getName())) {
                deliveryComplete = true;
            } else if (INGEST_ACK.equals(file.getName())) {
                processed = true;
            } else if (auto && AUTO_INGEST_ACK.equals(file.getName())) {
                processed = true;
            }
        }
        if (deliveryComplete && !processed) {
            LOGGER.debug("Adding [{}] to drops", folder.getAbsolutePath());
            DropData drop = new DropData();
            drop.name = folder.getAbsolutePath();
            drop.date = new Date(folder.lastModified());

            LOGGER.info("The drop was found: [{}]", drop.name);
            result.add(drop);
        }
        return result;
    }

    @Override
    public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
        String grid = release.getChild("ReleaseId").getChildText("GRid");
        track.physicalProductId = grid;
        track.productId = grid;
        String ISRC = release.getChild("ReleaseId").getChildText("ISRC");
        track.isrc = ISRC;
        track.productCode = grid;
    }

    @Override
    protected String getAssetFile(String root, String file) {
        return root + "/" + file;
    }


}