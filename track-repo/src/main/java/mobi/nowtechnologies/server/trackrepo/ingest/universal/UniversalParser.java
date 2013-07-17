package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UniversalParser extends IParser {
	
	protected static final Log LOG = LogFactory.getLog(UniversalParser.class);


	private String root;

	public UniversalParser(String root) {
        this.root = root;
		LOG.info("Universal parser loading from " + root);
	}

	protected Map<String, DropTrack> loadXml(String drop, String code, Map<String, List<DropAssetFile>> fulfillmentFiles) {

		Map<String, DropTrack> result = new HashMap<String, DropTrack>();
		SAXBuilder builder = new SAXBuilder();
		builder.setEntityResolver(new DtdLoader());

		/*
		 * SAXBuilder builder = new SAXBuilder(false);
		 * builder.setValidation(false);
		 * builder.setFeature("http://xml.org/sax/features/validation", false);
		 * builder.setFeature(
		 * "http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
		 * false);builder.setFeature(
		 * "http://apache.org/xml/features/nonvalidating/load-external-dtd",
		 * false); builder.setProperty(
		 * "http://apache.org/xml/properties/schema/external-schemaLocation",
		 * "http://www.digiplug.com/dsc/umgistd-1_4 umgistd-1_4");
		 */
		LOG.info("Scaning " + root + "/" + code + "_" + drop + " ");
		File productDir = new File(root + "/" + code + "_" + drop);
		File[] files = productDir.listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".xml")) {
				try {

					LOG.debug("Loading " + file.getPath());
					Document document = (Document) builder.build(file);
					Element product = document.getRootElement();
					String country = product.getChildText("territory");
					String provider = product.getChildText("prd_label_name");
					String releaseDate = product.getChildText("release_date");
					String year = null;
					SimpleDateFormat dateparse = new SimpleDateFormat("dd-MMM-yyyy");
					Date startDate = null;
					try {
						if (releaseDate != null) {
							startDate = dateparse.parse(releaseDate);
							SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
							year = yearFormat.format(startDate);
						}

					} catch (ParseException e) {
					}
					LOG.info("Tracks " + product.getChild("tracks"));

					List<Element> tracks = product.getChild("tracks").getChildren("track");
					for (Element track : tracks) {
						XMLOutputter outputter = new XMLOutputter();
						String isrc = track.getAttributeValue("isrc");
						DropTrack data = result.get(isrc);
						if (data == null) {
							data = new DropTrack();
							result.put(isrc, data);
						}
						data.type = Type.INSERT;
						data.productCode = code;
						data.productId = code;
						data.physicalProductId = code;
						data.copyright = product.getChildText("c_line");
						data.genre = product.getChildText("genre");
						data.year = year;
						data.isrc = isrc;
						String title = track.getChildText("track_title");
						data.title = title;
						data.subTitle = track.getChildText("track_version_title");
						List<Element> artists = track.getChild("track_contributors").getChildren("artist_name");
						boolean firstArtist = true;
						String artist = "";
						for (Element artistElement : artists) {
							if (firstArtist) {
								firstArtist = false;
							} else {
								artist += ", ";
							}
							artist += artistElement.getText();
						}
						// String artist =
						// track.getChild("track_contributors").getChildText("artist_name");
						data.artist = artist;

						Element trackPricing = track.getChild("track_pricing");

						DropTerritory territoryData = DropTerritory.getTerritory(country, data.territories);
						territoryData.country = country;
						String territoryLabel = track.getChildText("track_label");
						territoryData.label = territoryLabel;
						territoryData.reportingId = isrc;
						territoryData.distributor = provider;
						territoryData.startdate = startDate;
						territoryData.priceCode = trackPricing.getChildText("current_price_code");
						/*
						 * territoryData.currency =
						 * trackPricing.getChildText(""); String price =
						 * trackPricing.getChildText(""); try {
						 * territoryData.price = Float.parseFloat(price); }
						 * catch (Exception e) {
						 * 
						 * }
						 */

						if (fulfillmentFiles.get(isrc) != null)
							data.files.addAll(fulfillmentFiles.get(isrc));

						data.xml = outputter.outputString(track);
					}
					// result.put("xml", rootNode));
					/*
					 * terms <terms>
					 * <download_singles_restriction>N</download_singles_restriction
					 * > <terms_of_use>
					 * 
					 * <a_la_carte_online_permanent_download>N</
					 * a_la_carte_online_permanent_download>
					 * <a_la_carte_mobile_permanent_download
					 * >Y</a_la_carte_mobile_permanent_download>
					 * <subscription_mobile_time_limited_download
					 * >N</subscription_mobile_time_limited_download>
					 */
					return result;

				} catch (IOException io) {
					LOG.error(io.getMessage());
				} catch (JDOMException jdomex) {
					LOG.error(jdomex.getMessage());
				}
			}

		}

		return null;

	}

	public Map<String, DropTrack> ingest(DropData drop) {

		Map<String, DropTrack> result = new HashMap<String, DropTrack>();
		try {
			File fulfillment = new File(root + "/Delivery_Messages/" + "fulfillment_" + drop.name + ".xml");
			SAXBuilder builder = new SAXBuilder();
			builder.setEntityResolver(new DtdLoader());
			LOG.info("Loading " + fulfillment.getPath());

			try {
				Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();
				List<String> codes = new ArrayList<String>();
				Document document = (Document) builder.build(fulfillment);
				Element rootNode = document.getRootElement();
				List<Element> products = rootNode.getChild("products").getChildren("product");
				for (Element product : products) {
					DropAssetFile imageFile = null;
					String code = product.getChildText("upc");
					codes.add(code);
					List<Element> assets = product.getChild("assets").getChildren("asset");
					for (Element asset : assets) {
						String type = asset.getAttributeValue("type");
						Element file = asset.getChild("files").getChild("file");
						String fileName = file.getChildText("file_name");
						String subType = asset.getChildText("subtype");
						String md5 = file.getChildText("checksum");
						if ("Images".equals(type) && "Cover Art".equals(subType)) {
							imageFile = new DropAssetFile();
							imageFile.file = root + "/" + code + "_" + drop.name + "/" + fileName;
							imageFile.md5 = md5;
							imageFile.type = AssetFile.FileType.IMAGE;
						}
					}
					List<Element> tracks = product.getChild("assets").getChildren("track");
					for (Element track : tracks) {
						String isrc = track.getChildText("isrc");
						List<DropAssetFile> assetFiles = fulfillmentFiles.get(isrc);
						if (assetFiles == null) {
							assetFiles = new ArrayList<DropAssetFile>();
							fulfillmentFiles.put(isrc, assetFiles);
						}
						if (imageFile != null)
							assetFiles.add(imageFile);
						List<Element> files = track.getChild("files").getChildren("file");
						for (Element file : files) {
							String type = file.getChildText("file_type");
							Element excerpt = file.getChild("excerpt");
							String fileName = file.getChildText("file_name");
							String md5 = file.getChildText("checksum");
							DropAssetFile assetFile = new DropAssetFile();
							assetFile.file = root + "/" + code + "_" + drop.name + "/" + fileName;
							assetFile.md5 = md5;
							if ("mp3".equalsIgnoreCase(type)) {
								if (excerpt == null) {
									assetFile.type = AssetFile.FileType.DOWNLOAD;
								} else {
								}
							} else if ("MP4".equalsIgnoreCase(type)) {
								if (excerpt == null) {
									assetFile.type = AssetFile.FileType.MOBILE;
								} else {
									assetFile.type = AssetFile.FileType.PREVIEW;
								}
							}
							assetFiles.add(assetFile);
						}

					}
				}

				for (String code : codes) {
					result.putAll(loadXml(drop.name, code, fulfillmentFiles));
				}

			} catch (IOException io) {
				LOG.error(io.getMessage());
			} catch (JDOMException jdomex) {
				LOG.error(jdomex.getMessage());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	public void commit(DropData drop, boolean auto) {

		if (!auto) {
			File commitFile = new File(root + "/Delivery_Messages/" + drop.name + ".ack");
			try {
				commitFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File commitFile = new File(root + "/Delivery_Messages/auto_" + drop.name + ".ack");
		try {
			commitFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<DropData> getDrops(boolean auto) {

		List<DropData> result = new ArrayList<DropData>();
		File deliveries = new File(root + "/Delivery_Messages");
		LOG.info("Checking manifests in " + root + "/Delivery_Messages");
		File[] fulfillmentFiles = deliveries.listFiles();
		for (File file : fulfillmentFiles) {
			if (file.getName().startsWith("delivery") && file.getName().endsWith(".xml")) {
				String order = file.getName().substring(file.getName().indexOf('_') + 1, file.getName().lastIndexOf('.'));
				File ackManual = new File(root + "/Delivery_Messages/" + order + ".ack");
				if (!auto) {
					if (!ackManual.exists()) {
						DropData drop = new DropData();
						drop.name = order;
						drop.date = new Date(file.lastModified());
						result.add(drop);
					}
				} else {
					File ack = new File(root + "/Delivery_Messages/auto_" + order + ".ack");
					if (!ack.exists() && !ackManual.exists()) {
						DropData drop = new DropData();
						drop.name = order;
						drop.date = new Date(file.lastModified());
						result.add(drop);
					}
				}
			}
		}
		return result;
	}

}