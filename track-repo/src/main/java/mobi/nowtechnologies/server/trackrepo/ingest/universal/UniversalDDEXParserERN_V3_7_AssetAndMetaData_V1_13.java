package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParserERN_V3_7_AssetAndMetaData_V1_13;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.DtdLoader;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ddex.xml.ern.x37.ICPN;
import net.ddex.xml.ern.x37.NewReleaseMessageDocument.NewReleaseMessage;
import net.ddex.xml.ern.x37.Release;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @author Titov Mykhaylo (titov) on 09.02.2015.
public class UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13 extends DDEXParserERN_V3_7_AssetAndMetaData_V1_13 {

    private static final Logger LOGGER = LoggerFactory.getLogger(UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13.class);

    public UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13(String root) throws FileNotFoundException {
        super(root);
    }

    @Override
    protected void getIds(Release release, DropTrack track, List<DropAssetFile> files, NewReleaseMessage newReleaseMessage) {

        track.physicalProductId = release.getReleaseIdArray(0).getProprietaryIdArray(0).getStringValue();

        Release[] releaseArray = newReleaseMessage.getReleaseList().getReleaseArray();
        for (Release release1 : releaseArray) {
            ICPN icpn = release1.getReleaseIdArray(0).getICPN();
            if (isNotNull(icpn)) {
                track.productCode = icpn.getStringValue();
                break;
            }
        }

        track.productId = track.physicalProductId;
    }

    @Override
    public List<DropData> getDrops(boolean auto) {
        List<DropData> result = new ArrayList<>();
        java.io.File deliveries = new java.io.File(root + "/Delivery_Messages");
        if (!deliveries.exists()) {
            LOGGER.warn("Skipping drops scanning: folder [{}] does not exists!", deliveries.getAbsolutePath());
            return result;
        }
        LOGGER.info("Checking manifests in {}/Delivery_Messages: found {} files", root, deliveries.listFiles().length);

        java.io.File[] fulfillmentFiles = deliveries.listFiles();
        for (java.io.File file : fulfillmentFiles) {
            LOGGER.info("Scanning directory [{}]", file.getAbsolutePath());
            if (file.getName().startsWith("delivery") && file.getName().endsWith(".xml")) {
                String order = file.getName().substring(file.getName().indexOf('_') + 1, file.getName().lastIndexOf('.'));
                java.io.File ackManual = new java.io.File(root + "/Delivery_Messages/" + order + ".ack");
                if (!auto) {
                    if (!ackManual.exists()) {
                        DropData drop = new DropData();
                        drop.name = order;
                        drop.date = new Date(file.lastModified());

                        LOGGER.info("The drop was found: [{}]", drop.name);
                        result.add(drop);
                    }
                }
                else {
                    java.io.File ack = new java.io.File(root + "/Delivery_Messages/auto_" + order + ".ack");
                    if (!ack.exists() && !ackManual.exists()) {
                        DropData drop = new DropData();
                        drop.name = order;
                        drop.date = new Date(file.lastModified());

                        LOGGER.info("The drop was found: [{}]", drop.name);
                        result.add(drop);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Map<String, DropTrack> ingest(DropData drop) {

        Map<String, DropTrack> result = new HashMap<>();
        try {
            File fulfillment = new File(root + "/Delivery_Messages/fulfillment_" + drop.name + ".xml");
            SAXBuilder builder = new SAXBuilder();
            builder.setEntityResolver(new DtdLoader());
            LOGGER.info("Loading [{}]", fulfillment.getPath());

            try {
                Document document = builder.build(fulfillment);
                Element rootNode = document.getRootElement();

                @SuppressWarnings("unchecked") List<Element> products = rootNode.getChild("products").getChildren("product");
                for (Element product : products) {
                    result.putAll(parseProduct(drop.name, product));
                }
            }
            catch (IOException | JDOMException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        catch (Exception e) {
            LOGGER.error("Can't ingest [{}] drop", drop, e);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, DropTrack> parseProduct(String dropId, Element product) {
        String code = product.getChildText("upc");
        return loadXml(dropId, code);
    }

    public Map<String, DropTrack> loadXml(String drop, String code) {
        LOGGER.info("Scanning {}/{}_{}", root, code, drop);
        File productDir = new File(root + "/" + code + "_" + drop);
        File[] files = productDir.listFiles();
        Map<String, DropTrack> resultDropTracksWithMetadata = new HashMap<>();
        for (File file : files) {
            String name = file.getName();
            if (name.contains("DDEX") && name.endsWith(".xml")) {
                resultDropTracksWithMetadata.putAll(loadXml(file));
            }
        }

        return resultDropTracksWithMetadata;
    }

    @Override
    public void commit(DropData drop, boolean auto) {
        if (!auto) {
            File commitFile = new File(root + "/Delivery_Messages/" + drop.name + ".ack");
            try {
                commitFile.createNewFile();
            }
            catch (IOException e) {
                LOGGER.error("Can't create file [{}]", commitFile, e);
            }
        }
        File commitFile = new File(root + "/Delivery_Messages/auto_" + drop.name + ".ack");
        try {
            commitFile.createNewFile();
        }
        catch (IOException e) {
            LOGGER.error("Can't create file [{}]", commitFile, e);
        }
    }

}
