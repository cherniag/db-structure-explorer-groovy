package mobi.nowtechnologies.ingestors.fuga;

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

public class FugaParser extends DDEXParser {
	protected static final Log LOG = LogFactory.getLog(FugaParser.class);

	private String root;

	public FugaParser() {
		root = Property.getInstance().getStringValue("ingest.fuga.root");
		LOG.info("Fuga parser loadin from " + root);
	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
		try {
			File folder = new File(drop.name);
			File xml = getXmlFile(folder);
			Map<String, DropTrack> result = loadXml(xml.getAbsolutePath());

			if (result != null) {
				tracks.putAll(result);
			}

		} catch (Exception e) {
			LOG.error("Ingest failed " + e.getMessage());
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
			} else if ("ingest.ack".equals(file.getName())) {
				processed = true;
			} else if (auto && "autoingest.ack".equals(file.getName())) {
				processed = true;
			} else {
				File xml = getXmlFile(folder);
				if (xml != null && xml.exists()) {
					deliveryComplete = true;
				}
			}
		}
		if (deliveryComplete && !processed) {
			LOG.debug("Adding " + folder.getAbsolutePath() + " to drops");
			DropData drop = new DropData();
			drop.name = folder.getAbsolutePath();
			drop.date = new Date(folder.lastModified());

			result.add(drop);
		}
		return result;
	}

	protected File getXmlFile(File folder) {
		int codeSep = folder.getName().indexOf('_');
		if (codeSep > 0) {
			String code = folder.getName().substring(0, codeSep);
			LOG.debug("Checking " + folder.getAbsoluteFile() + "/" + code + ".xml");
			File xml = new File(folder.getAbsoluteFile() + "/" + code + ".xml");
			return xml;
		}
		return null;

	}

	@Override
	public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
		String ISRC = release.getChild("ReleaseId").getChildText("ISRC");
		String id = release.getChild("ReleaseId").getChildText("ProprietaryId");
		if (id != null && !"".equals(id))
			track.productCode = id;
		else
			track.productCode = ISRC;
		track.isrc = ISRC;
		track.physicalProductId = ISRC;
		track.productId = ISRC;
	}

	public void setUpc(DropTrack track, String upc) {
		if (upc != null) {
			track.productCode = upc;
		}
	}
	
	public boolean checkAlbum(String type, DropTrack track) {
		if ("TrackRelease".equals(type) || "Single".equals(type)) {
			if (track.isrc == null || "".equals(track.isrc)) {
				LOG.info("Album for "+type+" (no ISRC)");
				return  true;
			} else {
				LOG.info("Track for "+type+" "+track.isrc);
			}
		} else if ("Album".equals(type) || "SingleResourceRelease".equals(type)) {
			LOG.info("Album for "+type);
			return  true;
		}
		return false;
	}


}