package mobi.nowtechnologies.server.trackrepo.ingest.manual;

import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;
import static org.springframework.util.StringUtils.isEmpty;

public class ManualParser extends IParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManualParser.class);

	public ManualParser(String root) throws FileNotFoundException {
        super(root);
	}

	@Override
	public void commit(DropData drop, boolean auto) {
		File dropFile = new File(drop.name);
		String commitFileName = dropFile.getParent() + "/" + INGEST_ACK;
		File commitFile = new File(commitFileName);
		try {
			commitFile.createNewFile();
		} catch (IOException e) {
			LOGGER.error("Ingest failed "+e.getMessage());
		}
	}

	@Override
	public List<DropData> getDrops(boolean auto) {
		List<DropData> result = new ArrayList<DropData>();
		if (auto)
			return result;
		File rootFolder = new File(root);
		if (!rootFolder.exists()) {
			LOGGER.warn("Skipping drops scanning: folder [{}] does not exists!", rootFolder.getAbsolutePath());
			return result;
		}
		for (File file: rootFolder.listFiles()) {
			if (isDirectory(file)) {
                LOGGER.info("Scanning directory [{}]", file.getAbsolutePath());
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
			if (file.getName().equals(INGEST_ACK)) {
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
				LOGGER.error("getDrops failed "+e.getMessage());
			}
			result.add(data);
            LOGGER.info("The drop was found: [{}]", data.name);
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
				LOGGER.info("Token length [{}] line", tokens.length);
				DropTrack track = new DropTrack();
				track.type = INSERT;
				track.xml = "";
				track.title = tokens[1];
				track.artist = tokens[2];
				track.isrc = tokens[3];
				track.productCode = !isEmpty(tokens[6]) ? tokens[6] : track.isrc;
				track.productId = track.isrc;
				track.info = ""; // is not used
                track.label = tokens[8];
                track.territories = getDropTerritories(tokens[7], track);

                track.files = new ArrayList<DropAssetFile>();
                DropAssetFile image = new DropAssetFile();
                image.type = IMAGE;
                image.file = dropFile.getParent()+"/"+tokens[5];
                track.files.add(image);

                if (!"".equals(tokens[4])) {
					DropAssetFile download = new DropAssetFile();
					download.type = DOWNLOAD;
					download.file = dropFile.getParent()+"/"+tokens[4];
					track.files.add(download);
				}
				track.setReportingType(ReportingType.valueOf(tokens[9]));

				if (tokens.length > 10 && !"".equals(tokens[10])) {
					// Mobile file
					DropAssetFile mobile = new DropAssetFile();
					mobile.type = MOBILE;
					mobile.file = dropFile.getParent() + "/" + tokens[10];
					track.files.add(mobile);
				}

				if (tokens.length > 11 && !"".equals(tokens[11])) {
					// Video file
					DropAssetFile mobile = new DropAssetFile();
					mobile.type = VIDEO;
					mobile.file = dropFile.getParent() + "/" + tokens[11];
					if (tokens.length > 12 && !"".equals(tokens[12])) {
						mobile.duration = Integer.parseInt(tokens[12]);
					}
					track.files.add(mobile);
				}

				if (tokens.length > 13) {
					// Unlicensed flag
					if ("no".equalsIgnoreCase(tokens[13])) {
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
			LOGGER.error("Ingest failed", e);
		} catch (IOException e) {
			LOGGER.error("Ingest failed", e);
		}
		return result;
	}

    private List<DropTerritory> getDropTerritories(String territories, DropTrack track) {
        ArrayList<DropTerritory> dropTerritories = new ArrayList<DropTerritory>();
        if(!isEmpty(territories)){
            for (String countryName : territories.split(",")) {
                DropTerritory territory = getDropTerritory(track, countryName.trim());
                dropTerritories.add(territory);
            }
        } else {
            DropTerritory territory = getDropTerritory(track, "Worldwide");
            dropTerritories.add(territory);
        }
        return dropTerritories;
    }

    private DropTerritory getDropTerritory(DropTrack track, String country) {
        DropTerritory territory = new DropTerritory();
        territory.country= country;
        territory.label = track.label;
        territory.distributor = "MANUAL";
        territory.reportingId = track.isrc;
        territory.startdate = new Date();
        return territory;
    }
}
