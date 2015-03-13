package mobi.nowtechnologies.server.trackrepo.ingest.warner;


import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;

import java.io.FileNotFoundException;
import java.util.List;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarnerParserV34 extends DDEXParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarnerParserV34.class);

    public WarnerParserV34(String root) throws FileNotFoundException {
        super(root);
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
    public void setGRid(DropTrack track, String GRid) {
        if (GRid != null) {
            track.productCode = GRid;
        }
    }

    @Override
    protected Integer getDuration(String duration) {
        return super.getDuration(duration) * 1000;
    }

    @Override
    protected boolean isPriorityImage(Element node) {
        return ("VideoScreenCapture").equals(node.getChild("ImageType").getValue());
    }
}