package mobi.nowtechnologies.server.trackrepo.ingest.emi;

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

public class EmiParser extends DDEXParser {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EmiParser.class);

    public EmiParser(String root) throws FileNotFoundException {
        super(root);
    }

    public Map<String, DropTrack> ingest(DropData drop) {

        Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
        try {
            File folder = new File(drop.name);
            File xml = getXmlFile(folder);
            Map<String, DropTrack> result = loadXml(xml);

            if (result != null) {
                tracks.putAll(result);
            }

        }
        catch (Exception e) {
            LOGGER.error("Ingest failed " + e.getMessage());
        }
        return tracks;

    }

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
            try {
                if (isDirectory(file)) {
                    LOGGER.info("Scanning directory [{}]", file.getAbsolutePath());
                    result.addAll(getDrops(file, auto));
                }
                else if (INGEST_ACK.equals(file.getName())) {
                    processed = true;
                }
                else if (auto && AUTO_INGEST_ACK.equals(file.getName())) {
                    processed = true;
                }
                else {
                    File xml = getXmlFile(folder);
                    if (xml != null && xml.exists()) {
                        deliveryComplete = true;
                    }
                }
            }
            catch (Exception e1) {
                LOGGER.error("Ingest failed " + e1.getMessage());
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

    protected File getXmlFile(File folder) {
        int codeSep = folder.getName().indexOf('_');
        if (codeSep > 0) {
            String code = folder.getName().substring(0, codeSep);
            LOGGER.info("Checking " + folder.getAbsoluteFile() + "/" + code + ".xml");
            File xml = new File(folder.getAbsoluteFile() + "/" + code + ".xml");
            return xml;
        }
        return null;

    }

    @Override
    public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
        String id = parseProprietaryId(release.getChild("ReleaseId").getChildText("ProprietaryId"));
        for (DropAssetFile file : files) {
            if (file.isrc != null) {
                track.isrc = file.isrc;
            }
        }
        LOGGER.info("getIds -> ID = {}", id);

        track.productCode = id;
        track.physicalProductId = id;
        track.productId = id;

    }

    /**
     * Callback method for customization id parsing in hierarchy of EMI like parsers - override and customize.
     *
     * @param proprietaryId
     * @return
     */
    protected String parseProprietaryId(String proprietaryId) {
        return proprietaryId;
    }

    public void setUpc(DropTrack track, String upc) {
        LOGGER.info("setUpc -> upc = {}", upc);
        if (upc != null) {
            track.productCode = upc;
        }
    }

}