package mobi.nowtechnologies.server.trackrepo.ingest.sony;


import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;

import java.io.FileNotFoundException;
import java.util.List;

import org.jdom.Element;

public class SonyDDEXParser extends DDEXParser {

    public SonyDDEXParser(String root) throws FileNotFoundException {
        super(root);
    }

    @Override
    public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
        String id = release.getChild("ReleaseId").getChildText("ProprietaryId");
        String grid = release.getChild("ReleaseId").getChildText("GRid");
        for (DropAssetFile file : files) {
            if (file.isrc != null) {
                track.isrc = file.isrc;
            }
        }
        track.productCode = id;
        track.physicalProductId = grid;
        track.productId = grid;
    }

    public void setUpc(DropTrack track, String upc) {
        if (upc != null) {
            track.productCode = upc;
        }
    }

    @Override
    protected Integer getDuration(String duration) {
        //additional check just in case not to ruin audio duration
        if (super.getDuration(duration) < 1000) {
            return super.getDuration(duration) * 1000;
        } else {
            return super.getDuration(duration);
        }
    }
}