package mobi.nowtechnologies.server.trackrepo.ingest.sony;


import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class SonyDDEXParser extends DDEXParser {
	protected static final Log LOG = LogFactory.getLog(SonyDDEXParser.class);

	public SonyDDEXParser(String root) throws FileNotFoundException {
        super(root);
		LOG.info("EMI parser loadin from " + root);
	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
		try {
			File xmlFile = new File(root + "/manifests/"+drop.name);
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();
			List messages = rootNode.getChildren("MessageInBatch");
			for (int i = 0; i < messages.size(); i++) {
				Element message = (Element) messages.get(i);
				String url = message.getChildText("URL");
				int index = url.indexOf("ddex");
				String xml=root+url.substring(index+4);
				LOG.info("Loading "+xml);
						Map<String, DropTrack> result = loadXml(xml);

				if (result != null) {
					tracks.putAll(result);
				}
			}

			

		} catch (Exception e) {
			LOG.error("Ingest failed " + e.getMessage());
		}
		return tracks;

	}

	public void commit(DropData drop, boolean auto) {
		if (!auto) {
			String commitFileName = root + "/manifests/"+drop.name + ".ack";
			File commitFile = new File(commitFileName);
			try {
				commitFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String commitFileName = root + "/manifests/"+drop.name + ".autoingest.ack";
		File commitFile = new File(commitFileName);
		try {
			commitFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<DropData> getDrops(boolean auto) {

		List<DropData> result = new ArrayList<DropData>();

		File manifests = new File(root + "/manifests");
		LOG.info("Checking manifests in " + root + "/manifests");
		File[] manifestFiles = manifests.listFiles();
		for (File manifest : manifestFiles) {
			if (!manifest.getName().endsWith(".ack")) {
				if (!auto) {
				File ackFile = new File(manifest.getPath() + ".ack");
				if (!ackFile.exists()) {
					DropData drop = new DropData();
					drop.name = manifest.getName();
					drop.date = new Date(manifest.lastModified());
					result.add(drop);

				}
				} else {
					File ackFile = new File(manifest.getPath() + ".autoingest.ack");
					if (!ackFile.exists()) {
						DropData drop = new DropData();
						drop.name = manifest.getName();
						drop.date = new Date(manifest.lastModified());
						result.add(drop);

					}
				}
			} 
		}
		return result;
	}


	protected File getXmlFile(File folder) {
		int codeSep = folder.getName().indexOf('_');
		if (codeSep > 0) {
			String code = folder.getName().substring(0, codeSep);
			LOG.info("Checking " + folder.getAbsoluteFile() + "/" + code + ".xml");
			File xml = new File(folder.getAbsoluteFile() + "/" + code + ".xml");
			return xml;
		}
		return null;
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