package mobi.nowtechnologies.server.trackrepo.ingest.fuga;

import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FugaParser extends DDEXParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FugaParser.class);

	public FugaParser(String root) throws FileNotFoundException {
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

		} catch (Exception e) {
			LOGGER.error("Ingest failed " + e.getMessage());
		}
		return tracks;

	}

	public List<DropData> getDrops(boolean auto) {

		List<DropData> result = new ArrayList<DropData>();
		File rootFolder = new File(root);
		result.addAll(getDrops(rootFolder, auto));
		for (int i = 0; i < result.size(); i++) {
			LOGGER.info("Drop folder [{}]", result.get(i));
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
			} else if (INGEST_ACK.equals(file.getName())) {
				processed = true;
			} else if (auto && AUTO_INGEST_ACK.equals(file.getName())) {
				processed = true;
			} else {
				File xml = getXmlFile(folder);
				if (xml != null && xml.exists()) {
					deliveryComplete = true;
				}
			}
		}
		if (deliveryComplete && !processed) {
			LOGGER.debug("Adding [{}]  to drops", folder.getAbsolutePath());
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
			LOGGER.debug("Checking " + folder.getAbsoluteFile() + "/" + code + ".xml");
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
				LOGGER.info("Album for "+type+" (no ISRC)");
				return  true;
			} else {
				LOGGER.info("Track for "+type+" "+track.isrc);
			}
		} else if ("Album".equals(type) || "SingleResourceRelease".equals(type)) {
			LOGGER.info("Album for "+type);
			return  true;
		}
		return false;
	}


}