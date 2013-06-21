package mobi.nowtechnologies.ingestors.warner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import mobi.nowtechnologies.ingestors.DDEXParser;
import mobi.nowtechnologies.ingestors.DropData;
import mobi.nowtechnologies.ingestors.DropAssetFile;
import mobi.nowtechnologies.ingestors.DropTrack;
import mobi.nowtechnologies.util.Property;

public class WarnerParser extends DDEXParser {
	protected static final Log LOG = LogFactory.getLog(DDEXParser.class);

	private String root;

	public WarnerParser() {
		root = Property.getInstance().getStringValue("ingest.warner.root");
		LOG.info("Warner parser loadin from " + root);
	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
		try {
			File folder = new File(drop.name);
			File[] content = folder.listFiles();
			for (File file : content) {
				String xmlFileName = file.getName() + ".xml";
				Map<String, DropTrack> result = loadXml(file.getAbsolutePath() + "/" + xmlFileName);

				if (result != null) {
					tracks.putAll(result);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tracks;

	}


	public List<DropData> getDrops(boolean auto) {

		List<DropData> result = new ArrayList<DropData>();
		File rootFolder = new File(root);
		result.addAll(getDrops(rootFolder, auto));
		for (int i = 0; i < result.size(); i++) {
			LOG.info("Drop folder " + result.get(i));
		}
		return result;
	}

	public List<DropData> getDrops(File folder, boolean auto) {

		List<DropData> result = new ArrayList<DropData>();
		File[] content = folder.listFiles();
		boolean deliveryComplete = false;
		boolean processed = false;
		for (File file : content) {
			if (isDirectory(file)) {
				result.addAll(getDrops(file, auto));
			} else if ("delivery.complete".equals(file.getName())) {
				deliveryComplete = true;
			} else if ("ingest.ack".equals(file.getName())) {
				processed = true;
			} else if (auto && "autoingest.ack".equals(file.getName())) {
				processed = true;
			}
		}
		if (deliveryComplete && !processed) {
			LOG.debug("Adding " + folder.getAbsolutePath() + " to drops");
			DropData drop = new DropData();
			drop.name =folder.getAbsolutePath();
			drop.date = new Date(folder.lastModified());

			result.add(drop);
		}
		return result;
	}

	@Override
	public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
		String grid = release.getChild("ReleaseId").getChildText("GRid");
		track.physicalProductId= grid;
		track.productId= grid;
		String ISRC = release.getChild("ReleaseId").getChildText("ISRC");
		track.isrc= ISRC;
		track.productCode= grid;
	}
	
	@Override	
	protected String getAssetFile(String root, String file) {
		return root + "/" + file;
	}



}