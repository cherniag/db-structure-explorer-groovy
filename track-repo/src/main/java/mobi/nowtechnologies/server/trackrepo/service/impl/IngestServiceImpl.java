package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.domain.*;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import mobi.nowtechnologies.server.trackrepo.ingest.DropsData.Drop;
import mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors;
import mobi.nowtechnologies.server.trackrepo.repository.IngestionLogRepository;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.service.IngestService;
import mobi.nowtechnologies.server.trackrepo.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class IngestServiceImpl implements IngestService{

    protected final static int MAX_SIZE_DATA_BUFFER = 10;
    protected final static long EXPIRE_PERIOD_BUFFER = 24*60*60*1000;
	protected static final Log LOG = LogFactory.getLog(IngestServiceImpl.class);

	private TrackRepository trackRepository;
	private IngestionLogRepository ingestionLogRepository;
    private IParserFactory parserFactory;

    protected Map<String, IngestWizardData> ingestDataBuffer = Collections.synchronizedMap(new HashMap<String, IngestWizardData>());

	public void setTrackRepository(TrackRepository trackRepository) {
		this.trackRepository = trackRepository;
	}

	public void setIngestionLogRepository(IngestionLogRepository ingestionLogRepository) {
		this.ingestionLogRepository = ingestionLogRepository;
	}

    public void setParserFactory(IParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    @Override
	public IngestWizardData getDrops(String parserName) throws Exception {

		LOG.debug("formBackingObject " + parserName);
		IngestWizardData wizardData = getIngestData(null, false);

		if (parserName != null) {

			IParserFactory.Ingestors ingestor = IParserFactory.Ingestors.valueOf(parserName);
			IParser parser = parserFactory.getParser(ingestor);
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
				IParser parser = parserFactory.getParser(ingestor);

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
	public boolean commitDrops(IngestWizardData command) throws Exception {
		LOG.debug("INGEST processFinish");
        command = getIngestData(command, true);

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

    @Transactional(propagation = Propagation.REQUIRED)
	protected void processDrop(IParserFactory.Ingestors ingestor, IParser parser, DropData drop, boolean updateFiles) throws IOException, InterruptedException {
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

				Track track = trackRepository.findByKey((String) value.isrc, (String) value.productCode, parserFactory.getName(ingestor));
				if (track == null) { // Try to find old keys for Fuga
					track = trackRepository.findByKey((String) value.isrc, (String) value.isrc, parserFactory.getName(ingestor));
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
				track.setIsrc((String) value.isrc);
				track.setProductId((String) value.productId);
				track.setProductCode((String) value.productCode);
				track.setGenre((String) value.genre);
				track.setCopyright((String) value.copyright);
				track.setYear((String) value.year);
				track.setAlbum((String) value.album);
				track.setIngestor(parserFactory.getName(ingestor));
				track.setXml(value.xml.getBytes());
				track.setInfo(value.info);
				track.setLicensed(value.licensed);

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

				trackRepository.save(track);
			} else if (value.type == Type.DELETE) {
				LOG.info("DELETE " + value.productId);
				Track track = trackRepository.findByProductCode((String) value.productId);
				if (track != null)
					trackRepository.delete(track);

			}
		}
		commit(ingestor, parser, drop, list, true, false, "");

	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
	public void processAllDrops() throws Exception {

		for (IParserFactory.Ingestors ingestor : IParserFactory.Ingestors.values()) {
			LOG.info("Getting drops for " + ingestor);
			IParser parser = parserFactory.getParser(ingestor);

			List<DropData> parserDrops = parser.getDrops(true);
			if (parserDrops != null && parserDrops.size() > 0) {
				for (DropData drop : parserDrops) {
					processDrop(ingestor, parser, drop, false);
				}
			}

		}
	}

    @Override
    @Transactional(readOnly = true)
    public IngestWizardData selectDropTracks(IngestWizardData command) throws Exception {
        return getIngestData(command, false);
    }

    @Override
    @Transactional(readOnly = true)
	public IngestWizardData selectDrops(IngestWizardData command) throws Exception {
            command = getIngestData(command, false);

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
						Track track = trackRepository.findByProductCode(value.productCode);
						if (track == null) {
							continue;
						}
						dataTrack.artist = track.getArtist();
						dataTrack.title = track.getTitle();
						dataTrack.ISRC = track.getIsrc();
						dataTrack.productCode = track.getProductCode();
						data.getData().add(dataTrack);
						((IngestWizardData) command).setSize(((IngestWizardData) command).getSize() + 1);
					} else {
						((IngestWizardData) command).setSize(((IngestWizardData) command).getSize() + 1);
						LOG.info("Checking ISRC in cn " + value.isrc);
						Track track = trackRepository.findByKey((String) value.isrc, (String) value.productCode, parserFactory.getName(drop.getIngestor()));
						if (track == null) { // Try to find old keys for Fuga
							track = trackRepository.findByKey((String) value.isrc, (String) value.isrc, parserFactory.getName(drop.getIngestor()));
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

            return command;
	}

    protected IngestWizardData getIngestData(IngestWizardData data, boolean removeAfterGet) throws InvocationTargetException, IllegalAccessException {
        if(data == null){
            String suid = String.valueOf(System.currentTimeMillis());
            data = new IngestWizardData();
            data.setSuid(suid);

            if(ingestDataBuffer.size() == MAX_SIZE_DATA_BUFFER){
                Long earliest = null;
                Long nowtime = System.currentTimeMillis();
                Iterator<Map.Entry<String, IngestWizardData>> i = ingestDataBuffer.entrySet().iterator();
                while(i.hasNext()){
                    Map.Entry<String, IngestWizardData> entry = i.next();
                    Long time = new Long(entry.getKey());
                    if(earliest == null || earliest > time)
                        earliest = time;
                    if(nowtime - time >= EXPIRE_PERIOD_BUFFER)
                        i.remove();
                }

                if(ingestDataBuffer.size() == MAX_SIZE_DATA_BUFFER){
                     ingestDataBuffer.remove(earliest.toString());
                }
            }

            ingestDataBuffer.put(suid, data);
        } else {
            IngestWizardData suData = removeAfterGet ? ingestDataBuffer.remove(data.getSuid()) : ingestDataBuffer.get(data.getSuid());

            if(suData == null){
                throw new IngestSessionClosed();
            }

            BeanUtilsBean notNull=new NullAwareBeanUtilsBean();
            notNull.copyProperties(suData, data);

            data = suData;
        }

        return data;
    }

	private boolean AddOrUpdateFile(Set<AssetFile> files, DropAssetFile dropFile, boolean force) {
		boolean found = false;
		for (AssetFile file : files) {
			if (file.getType() == dropFile.type) {
				if (!force)
					return false; // Do not update existing file
				file.setPath(dropFile.file);
				// trackRepository.persist(file);
				file.setMd5(dropFile.md5);
				found = true;
			}
		}
		if (!found) {
			LOG.info("Adding file " + dropFile.type + " " + dropFile.file);
			AssetFile file = new AssetFile();
			file.setType(dropFile.type);
			file.setPath(dropFile.file);
			file.setMd5(dropFile.md5);
			// trackRepository.persist(file);
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
			String message) throws IOException, InterruptedException {
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
					dropContent.setIsrc(id);
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
		ingestionLogRepository.save(log);
	}

}
