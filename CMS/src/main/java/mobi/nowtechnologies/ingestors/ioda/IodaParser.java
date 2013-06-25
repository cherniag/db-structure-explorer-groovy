package mobi.nowtechnologies.ingestors.ioda;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.domain.AssetFile.FileType;
import mobi.nowtechnologies.ingestors.DDEXParser;
import mobi.nowtechnologies.ingestors.DebugErrorHandler;
import mobi.nowtechnologies.ingestors.DropAssetFile;
import mobi.nowtechnologies.ingestors.DropData;
import mobi.nowtechnologies.ingestors.DropTerritory;
import mobi.nowtechnologies.ingestors.DropTrack;
import mobi.nowtechnologies.ingestors.IParser;
import mobi.nowtechnologies.ingestors.DropTrack.Type;
import mobi.nowtechnologies.util.Property;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import antlr.NameSpace;

public class IodaParser extends IParser {
	protected static final Log LOG = LogFactory.getLog(IodaParser.class);

	private ArrayList<String> files = new ArrayList<String>();;
	private String root;

	public IodaParser() {
		root = Property.getInstance().getStringValue("ingest.ioda.root");
		LOG.info("IODA parser loading from " + root);
	}

	protected Map<String, DropTrack> loadXml(String file) {

		Map<String, DropTrack> result = new HashMap<String, DropTrack>();

		SAXBuilder builder = new SAXBuilder();
		LOG.info("Loading " + file);
		File xmlFile = new File(file);

		try {
			// builder.setFeature(
			// "http://apache.org/xml/features/nonvalidating/load-external-dtd",
			// true);
			// builder.setFeature("http://apache.org/xml/features/validation/schema",
			// false);
			// builder.setFeature("http://apache.org/xml/features/validation/schema-full-checking",
			// false);

			builder.setValidation(true);
			builder.setErrorHandler(new DebugErrorHandler());
			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();

			XMLOutputter outputter = new XMLOutputter();
			List<Element> list = rootNode.getChildren();
			for (Element el : list) {
				if (el.getName().equals("export_action")) {
					LOG.debug("TYPE " + el.getText());
				}
			}
			Type type = null;
			Namespace space = Namespace.getNamespace("http://www.iodalliance.com/schema/ioda_standard_export_v2.2.xsd");
			// Parse media files
			String typeStr = rootNode.getChildText("export_action", space);
			LOG.debug("type is " + typeStr);

			if ("update".equals(typeStr)) {
				type = Type.UPDATE;
			} else if ("insert".equals(typeStr)) {
				type = Type.INSERT;
			} else if ("delete".equals(typeStr)) {
				type = Type.DELETE;
			}

			String year = null;
			String releasedateStr = rootNode.getChildText("original_release_date", space);
			SimpleDateFormat dateparse = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
			try {
				Date releasedate = dateparse.parse(releasedateStr);
				year = yearFormat.format(releasedate);
			} catch (ParseException e) {
			}

			String genre = rootNode.getChild("primary_style", space).getChildText("ioda_style_name", space);

			// if (type == Type.INSERT || type == Type.UPDATE) {
			String label = rootNode.getChild("label", space).getChildText("label_name", space);
			LOG.debug("Label " + label);
			String artist = rootNode.getChildText("display_artist_name", space);
			String album = rootNode.getChildText("release_name", space);
			List<DropTerritory> territoriesList = new ArrayList<DropTerritory>();
			List<Element> territories = rootNode.getChildren("territories", space);
			for (Element territory : territories) {
				String code = territory.getChildText("country_code", space);
				DropTerritory territoryData = DropTerritory.getTerritory(code, territoriesList);
				territoryData.country = code;
				territoryData.label = label;
				territoryData.distributor = "IODA";
				territoryData.priceCode = territory.getChildText("price_tier", space);
				String startdate = territory.getChildText("publish_date", space);
				try {
					territoryData.startdate = dateparse.parse(startdate);
				} catch (ParseException e) {
				}

			}
			Element image = rootNode.getChild("image", space);

			DropAssetFile imageFile = null;
			if (image != null) {
				imageFile = new DropAssetFile();
				imageFile.file = xmlFile.getParent() + "/" + image.getChildText("file_name", space);
				imageFile.md5 = image.getChildText("md5_checksum", space);
				imageFile.type = FileType.IMAGE;
			}
			List<Element> tracks = rootNode.getChildren("track", space);
			for (Element track : tracks) {
				DropTrack dropTrack = new DropTrack();
				dropTrack.productCode = track.getChildText("ioda_track_id", space);
				dropTrack.productId = dropTrack.productCode;
				dropTrack.label = label;
				dropTrack.type = type;
				dropTrack.isrc = track.getChildText("isrc", space);
				dropTrack.artist = track.getChildText("display_artist_name", space);
				dropTrack.title = track.getChildText("track_name", space);
				dropTrack.genre = genre;
				dropTrack.copyright = track.getChildText("p_line", space);
				dropTrack.album = album;
				dropTrack.year = year;
				dropTrack.territories = DropTerritory.copyList(territoriesList);
				for (DropTerritory territory : dropTrack.territories) {
					territory.reportingId = dropTrack.productId;
				}
				dropTrack.xml = outputter.outputString(rootNode);

				Element media = track.getChild("media_file", space);
				if (media != null) {
					DropAssetFile mediaFile = new DropAssetFile();
					if ("mp3".equals(media.getChildText("format", space))) {
						mediaFile.file = xmlFile.getParent() + "/" + media.getChildText("file_name", space);
						mediaFile.md5 = media.getChildText("md5_checksum", space);
						mediaFile.type = FileType.DOWNLOAD;
					}
					dropTrack.files.add(mediaFile);
				}
				if (imageFile != null)
					dropTrack.files.add(imageFile);
				result.put(dropTrack.isrc+dropTrack.productCode+getClass(), dropTrack);

			}

			// }

			return result;

		} catch (IOException io) {
			LOG.error("Ingest failed "+io.getMessage());
		} catch (JDOMException jdomex) {
			LOG.error("Ingest failed "+jdomex.getMessage());
		} 
		return null;

	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> tracks = new HashMap<String, DropTrack>();
		try {
			File dropDir = new File(drop.name);
			tracks = loadXml(drop.name + "/" + dropDir.getName() + ".xml");

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
		boolean processed = false;
		boolean valid = false;
		for (File file : content) {
			if (isDirectory(file)) {
				result.addAll(getDrops(file, auto));
			} else if ("ingest.ack".equals(file.getName())) {
				processed = true;
			} else if (auto && "autoingest.ack".equals(file.getName())) {
				processed = true;
			}  else if ("dir.complete".equals(file.getName())) {
				valid = true;

			}
		}
		if (valid && !processed) {
			LOG.debug("Adding " + folder.getAbsolutePath() + " to drops");
			DropData drop = new DropData();
			drop.name = folder.getAbsolutePath();
			drop.date = new Date(folder.lastModified());
			result.add(drop);
		}
		return result;
	}

}