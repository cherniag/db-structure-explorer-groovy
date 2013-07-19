package mobi.nowtechnologies.service;

import mobi.nowtechnologies.domain.*;
import mobi.nowtechnologies.ingestors.*;
import mobi.nowtechnologies.ingestors.DropTrack.Type;
import mobi.nowtechnologies.ingestors.IParserFactory.Ingestors;
import mobi.nowtechnologies.mvc.controller.DropsData;
import mobi.nowtechnologies.mvc.controller.DropsData.Drop;
import mobi.nowtechnologies.mvc.controller.IngestData;
import mobi.nowtechnologies.mvc.controller.IngestWizardData;
import mobi.nowtechnologies.service.dao.IngestionLogDAO;
import mobi.nowtechnologies.service.dao.TrackDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class IngestService {

	protected static final Log LOG = LogFactory.getLog(IngestService.class);

	private TrackDAO trackDAO;
	private IngestionLogDAO ingestionLogDAO;

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	public void setIngestionLogDAO(IngestionLogDAO ingestionLogDAO) {
		this.ingestionLogDAO = ingestionLogDAO;
	}

	protected List<DropsData.Drop> getSelectedDrop(IngestWizardData data) {
		List<DropsData.Drop> selected = new ArrayList<DropsData.Drop>();
		DropsData drops = data.getDropdata();
		for (Drop drop : drops.getDrops()) {
			if (drop.isSelected()) {
				selected.add(drop);
			}
		}
		return selected;
	}

	public Object prepareData(String parserName) throws Exception {

		LOG.debug("formBackingObject " + parserName);
		IngestWizardData wizardData = new IngestWizardData();

		if (parserName != null) {

			IParserFactory.Ingestors ingestor = IParserFactory.Ingestors.valueOf(parserName);
			IParser parser = IParserFactory.getParser(ingestor);
			// wizardData.setParser(parser);
			// wizardData.setIngestor(parserName);
			DropsData drops = new DropsData();
			wizardData.setDropdata(drops);
			drops.setDrops(new ArrayList<DropsData.Drop>());

			List<DropData> parserDrops = parser.getDrops(false);
			if (parserDrops != null && parserDrops.size() > 0) {
				for (DropData drop : parserDrops) {
					DropsData.Drop data = drops.new Drop();
					data.setName(drop.name);
					data.setIngestor(ingestor);
					data.setParser(parser);
					data.setDrop(drop);
					drops.getDrops().add(data);
				}
			}
		} else {
			DropsData drops = new DropsData();
			drops.setDrops(new ArrayList<DropsData.Drop>());
			wizardData.setDropdata(drops);
			for (IParserFactory.Ingestors ingestor : IParserFactory.Ingestors.values()) {
				LOG.info("Getting drops for " + ingestor);
				IParser parser = IParserFactory.getParser(ingestor);

				List<DropData> parserDrops = parser.getDrops(false);
				if (parserDrops != null && parserDrops.size() > 0) {
					for (DropData drop : parserDrops) {
						DropsData.Drop data = drops.new Drop();
						data.setName(drop.name);
						data.setParser(parser);
						data.setIngestor(ingestor);
						data.setDrop(drop);
						drops.getDrops().add(data);
					}
				}

			}
		}
		return wizardData;
	}

	public boolean processFinish(IngestWizardData command) throws Exception {
		LOG.debug("INGEST processFinish");

		for (Drop drop : ((IngestWizardData) command).getDropdata().getDrops()) {

			if (!drop.isSelected()) {
				LOG.debug("Skipping not selected  " + drop.getName());
				continue;
			}
			LOG.info("Loading " + drop.getName() + " with " + drop.getParser().getClass());

			IParser parser = drop.getParser();

			processDrop(drop.getIngestor(), parser, drop.getDrop(), true);
		}
		return true;

	}

	public void processDrop(IParserFactory.Ingestors ingestor, IParser parser, DropData drop, boolean updateFiles) {
		LOG.debug("INGEST processFinish");

		LOG.info("Loading " + drop.getName() + " with " + parser.getClass());

		Map<String, DropTrack> tracks = parser.ingest(drop);
		if (tracks == null || tracks.isEmpty()) {
			commit(ingestor, parser, drop, null, false, false, "No tracks");
			// parser.commit(drop.getDrop());
			return;
		}

		Collection<DropTrack> list = tracks.values();
		Iterator<DropTrack> it = list.iterator();
		while (it.hasNext()) {
			DropTrack value = it.next();
			if (value == null) {
				LOG.info("Null track value");
				continue;
			}
			LOG.info("Ingesting " + value.isrc);

			if (value.type == Type.INSERT || value.type == Type.UPDATE) {
				LOG.info("Inserting " + value.isrc);

				Track track = trackDAO.getByKey((String) value.isrc, (String) value.productCode, IParserFactory.getName(ingestor));
				if (track == null) { // Try to find old keys for Fuga
					track = trackDAO.getByKey((String) value.isrc, (String) value.isrc, IParserFactory.getName(ingestor));
				}
				if (track == null) {
					if (value.type == Type.UPDATE) {
						LOG.info("Not updating " + value.isrc + " " + value.productCode + " " + ingestor.toString());
						continue; // Nothing to update
					}
					if (value.files == null || value.files.size() == 0) {
						LOG.info("Not inserting with no tracks " + value.isrc + " " + value.productCode + " " + ingestor.toString());
						continue;
					}
					track = new Track();
					track.setIngestionDate(new Date());
					// track.setIngestor(getIngestionProcess());
				} else {
					track.setIngestionUpdateDate(new Date());
				}
				track.setTitle((String) value.title);
				track.setSubTitle((String) value.subTitle);
				track.setArtist((String) value.artist);
				track.setISRC((String) value.isrc);
				track.setProductId((String) value.productId);
				track.setProductCode((String) value.productCode);
				track.setGenre((String) value.genre);
				track.setCopyright((String) value.copyright);
				track.setYear((String) value.year);
				track.setAlbum((String) value.album);
				track.setIngestor(IParserFactory.getName(ingestor));
				track.setXml(value.xml.getBytes());
				track.setInfo(value.info);
				track.setLicensed(value.licensed);
				track.setExplicit(value.explicit);

				Set<AssetFile> files = track.getFiles();
				if (files == null) {
					files = new HashSet<AssetFile>();
					track.setFiles(files);
				}

				List<DropAssetFile> dropFiles = value.files;
				LOG.info("Adding files " + dropFiles.size());
				for (DropAssetFile file : dropFiles) {
					if (!AddOrUpdateFile(files, file, updateFiles)) {
						commit(ingestor, parser, drop, list, false, true, "Drop is updating asset files: to be processed manually");
						return;
					}
				}

				Set<Territory> territories = track.getTerritories();
				if (territories == null) {
					territories = new HashSet<Territory>();
					track.setTerritories(territories);
				}
				boolean takeDown = false;
				for (DropTerritory territoryData : value.territories) {
					takeDown |= AddOrUpdateTerritory(territories, territoryData);
				}

				trackDAO.persist(track);
			} else if (value.type == Type.DELETE) {
				LOG.info("DELETE " + value.productId);
				Track track = trackDAO.getByProductCode((String) value.productId);
				if (track != null)
					trackDAO.delete(track);

			}
		}
		commit(ingestor, parser, drop, list, true, false, "");

	}

	public void processAllDrops() throws Exception {

		for (IParserFactory.Ingestors ingestor : IParserFactory.Ingestors.values()) {
			LOG.info("Getting drops for " + ingestor);
			IParser parser = IParserFactory.getParser(ingestor);

			List<DropData> parserDrops = parser.getDrops(true);
			if (parserDrops != null && parserDrops.size() > 0) {
				for (DropData drop : parserDrops) {
					processDrop(ingestor, parser, drop, false);
				}
			}

		}
	}

	public void postProcessPage(IngestWizardData command, int page) throws Exception {
		LOG.debug("POST PROCESS " + page);
		if (page == 0) {
			for (Drop drop : command.getDropdata().getDrops()) {

				IngestData data = new IngestData();
				if (!drop.isSelected()) {
					continue;
				}

				drop.setIngestdata(data);
				IParser parser = drop.getParser();

				data.setData(new ArrayList<IngestData.Track>());

				data.setDrop(drop);
				Map<String, DropTrack> tracks = parser.ingest(drop.getDrop());
				data.setTracks(tracks);
				Collection<DropTrack> list = tracks.values();
				Iterator<DropTrack> it = list.iterator();
				while (it.hasNext()) {
					DropTrack value = it.next();
					if (value == null) {
						LOG.info("NULL TRACK VALUE !!!!!!!!");
						continue;
					}
					if (value.isrc == null) {
						LOG.info("NULL ISRC VALUE !!!!!!!!");
					}
					IngestData.Track dataTrack = data.new Track();
					dataTrack.type = value.type;
					if (value.type == Type.DELETE) {
						Track track = trackDAO.getByProductCode(value.productCode);
						if (track == null) {
							continue;
						}
						dataTrack.artist = track.getArtist();
						dataTrack.title = track.getTitle();
						dataTrack.ISRC = track.getISRC();
						dataTrack.productCode = track.getProductCode();
						data.getData().add(dataTrack);
						((IngestWizardData) command).setSize(((IngestWizardData) command).getSize() + 1);
					} else {
						((IngestWizardData) command).setSize(((IngestWizardData) command).getSize() + 1);
						LOG.info("Checking ISRC in cn " + value.isrc);
						Track track = trackDAO.getByKey((String) value.isrc, (String) value.productCode, IParserFactory.getName(drop.getIngestor()));
						if (track == null) { // Try to find old keys for Fuga
							track = trackDAO.getByKey((String) value.isrc, (String) value.isrc, IParserFactory.getName(drop.getIngestor()));
						}
						if (track == null) {
							if (value.files == null || value.files.size() == 0)
								continue; // Skip empty insert
							dataTrack.exists = false;
						} else {
							dataTrack.exists = true;
						}

						dataTrack.artist = (String) value.artist;
						dataTrack.title = (String) value.title;
						dataTrack.ISRC = (String) value.isrc;
						dataTrack.productCode = (String) value.productCode;
						data.getData().add(dataTrack);
					}
				}
			}

		} else if (page == 1) {
		}
	}

	private boolean AddOrUpdateFile(Set<AssetFile> files, DropAssetFile dropFile, boolean force) {
		boolean found = false;
		for (AssetFile file : files) {
			if (file.getType() == dropFile.type) {
				if (!force)
					return false; // Do not update existing file
				file.setPath(dropFile.file);
				// trackDAO.persist(file);
				file.setMD5(dropFile.md5);
				found = true;
			}
		}
		if (!found) {
			LOG.info("Adding file " + dropFile.type + " " + dropFile.file);
			AssetFile file = new AssetFile();
			file.setType(dropFile.type);
			file.setPath(dropFile.file);
			file.setMD5(dropFile.md5);
			// trackDAO.persist(file);
			files.add(file);
		}
		return true;
	}

	/*
	 * Return false if the territory is removed (take down) Return true if the
	 * territory is added or updated.
	 */
	private boolean AddOrUpdateTerritory(Set<Territory> territories, DropTerritory value) {
		LOG.debug("Adding territory " + value.country + " " + value.label);
		if (value.country != null) {
			boolean found = false;
			Territory territory = null;
			for (Territory data : territories) {
				if (data.getCode().equals(value.country)) {
					found = true;
					territory = data;
					if (value.takeDown && value.dealReference != null && value.dealReference.equals(data.getDealReference())) {
						LOG.info("Takedown for " + value.country + " on " + territory.getReportingId());
						territory.setDeleted(true);
						territory.setDeleteDate(new Date());
						// territories.remove(data);
						return false;
					}
				}
			}
			if (!found) {
				territory = new Territory();
				territories.add(territory);
				territory.setCode((String) value.country);
				territory.setCreateDate(new Date());
			}
			territory.setDistributor((String) value.distributor);
			territory.setPublisher(((String) value.publisher) == null ? "" : (String) value.publisher);
			territory.setLabel((String) value.label);
			territory.setCurrency((String) value.currency);
			territory.setPrice((Float) value.price);
			territory.setStartDate((Date) value.startdate);
			territory.setReportingId((String) value.reportingId);
			territory.setPriceCode(value.priceCode);
			territory.setDealReference(value.dealReference);
			territory.setDeleted(false);
		}
		return true;

	}

	private void commit(IParserFactory.Ingestors ingestor, IParser parser, DropData drop, Collection<DropTrack> tracks, boolean status, boolean auto,
			String message) {
		parser.commit(drop, auto);
		logIngest(ingestor, parser, drop, tracks, status, message);
	}

	private void logIngest(Ingestors ingestor, IParser parser, DropData drop, Collection<DropTrack> tracks, boolean status, String message) {
		Set<DropContent> cnt = new HashSet<DropContent>();
		if (tracks != null) {
			for (DropTrack track : tracks) {
				DropContent dropContent = new DropContent();
				dropContent.setArtist(track.getArtist() == null ? "" : track.getArtist());
				dropContent.setTitle(track.getTitle()== null ? "" : track.getTitle());
				String id = (track.getIsrc() == null) ? track.getProductCode() : track.getIsrc();
				if (id != null) {
					dropContent.setISRC(id);
					dropContent.setUpdated(track.isExists());
					cnt.add(dropContent);
				}
			}
		}
		IngestionLog log = new IngestionLog();
		log.setIngestionDate(new Date());
		log.setContent(cnt);
		log.setStatus(status);
		log.setIngestor(ingestor.name());
		log.setMessage(message);
		log.setDropName(drop.getName());
		ingestionLogDAO.persist(log);
	}

}
