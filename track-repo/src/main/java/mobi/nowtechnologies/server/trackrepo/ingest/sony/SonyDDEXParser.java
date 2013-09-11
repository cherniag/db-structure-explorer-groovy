package mobi.nowtechnologies.server.trackrepo.ingest.sony;


import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.List;

public class SonyDDEXParser extends DDEXParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SonyDDEXParser.class);

	public SonyDDEXParser(String root) throws FileNotFoundException {
        super(root);
		LOGGER.info("Sony DDEX parser loading from [{}]", root);
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

}