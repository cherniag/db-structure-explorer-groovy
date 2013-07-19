package mobi.nowtechnologies.ingestors;

import mobi.nowtechnologies.domain.AssetFile.FileType;
import mobi.nowtechnologies.ingestors.DropTrack.Type;
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

public abstract class DDEXParser extends IParser {

	protected static final Log LOG = LogFactory.getLog(DDEXParser.class);

	@SuppressWarnings("unchecked")
	protected Map<String, DropTrack> loadXml(String file) {

		Map<String, DropTrack> result = new HashMap<String, DropTrack>();
		SAXBuilder builder = new SAXBuilder();
		LOG.info("Loading " + file);
		File xmlFile = new File(file);
		String fileRoot = xmlFile.getParent();
		Map<String, List<DropAssetFile>> files = new HashMap<String, List<DropAssetFile>>();
		Map<String, DropTrack> resourceDetails = new HashMap<String, DropTrack>();

		try {

			String albumTitle = null;
			String upc = null;
			String grid = null;

			Document document = (Document) builder.build(xmlFile);
			Element rootNode = document.getRootElement();

			// Get distributor
			String distributor = null;
			Element onbehalf = rootNode.getChild("MessageHeader").getChild("SentOnBehalfOf");
			if (onbehalf != null) {
				distributor = onbehalf.getChild("PartyName").getChildText("FullName");
			} else {
				distributor = rootNode.getChild("MessageHeader").getChild("MessageSender").getChild("PartyName").getChildText("FullName");
			}
			String updateIndicator = rootNode.getChildText("UpdateIndicator");
			String action = "UpdateMessage".equals(updateIndicator) ? "UPDATE" : "INSERT";

			// Parse media files
			List list = rootNode.getChild("ResourceList").getChildren("SoundRecording");

			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);

				Element details = node.getChild("SoundRecordingDetailsByTerritory");
				String reference = node.getChildText("ResourceReference");
				DropTrack resourceDetail = null;
				resourceDetail = new DropTrack();
				String isrc = node.getChild("SoundRecordingId").getChildText("ISRC");
				resourceDetail.isrc = isrc;
                String parentalWarningType = details.getChildText("ParentalWarningType");
                resourceDetail.explicit = "Explicit".equals(parentalWarningType);
				resourceDetails.put(reference, resourceDetail);
				if (details.getChild("PLine") != null) {
					resourceDetail.copyright = details.getChild("PLine").getChildText("PLineText");
					resourceDetail.year = details.getChild("PLine").getChildText("Year");
				}
				List<Element> techDetails = details.getChildren("TechnicalSoundRecordingDetails");
				for (Element techDetail : techDetails) {
					String preview = techDetail.getChildText("IsPreview");
					String fileName = techDetail.getChild("File").getChildText("FileName");
					DropAssetFile assetFile = new DropAssetFile();
					assetFile.file = getAssetFile(fileRoot, fileName);
					assetFile.isrc = resourceDetail.isrc;
					if (preview == null || "false".equals(preview)) {
						String codecType = techDetail.getChildText("AudioCodecType");
						if (codecType == null
								|| "MP3".equals(codecType)
								|| ("UserDefined".equals(codecType) && "MP3".equals(techDetail.getChild("AudioCodecType").getAttributeValue(
										"UserDefinedValue")))) {
							assetFile.type = FileType.DOWNLOAD;
						} else {
							assetFile.type = FileType.MOBILE;
						}

					} else {
						assetFile.type = FileType.PREVIEW;
					}
					List<DropAssetFile> resourceFiles = files.get(node.getChildText("ResourceReference"));
					if (resourceFiles == null) {
						resourceFiles = new ArrayList<DropAssetFile>();
						files.put(node.getChildText("ResourceReference"), resourceFiles);
					}
					resourceFiles.add(assetFile);

					// Get Hash
					Element hash = techDetail.getChild("File").getChild("HashSum");
					if (hash != null) {
						if ("MD5".equals(hash.getChildText("HashSumAlgorithmType"))) {
							assetFile.md5 = hash.getChildText("HashSum");
						}
					}
				}
			}

			DropAssetFile imageFile = null;
			// Parse images
			list = rootNode.getChild("ResourceList").getChildren("Image");
			for (int i = 0; i < list.size(); i++) {
				Element node = (Element) list.get(i);
				Element details = node.getChild("ImageDetailsByTerritory");
				Element techDetail = details.getChild("TechnicalImageDetails");
				if (techDetail != null) {
					imageFile = new DropAssetFile();
					String fileName = techDetail.getChild("File").getChildText("FileName");
					imageFile.file = getAssetFile(fileRoot, fileName);
					;
					imageFile.type = FileType.IMAGE;
					// Get Hash
					Element hash = techDetail.getChild("File").getChild("HashSum");
					if (hash != null) {
						if ("MD5".equals(hash.getChildText("HashSumAlgorithmType"))) {
							imageFile.md5 = hash.getChildText("HashSum");
						}
					}

				}

			}

			// Parse deals
			Map<String, Map<String, DropTerritory>> deals = new HashMap<String, Map<String, DropTerritory>>();
			list = rootNode.getChild("DealList").getChildren("ReleaseDeal");
			for (int i = 0; i < list.size(); i++) {
				Element releaseDeal = (Element) list.get(i);
				List<Element> references = releaseDeal.getChildren("DealReleaseReference");

				Map<String, DropTerritory> dealsMap = new HashMap<String, DropTerritory>();

				/*
				 * for (Element reference : references) {
				 * LOG.info("Loading deal reference " + reference.getText());
				 * deals.put(reference.getText(), dealsMap); }
				 */

				List<Element> dealNodes = releaseDeal.getChildren("Deal");
				for (Element dealNode : dealNodes) {
					Element dealTerms = dealNode.getChild("DealTerms");
					boolean validUseType = false;
					Element usage = dealTerms.getChild("Usage");
					if (usage != null) {
						List<Element> useTypes = usage.getChildren("UseType");
						for (Element useType : useTypes) {
							if ("AsPerContract".equals(useType.getText()) || "Download".equals(useType.getText())
									|| "PermanentDownload".equals(useType.getText())) {
								LOG.info("Found valid usage " + useType.getText());
								validUseType = true;
								break;
							}
						}
					}
					String takeDown = dealTerms.getChildText("TakeDown");
					if (takeDown != null || validUseType) {
						List<Element> countriesNodes = dealTerms.getChildren("TerritoryCode");
						String startDate = dealTerms.getChild("ValidityPeriod").getChildText("StartDate");
						Date dealStartDate = null;
						SimpleDateFormat dateparse = new SimpleDateFormat("yyyy-MM-dd");
						try {
							dealStartDate = dateparse.parse(startDate);
						} catch (ParseException e) {
						}

						Element priceInfo = dealTerms.getChild("PriceInformation");
						String price = null;
						String currency = null;
						String priceType = null;
						if (priceInfo != null) {
							Element wholeSaleprice = priceInfo.getChild("WholesalePricePerUnit");
							if (wholeSaleprice != null) {
								currency = wholeSaleprice.getAttributeValue("CurrencyCode");
								price = wholeSaleprice.getText();
							}
							priceType = priceInfo.getChildText("PriceType");
							if (priceType == null) {
								priceType = priceInfo.getChildText("PriceRangeType");
							}
						}

						for (Element country : countriesNodes) {
							LOG.info("Deal for country " + country.getText());
							DropTerritory territory = (DropTerritory) dealsMap.get(country.getText());
							if (territory == null) {
								territory = new DropTerritory();
								dealsMap.put(country.getText(), territory);
							}
							territory.country = country.getText();
							territory.takeDown = (takeDown != null);
							territory.startdate = dealStartDate;
							territory.dealReference = dealNode.getChildText("DealReference");
							try {
								if (price != null)
									territory.price = Float.valueOf(price);
							} catch (NumberFormatException e) {
							}
							territory.currency = currency;
							territory.priceCode = priceType;
						}

					}
				}
				for (Element reference : references) {
					LOG.info("Loading deal reference " + reference.getText());
					Map<String, DropTerritory> ExistingDealsMap = deals.get(reference.getText());
					if (ExistingDealsMap == null)
						deals.put(reference.getText(), dealsMap);
					else
						ExistingDealsMap.putAll(dealsMap);
				}

			}

			// Parse releases
			list = rootNode.getChild("ReleaseList").getChildren("Release");
			Element albumElement = null;

			for (int i = 0; i < list.size(); i++) {
				DropTrack track = new DropTrack();
				Element release = (Element) list.get(i);
				String type = release.getChildText("ReleaseType");
				LOG.info("release type " + type);
				if ("UPDATE".equals(action)) {
					track.type = Type.UPDATE;
				} else if ("INSERT".equals(action)) {
					track.type = Type.INSERT;
				} else if ("DELETE".equals(action)) {
					track.type = Type.DELETE;
				}

				// Add files
				String resourceRef = release.getChild("ReleaseResourceReferenceList").getChildText("ReleaseResourceReference");
				LOG.info("Resource reference " + resourceRef);
				if (files.get(resourceRef) != null)
					track.files.addAll(files.get(resourceRef));
				if (imageFile != null)
					track.files.add(imageFile);

				getIds(release, track, track.files);
                DropTrack resourceDetail = resourceDetails.get(resourceRef);
				if (track.isrc == null || "".equals(track.isrc)) {
					if (release.getChild("ReleaseResourceReferenceList").getChildren("ReleaseResourceReference").size() == 1
							&& resourceDetail != null) {
						LOG.info("Getting ISRC from resource " + resourceRef);
						track.isrc = resourceDetail.isrc;
					}
				}

				boolean isAlbum = checkAlbum(type, track);

				if (!isAlbum) {

					Element pline = release.getChild("PLine");
					if (pline != null) {
						track.year = pline.getChildText("Year");
						track.copyright = pline.getChildText("PLineText");
					} else if (resourceDetail != null) {
						track.year = resourceDetail.year;
						track.copyright = resourceDetail.copyright;

					}

                    if (resourceDetail != null) {
                        track.explicit = resourceDetail.explicit;
                    }

					Element details = release.getChild("ReleaseDetailsByTerritory");
					String title = release.getChild("ReferenceTitle").getChildText("TitleText");
					track.title = title;
					track.subTitle = release.getChild("ReferenceTitle").getChildText("SubTitle");
					
					//Get all sub titles from ReleaseDetailsByTerritory
					Element detailsTitle = details.getChild("Title");
					List subTitles = detailsTitle.getChildren("SubTitle");
					if (subTitles != null) {
						String fullSubTitle = new String();
						for (int si = 0; si < subTitles.size(); si++) {
							Element subTitle = (Element) subTitles.get(si);
							fullSubTitle += subTitle.getText();
							if (si < subTitles.size() -1)
								fullSubTitle += " / ";
						}
						if (!"".equals(fullSubTitle))
							track.subTitle = fullSubTitle;
					}
					String artist = details.getChildText("DisplayArtistName");
					if (artist == null || "".equals(artist)) {
						// Try another format (used by CI)
						List<Element> displayArtists = details.getChildren("DisplayArtist");
						if (displayArtists != null && displayArtists.size() > 0) {
							for (Element displayArtist : displayArtists) {
								if (displayArtist.getChildText("ArtistRole").equals("MainArtist")) {
									artist = displayArtist.getChild("PartyName").getChildText("FullName");
								}
							}
							if (artist == null || "".equals(artist)) {
								// Default to first one.....
								artist = displayArtists.get(0).getChild("PartyName").getChildText("FullName");
							}

						}
					}
					track.artist = artist;
					String label = details.getChildText("LabelName");
					track.label = label;
					XMLOutputter outputter = new XMLOutputter();
					track.xml = outputter.outputString(release);

					// Add territory info (need to add deals....)
					List<Element> territoriesNodes = release.getChildren("ReleaseDetailsByTerritory");
					for (Element territory : territoriesNodes) {
						String code = territory.getChildText("TerritoryCode");
						String releaseReference = release.getChildText("ReleaseReference");
						Element genre = territory.getChild("Genre");
						if (genre != null)
							track.genre = genre.getChildText("GenreText");

						Map<String, DropTerritory> deal = deals.get(releaseReference);
						LOG.info("Deal for release ref  " + releaseReference + " " + deal);

						if (deal == null) {
							continue;
						}
						if ("Worldwide".equals(code)) {
							Set<String> countries = deal.keySet();
							Iterator<String> it = countries.iterator();
							while (it.hasNext()) {
								String country = it.next();
								LOG.info("Adding country " + country);
								DropTerritory territoryData = DropTerritory.getTerritory(country, track.territories);
								DropTerritory dealTerritory = deal.get(country);
								territoryData.country = dealTerritory.country;
								territoryData.takeDown = dealTerritory.takeDown;
								territoryData.distributor = distributor;
								String territoryLabel = territory.getChildText("LabelName");
								territoryData.label = territoryLabel;
								territoryData.reportingId = track.isrc;
								territoryData.startdate = dealTerritory.startdate;
								territoryData.price = dealTerritory.price;
								territoryData.priceCode = dealTerritory.priceCode;
								territoryData.currency = dealTerritory.currency;
								territoryData.dealReference = dealTerritory.dealReference;
							}
						} else {
							LOG.info("Adding country " + code);

							DropTerritory dealTerritory = deal.get(code);
							DropTerritory territoryData = DropTerritory.getTerritory(code, track.territories);
							territoryData.country = dealTerritory.country;
							territoryData.takeDown = dealTerritory.takeDown;
							territoryData.distributor = distributor;
							String territoryLabel = territory.getChildText("LabelName");
							territoryData.label = territoryLabel;
							territoryData.reportingId = track.isrc;
							territoryData.startdate = dealTerritory.startdate;
							territoryData.price = dealTerritory.price;
							territoryData.priceCode = dealTerritory.priceCode;
							territoryData.currency = dealTerritory.currency;
							territoryData.dealReference = dealTerritory.dealReference;
						}

					}

					result.put((String) track.isrc + track.productCode + getClass(), track);
				} else {
					albumTitle = release.getChild("ReferenceTitle").getChildText("TitleText");
					Element releaseId = release.getChild("ReleaseId");
					if (releaseId != null) {
						upc = releaseId.getChildText("ICPN");
						grid = releaseId.getChildText("GRid");
					}
					LOG.info("album " + albumTitle);
				}
			}

			// Add album title to all tracks
			if (albumTitle != null)
				for (DropTrack track : result.values()) {
					track.album = albumTitle;
					setUpc(track, upc);
					setGRid(track, grid);
				}
			return result;

		} catch (IOException io) {
			LOG.error("Exception " + io.getMessage());
		} catch (JDOMException jdomex) {
			LOG.error("Exception " + jdomex.getMessage());
		}
		return null;

	}

	protected String getAssetFile(String root, String file) {
		return root + "/resources/" + file;
	}

	public abstract void getIds(Element release, DropTrack track, List<DropAssetFile> files);

	public void setUpc(DropTrack track, String upc) {
	}
	public void setGRid(DropTrack track, String GRid) {
	}

	public boolean checkAlbum(String type, DropTrack track) {
		if ("Single".equals(type) || "Album".equals(type) || "SingleResourceRelease".equals(type)) {
			LOG.info("Album for " + type);
			return true;
		}
		LOG.info("Track for " + type);
		return false;

	}

}