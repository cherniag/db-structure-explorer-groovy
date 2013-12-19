package mobi.nowtechnologies.server.trackrepo.ingest.sony;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommandThread;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SonyParser extends IParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonyParser.class);

	private ArrayList<String> files = new ArrayList<String>();;
	private String logFile;
	private String tempLogFile;

	public SonyParser(String root) throws FileNotFoundException {
		super(root);
	}

	protected DropTrack loadXml(String file) {

		DropTrack result = new DropTrack();
		SAXBuilder builder = new SAXBuilder();
		LOGGER.debug("Loading [{}]", file);
		File xmlFile = new File(file);

		try {

			Document document = builder.build(xmlFile);
			Element rootNode = document.getRootElement();

			XMLOutputter outputter = new XMLOutputter();
			result.xml = outputter.outputString(rootNode);

			// Parse media files
			Element actionRoot = rootNode.getChild("Action");
			String type = actionRoot.getAttribute("Type").getValue();
			if ("UPDATE".equals(type)) {
				result.type = Type.UPDATE;
			} else if ("INSERT".equals(type)) {
				result.type = Type.INSERT;
			} else if ("DELETE".equals(type)) {
				result.type = Type.DELETE;
			}

			if (result.type == Type.INSERT || result.type == Type.UPDATE) {
				String product = actionRoot.getChild("Product").getAttribute("Type").getValue();
				if (!"TRACK".equals(product)) {
					LOGGER.info("Skipping product " + product);
					return null;
				}
				String typeName = actionRoot.getChild("Product").getChildText("TypeName");
				if (!"Single".equals(typeName)) {
					LOGGER.info("Skipping type " + typeName);
					return null;
				}
				String prodId = actionRoot.getChild("Product").getChildText("ProdID");
				result.productId = prodId;
				String productCode = actionRoot.getChild("Product").getChildText("UPC");
				result.productCode = productCode;
				Element track = actionRoot.getChild("Product").getChild("Track");
				Element metadata = track.getChild("MetaData");
				String title = metadata.getChild("Title").getValue();
				result.title = title;
				String version = metadata.getChild("Version").getValue();
				result.subTitle = version;
				String artist = metadata.getChild("Artist").getValue();
				result.artist = artist;
				String genre = metadata.getChild("Genre").getAttributeValue("name");
				result.genre = genre;
                String explicit = metadata.getChild("Advisory").getValue();
				result.explicit = Boolean.valueOf(explicit);
				String copyright = metadata.getChild("Copyright").getValue();
				result.copyright = copyright;
				String label = metadata.getChild("Label").getValue();
				result.label = label;
				String isrc = metadata.getChild("ISRC").getValue();
				result.isrc = isrc;
				String year = metadata.getChildText("Year");
				result.year = year;
				Element physicalProduct = metadata.getChild("PhysicalProduct");
				String physicalProductId = physicalProduct.getChildText("ProductID");
				result.physicalProductId = physicalProductId;
				if (result.physicalProductId != null && !"".equals(result.physicalProductId)) {
					result.productId = physicalProductId;
				}

				if (result.productCode == null || "".equals(result.productCode)) {
					result.productCode = physicalProduct.getChildText("ProductCode");
				}
				String album = physicalProduct.getChildText("Title");
				result.album = album;

				LOGGER.info("Loading " + file + " type " + type + " title " + title + " isrc " + isrc);
				String provider = metadata.getChild("Provider").getValue();
				String publisher = metadata.getChild("Publisher").getValue();

				List<Element> audioTracks = track.getChildren("AudioDownload");
				for (Element audio : audioTracks) {
					String codec = audio.getChild("Codec").getValue();
					String url = audio.getChild("File").getChildText("SourceURL");
					if (url != null && !"".equals(url)) {
						for (String searchFile : files) {
							if (searchFile.contains(url)) {
								if ("MP3".equals(codec)) {
									DropAssetFile asset = new DropAssetFile();
									asset.file = root + "/" + searchFile;
									asset.type = FileType.DOWNLOAD;
									result.files.add(asset);
									break;
								}
								if ("AAC+ m4a".equals(codec)) {
									DropAssetFile asset = new DropAssetFile();
									asset.file = root + "/" + searchFile;
									asset.type = FileType.MOBILE;
									result.files.add(asset);
									break;
								}
							}
						}
					}

				}
				Element previewTrack = track.getChild("AudioSampleClip");
				String url = previewTrack.getChild("File").getChildText("SourceURL");
				if (url != null && !"".equals(url)) {
					for (String searchFile : files) {
						if (searchFile.contains(url)) {
							DropAssetFile asset = new DropAssetFile();
							asset.file = root + "/" + searchFile;
							asset.type = FileType.PREVIEW;
							result.files.add(asset);
							break;
						}
					}
				}
				Element image = track.getChild("Graphic");
				url = image.getChild("File").getChildText("SourceURL");
				if (url != null && !"".equals(url)) {
					for (String searchFile : files) {
						if (searchFile.contains(url)) {
							DropAssetFile asset = new DropAssetFile();
							asset.file = root + "/" + searchFile;
							asset.type = FileType.IMAGE;
							result.files.add(asset);
							break;
						}
					}
				}

				List<Element> products = actionRoot.getChild("Product").getChildren("PRODUCT_OFFER");
				for (Element product_offer : products) {
					List<Element> salesTerritories = product_offer.getChildren("SALES_TERRITORY");
					String territoryLabel = product_offer.getChildText("LABEL_CODE");
					String reportingId = product_offer.getChildText("REPORTING_ID");
					for (Element territory : salesTerritories) {
						String code = territory.getChildText("TERRITORY_CODE");
						DropTerritory territoryData = DropTerritory.getTerritory(code, result.territories);
						territoryData.country = code;
						territoryData.label = territoryLabel;
						String currency = territory.getChild("PRICING").getChildText("CURRENCY_CODE");
						territoryData.currency = currency;
						String price = territory.getChild("PRICING").getChildText("WHOLE_SALE_PRICE");
						try {
							territoryData.price = Float.parseFloat(price);
						} catch (Exception e) {
						}
						String startDate = territory.getChildText("SALES_START_DATE");
						SimpleDateFormat dateParse = new SimpleDateFormat("yyyyMMdd");
						try {
							territoryData.startdate = dateParse.parse(startDate);
						} catch (ParseException e) {
						}
						territoryData.reportingId = reportingId;
						territoryData.distributor = provider;
						territoryData.publisher = publisher;

					}
				}

			} else {
				String product = actionRoot.getChild("Product").getAttribute("Type").getValue();
				if (!"TRACK".equals(product)) {
					LOGGER.info("Skipping product " + product);
					return null;
				}
				String typeName = actionRoot.getChild("Product").getChildText("TypeName");
				if (!"Single".equals(typeName)) {
					LOGGER.info("Skipping type " + typeName);
					return null;
				}
				String productCode = actionRoot.getChild("Product").getChildText("ProdID");
				result.productCode = productCode;
			}
			return result;

		} catch (IOException io) {
			LOGGER.error(io.getMessage());
		} catch (JDOMException jdomex) {
			LOGGER.error(jdomex.getMessage());
		} 
		
		return null;

	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
		try {
			File manifest = new File(root + "/manifests/" + drop.name);
			tempLogFile = root + "/" + manifest.getName().replace("manifest", "log");
			logFile = root + "/logs/" + manifest.getName().replace("manifest", "log");

			FileReader reader = new FileReader(manifest);
			BufferedReader buffer = new BufferedReader(reader);
			String line;
			while ((line = buffer.readLine()) != null) {
				files.add(line);
			}
			buffer.close();
			FileWriter logWriter = new FileWriter(tempLogFile);
			for (String file : files) {
				if (file.contains(".xml")) {
					try {
						DropTrack result = loadXml(root + "/" + file);
						if (result != null) {
							tracks.put(file, result);
							LOGGER.debug("ISRC is " + result.isrc);
						}
						// Log all files for this directory
                        try {
                            File size = new File(file);
                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:MM");
                            logWriter.write(file + " " + format.format(date) + " " + size.length() + "\n");
                        } catch (Exception e) {
						    LOGGER.error("Skipping file " + file + " in logs: exception " + e.getMessage());
						}
					} catch (Exception e) {
						LOGGER.error("Not processed " + file);
					}
				}
			}
			logWriter.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tracks;

	}

	public void commit(DropData drop, boolean auto) throws IOException, InterruptedException {
		if (!auto) {
			String logFileName = drop.name.replace("manifest", "log");
			ExternalCommandThread run = new ExternalCommandThread();
			run.setCommand("mv");
			run.addParam(root + "/" + logFileName);
			run.addParam(root + "/logs/" + logFileName);
			run.run();
		}

	}

	public List<DropData> getDrops(boolean auto) {

		List<DropData> result = new ArrayList<DropData>();
		File manifests = new File(root + "/manifests");
		LOGGER.info("Checking manifests in " + root + "/manifests");
		File[] manifestFiles = manifests.listFiles();
		for (File manifest : manifestFiles) {
            LOGGER.info("Scanning manifest [{}]", manifest.getAbsolutePath());
			String logFileName = manifest.getName().replace("manifest", "log");
			File logFile = new File(root + "/logs/" + logFileName);
			if (!auto) {
				if (!logFile.exists()) {
                    addDrop(result, manifest);
				}
			} else {
				String ackName = manifest.getName().replace(".txt", ".ack");
				File ackFile = new File(root + "/manifests/"+ackName);
				if (!ackFile.exists() && !logFile.exists()) {
                    addDrop(result, manifest);
				}
			}
		}
		return result;
	}

    private void addDrop(List<DropData> result, File manifest) {
        DropData drop = new DropData();
        drop.name = manifest.getName();
        drop.date = new Date(manifest.lastModified());

        LOGGER.info("The drop was found: [{}]", drop.name);
        result.add(drop);
    }

}