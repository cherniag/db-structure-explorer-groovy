package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import net.sf.saxon.s9api.*;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//import net.sf.saxon.s9api.*;

public class AbsoluteParser extends DDEXParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteParser.class);

    private static XPathCompiler xPathCompiler;
    private static XdmNode xdmNode;
    private XPathSelector proprietaryIdXPathSelector;
    private QName isrcQName;

    private void prepareXPath(File file) throws SaxonApiException {
        Processor processor = new Processor(false);
        xPathCompiler = processor.newXPathCompiler();
        isrcQName = new QName("isrc");
        xPathCompiler.declareVariable(isrcQName);
        DocumentBuilder builder = processor.newDocumentBuilder();
        xdmNode = builder.build(file);

        compileXPathExpressions();
    }

    private void compileXPathExpressions() throws SaxonApiException {
        proprietaryIdXPathSelector = xPathCompiler.compile("//ResourceList/SoundRecording/SoundRecordingId[ISRC eq $isrc]/ProprietaryId").load();
        proprietaryIdXPathSelector.setContextItem(xdmNode);
    }

    private String getProprietaryId(String isrc) throws SaxonApiException {
        return evaluate(proprietaryIdXPathSelector, isrc);
    }

    private String evaluate(XPathSelector xPathSelector, String isrc) throws SaxonApiException {
        XdmValue children = getXmlValue(xPathSelector, isrc);
        if(children.size()>0){
            return children.itemAt(0).getStringValue();
        }
        return null;
    }

    private XdmValue getXmlValue(XPathSelector xPathSelector, String isrc) throws SaxonApiException {
        xPathSelector.setVariable(isrcQName, new XdmAtomicValue(isrc));
        return xPathSelector.evaluate();
    }

    public AbsoluteParser(String root) throws FileNotFoundException {
        super(root);
    }

    @Override
    public Map<String, DropTrack> loadXml(File file) {
        try {
            prepareXPath(file);
            return super.loadXml(file);
        } catch (SaxonApiException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.<String, DropTrack>emptyMap();
    }

    @Override
    protected List<DropData> getDrops(File folder, boolean auto) {
        List<DropData> result = new ArrayList<DropData>();
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
    protected boolean checkAlbum(String type) {
        if ("Album".equals(type) || "SingleResourceRelease".equals(type)) {
            LOGGER.info("Album for [{}]", type);
            return true;
        }
        LOGGER.info("Track for [{}]", type);
        return false;
    }

    @Override
    protected boolean validDealUseType(Element dealTerms) {
        boolean validUseType = super.validDealUseType(dealTerms);
        if (!validUseType){
            Element commercialModelTypeElement = dealTerms.getChild("CommercialModelType");
            validUseType = "AsPerContract".equals(commercialModelTypeElement.getText());
        }

        return validUseType;
    }

    @Override
    protected void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
        String isrc = release.getChild("ReleaseId").getChildText("ISRC");
        try {
            track.productCode = getProprietaryId(isrc);
        } catch (SaxonApiException e) {
            LOGGER.error(e.getMessage());
        }
        track.physicalProductId = isrc;
        track.productId = isrc;
    }
}
