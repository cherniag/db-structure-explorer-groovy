package mobi.nowtechnologies.ingestors.ci;

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

public class CiParser extends DDEXParser {
	protected static final Log LOG = LogFactory.getLog(CiParser.class);

	private String root;

	public CiParser() {
		root = Property.getInstance().getStringValue("ingest.ci.root");
		LOG.info("CI parser loading from " + root);
	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
		try {
			File folder = new File(drop.name);
			List<File> xmls = getXmlFile(folder);
			for (File xml : xmls) {
				Map<String, DropTrack> result = loadXml(xml.getAbsolutePath());

				if (result != null) {
					tracks.putAll(result);
				}
			}

		} catch (Exception e) {
			LOG.error("Ingest failed "+e.getMessage());
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
				if (file.getName().startsWith("BatchComplete_")) {
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

	protected List<File> getXmlFile(File folder) {
		File[] content = folder.listFiles();
		List<File> result = new ArrayList<File>();
		for (File file : content) {
			if (isDirectory(file)) {
				File[] dirContent = file.listFiles();
				for (File dirContentFile : dirContent) {
					if (dirContentFile.getName().endsWith(".xml")) {
						result.add(dirContentFile);
					}
				}
			}
		}
		return result;

	}

	@Override
	public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
		Element id = release.getChild("ReleaseId");
		if (id.getChildText("GRid") != null) {
			track.productId = id.getChildText("GRid");
		}
		track.productCode = id.getChildText("ProprietaryId");
		track.physicalProductId = id.getChildText("ProprietaryId");
		track.isrc = id.getChildText("ISRC");
	}

}