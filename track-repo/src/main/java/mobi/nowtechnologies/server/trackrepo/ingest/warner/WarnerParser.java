package mobi.nowtechnologies.server.trackrepo.ingest.warner;

import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import java.io.File;
import java.util.*;

public class WarnerParser extends DDEXParser {
	protected static final Log LOG = LogFactory.getLog(DDEXParser.class);

	private String root;

	public WarnerParser(String root) {
        this.root = root;
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