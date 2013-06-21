package mobi.nowtechnologies.ingestors.manual;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mobi.nowtechnologies.domain.AssetFile.FileType;
import mobi.nowtechnologies.ingestors.DDEXParser;
import mobi.nowtechnologies.ingestors.DropAssetFile;
import mobi.nowtechnologies.ingestors.DropData;
import mobi.nowtechnologies.ingestors.DropTerritory;
import mobi.nowtechnologies.ingestors.DropTrack;
import mobi.nowtechnologies.ingestors.IParser;
import mobi.nowtechnologies.ingestors.DropTrack.Type;
import mobi.nowtechnologies.util.Property;

public class ManualParser extends IParser {
	protected static final Log LOG = LogFactory.getLog(ManualParser.class);

	private String root;
	
	public ManualParser() {
		root = Property.getInstance().getStringValue("ingest.manual.root");
		LOG.info("Manual parser loadin from " + root);
	}

	@Override
	public void commit(DropData drop, boolean auto) {
		File dropFile = new File(drop.name);
		String commitFileName = dropFile.getParent() + "/ingest.ack";
		File commitFile = new File(commitFileName);
		try {
			commitFile.createNewFile();
		} catch (IOException e) {
			LOG.error("Ingest failed "+e.getMessage());
		}
	}

	@Override
	public List<DropData> getDrops(boolean auto) {
		List<DropData> result = new ArrayList<DropData>();
		if (auto)
			return result;
		File rootFolder = new File(root);
		for (File file: rootFolder.listFiles()) {
			if (isDirectory(file)) {
				result.addAll(getDrops(file));
			}
		}

		return result;
	}
	protected List<DropData> getDrops(File rootFolder) {
		List<DropData> result = new ArrayList<DropData>();
		boolean processed = false;
		File csv = null;
		for (File file: rootFolder.listFiles()) {
			if (file.getName().equals("ingest.ack")) {
				processed = true;
			}
			if (file.getName().endsWith(".csv")) {
				csv = file;
			}
		}
		if (!processed && csv != null) {
			DropData data = new DropData();
			data.date = new Date(csv.lastModified());
			try {
				data.name = csv.getCanonicalPath();
			} catch (IOException e) {
				LOG.error("getdrops failed "+e.getMessage());
			}
			result.add(data);
		}

		return result;
	}


	@Override
	public Map<String, DropTrack> ingest(DropData drop) {
		Map<String, DropTrack> result = new HashMap<String, DropTrack>();
		try {
			File dropFile = new File(drop.name);
			BufferedReader in  = new BufferedReader(new FileReader(dropFile));
			in.readLine(); // Skip header
			String line;
			while ((line = in.readLine()) != null) {
				String[] tokens = line.split("#");
				LOG.info("Token lenght "+tokens.length+" "+line);
				DropTrack track = new DropTrack();
				track.type = Type.INSERT;
				track.xml="";
				track.title = tokens[1];
				track.artist = tokens[2];
				track.isrc = tokens[3];
				track.productCode = track.isrc;
				track.productId = track.isrc;
				track.files = new ArrayList<DropAssetFile>();
				DropAssetFile image = new DropAssetFile();
				image.type = FileType.IMAGE;
				image.file = dropFile.getParent()+"/"+tokens[5];
				track.files.add(image);
				DropAssetFile download = new DropAssetFile();
				download.type = FileType.DOWNLOAD;
				download.file = dropFile.getParent()+"/"+tokens[4];
				track.files.add(download);
				track.info = tokens[7];
				track.label = tokens[8];
				track.territories = new ArrayList<DropTerritory>();
				DropTerritory territory = new DropTerritory();
				territory.country="Worldwide";
				territory.label = track.label;
				territory.distributor = "MANUAL";
				territory.reportingId = track.isrc;
				territory.startdate = new Date();
				track.territories.add(territory);
				
				if (tokens.length > 9 && !"".equals(tokens[9])) {
					// Mobile file
					DropAssetFile mobile = new DropAssetFile();
					mobile.type = FileType.MOBILE;
					mobile.file = dropFile.getParent()+"/"+tokens[9];
					track.files.add(mobile);
				}
				if (tokens.length > 10) {
					// Unlicensed flag
					if ("no".equalsIgnoreCase(tokens[10])) {
						track.licensed = false;
					} else {
						track.licensed = true;
					}
				} else {
					track.licensed = true;
				}

				result.put(track.isrc, track);
				
			}
		} catch (FileNotFoundException e) {
			LOG.error("Ingest failed "+e.getMessage());
		} catch (IOException e) {
			LOG.error("Ingest failed "+e.getMessage());
		}
		return result;
	}


}
