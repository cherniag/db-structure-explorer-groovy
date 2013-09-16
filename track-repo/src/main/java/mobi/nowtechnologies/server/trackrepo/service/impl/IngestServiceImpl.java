package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.domain.*;
import mobi.nowtechnologies.server.trackrepo.enums.IngestStatus;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static mobi.nowtechnologies.server.trackrepo.ingest.DropsData.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.*;

public class IngestServiceImpl implements IngestService{

    protected final static int MAX_SIZE_DATA_BUFFER = 10;
    protected final static long EXPIRE_PERIOD_BUFFER = 24*60*60*1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(IngestServiceImpl.class);

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

        LOGGER.debug("formBackingObject [{}]", parserName);
		IngestWizardData result = updateIngestData(null, false);

		if (parserName != null) {
			Ingestors ingestor = Ingestors.valueOf(parserName);
			IParser parser = parserFactory.getParser(ingestor);
			DropsData drops = new DropsData();
			result.setDropdata(drops);
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
			result.setDropdata(drops);
			for (Ingestors ingestor : Ingestors.values()) {
                LOGGER.info("Getting drops for [{}]", ingestor);
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
		return result;
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
	public boolean commitDrops(IngestWizardData command) throws Exception {
        LOGGER.debug("INGEST processFinish");
        command = updateIngestData(command, true);

		for (Drop drop : command.getDropdata().getDrops()) {

			if (!drop.getSelected()) {
                LOGGER.debug("Skipping not selected  [{}]", drop.getName());
				continue;
			}
            LOGGER.info("Loading [{}] with [{}]", drop.getName(), drop.getParser().getClass());

			processDrop(drop, true);
		}
		return true;

	}

    @Transactional(propagation = Propagation.REQUIRED)
	protected void processDrop(Drop drop, boolean updateFiles) throws IOException, InterruptedException {
        LOGGER.debug("INGEST processFinish");
        IParser parser = drop.getParser();
        Ingestors ingestor = drop.getIngestor();

        LOGGER.info("Loading " + drop.getName() + " with " + parser.getClass());

		Map<String, DropTrack> tracks = drop.getIngestdata() != null ? drop.getIngestdata().getTracks() : parser.ingest(drop.getDrop());
		if (tracks == null || tracks.isEmpty()) {
			commit(ingestor, parser, drop.getDrop(), null, false, false, "No tracks");
			// parser.commit(drop.getDrop());
			return;
		}

		Collection<DropTrack> list = tracks.values();
		for (DropTrack value: list) {
			if (value == null) {
                LOGGER.info("Null track value");
				continue;
			}
            LOGGER.info("Ingesting [{}]", value.isrc);

			if (value.type == Type.INSERT || value.type == Type.UPDATE) {
                LOGGER.info("Inserting [{}]", value.isrc);

				Track track = trackRepository.findByKey( value.isrc,  value.productCode, parserFactory.getName(ingestor));
				if (track == null) { // Try to find old keys for Fuga
					track = trackRepository.findByKey( value.isrc,  value.isrc, parserFactory.getName(ingestor));
				}
				if (track == null) {
					if (value.type == Type.UPDATE) {
                        LOGGER.info("Not updating [{}] [{}] [{}]", value.isrc, value.productCode, ingestor.toString());
						continue; // Nothing to update
					}
					if (value.files == null || value.files.size() == 0) {
                        LOGGER.info("Not inserting with no tracks [{}] [{}] [{}]", value.isrc, value.productCode, ingestor.toString());
						continue;
					}
					track = new Track();
					track.setIngestionDate(new Date());
				} else {
					track.setIngestionUpdateDate(new Date());
				}
				track.setTitle( value.title);
				track.setSubTitle( value.subTitle);
				track.setArtist( value.artist);
				track.setIsrc( value.isrc);
				track.setProductId( value.productId);
				track.setProductCode(value.productCode);
				track.setGenre(value.genre);
				track.setCopyright(value.copyright);
				track.setYear( value.year);
				track.setAlbum( value.album);
				track.setIngestor(parserFactory.getName(ingestor));
				track.setXml(value.xml.getBytes());
				track.setInfo(value.info);
				track.setLicensed(value.licensed);
                track.setExplicit(value.explicit);

                if (!addOrUpdateFiles(track, value.files, updateFiles)) {
                    commit(ingestor, parser, drop.getDrop(), list, false, true, "Drop is updating asset files: to be processed manually");
                    return;
                }

                addOrUpdateTerritories(track, value.territories);

				trackRepository.save(track);
			} else if (value.type == Type.DELETE) {
                LOGGER.info("DELETE " + value.productId);
				Track track = trackRepository.findByProductCode((String) value.productId);
				if (track != null)
					trackRepository.delete(track);

			}
		}
		commit(ingestor, parser, drop.getDrop(), list, true, false, "");

	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
	public void processAllDrops() throws Exception {

        DropsData drops = new DropsData();
		for (Ingestors ingestor : Ingestors.values()) {
            LOGGER.info("Getting drops for [{}]", ingestor);
			IParser parser = parserFactory.getParser(ingestor);

			List<DropData> parserDrops = parser.getDrops(true);
			if (parserDrops != null && parserDrops.size() > 0) {
				for (DropData drop : parserDrops) {
                    DropsData.Drop data = drops.new Drop();
                    data.setName(drop.name);
                    data.setParser(parser);
                    data.setIngestor(ingestor);
                    data.setDrop(drop);
					processDrop(data, false);
				}
			}
		}
	}

    @Override
    @Transactional(readOnly = true)
    public IngestWizardData selectDropTracks(IngestWizardData command) throws Exception {
        command.setStatus(IngestStatus.TRACKS_SELECTED);
        return updateIngestData(command, false);
    }

    @Override
    @Transactional(readOnly = true)
	public IngestWizardData selectDrops(IngestWizardData command) throws Exception {
            command.setStatus(IngestStatus.DROPS_SELECTED);
            command = updateIngestData(command, false);

			for (Drop drop : command.getDropdata().getDrops()) {

				IngestData data = new IngestData();
				if (!drop.getSelected()) {
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
                        LOGGER.info("NULL TRACK VALUE !!!!!!!!");
						continue;
					}
					if (value.isrc == null) {
                        LOGGER.info("NULL ISRC VALUE !!!!!!!!");
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
                        LOGGER.info("Checking ISRC in cn " + value.isrc);
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

    protected IngestWizardData updateIngestData(IngestWizardData data, boolean removeAfterGet) throws InvocationTargetException, IllegalAccessException {
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
                throw new IngestSessionClosedException();
            }

            BeanUtilsBean notNull=new NullAwareBeanUtilsBean();
            notNull.copyProperties(suData, data);

            data = suData;
        }

        return data;
    }

    protected boolean addOrUpdateTerritories(Track track, List<DropTerritory> dropTerritories){
        Set<Territory> territories = track.getTerritories();
        if (territories == null) {
            territories = new HashSet<Territory>();
            track.setTerritories(territories);
        }

        StringBuilder territoryCodes = new StringBuilder();
        Date releaseDate = null;
        String label = null;

        boolean takeDown = false;
        for (DropTerritory territoryData : dropTerritories) {

            boolean result = addOrUpdateTerritory(territories, territoryData);
            takeDown |= result;

            if(result){
                if (territoryCodes.length() > 0){
                    territoryCodes.append(", ");
                }else{
                    releaseDate = territoryData.startdate;
                    label = territoryData.label;
                }
                territoryCodes.append(territoryData.country);
            }
        }

        track.setLabel(label);
        track.setReleaseDate(releaseDate);
        track.setTerritoryCodes(territoryCodes.toString());

        return takeDown;
    }

    protected boolean addOrUpdateFiles(Track track, List<DropAssetFile> dropFiles, boolean updateFiles){
        Set<AssetFile> files = track.getFiles();
        if (files == null) {
            files = new HashSet<AssetFile>();
            track.setFiles(files);
        }

        LOGGER.info("Adding files " + dropFiles.size());
        for (DropAssetFile file : dropFiles) {
            if (!addOrUpdateFile(files, file, updateFiles)) {
                return false;
            }
        }

        track.setCoverFile(track.getFile(AssetFile.FileType.IMAGE));
        track.setMediaFile(track.getFile(AssetFile.FileType.DOWNLOAD));
        if(track.getMediaFile() == null)
            track.setMediaFile(track.getFile(AssetFile.FileType.VIDEO));
        if(track.getMediaFile() != null)
            track.setMediaType(track.getMediaFile().getType());

        return true;
    }

    protected boolean addOrUpdateFile(Set<AssetFile> files, DropAssetFile dropFile, boolean force) {
		boolean found = false;
		for (AssetFile file : files) {
			if (file.getType() == dropFile.type) {
				if (!force)
					return false; // Do not update existing file
				file.setPath(dropFile.file);
				file.setMd5(dropFile.md5);
                file.setDuration(dropFile.duration);
				found = true;
			}
		}
		if (!found) {
            LOGGER.info("Adding file " + dropFile.type + " " + dropFile.file);
			AssetFile file = new AssetFile();
			file.setType(dropFile.type);
			file.setPath(dropFile.file);
			file.setMd5(dropFile.md5);
            file.setDuration(dropFile.duration);
			files.add(file);
		}
		return true;
	}

	/*
	 * Return false if the territory is removed (take down) Return true if the
	 * territory is added or updated.
	 */
	protected boolean addOrUpdateTerritory(Set<Territory> territories, DropTerritory value) {
        LOGGER.debug("Adding territory [{}] [{}]", value.country, value.label);
		if (value.country != null) {
			boolean found = false;
			Territory territory = null;
			for (Territory data : territories) {
				if (data.getCode().equals(value.country)) {
					found = true;
					territory = data;
					if (value.takeDown && value.dealReference != null && value.dealReference.equals(data.getDealReference())) {
                        LOGGER.info("Take down for [{}] on [{}]",value.country, territory.getReportingId());
						territory.setDeleted(true);
						territory.setDeleteDate(new Date());
						return false;
					}
				}
			}
			if (!found) {
				territory = new Territory();
				territories.add(territory);
				territory.setCode(value.country);
				territory.setCreateDate(new Date());
			}
			territory.setDistributor(value.distributor);
			territory.setPublisher((value.publisher) == null ? "" : value.publisher);
			territory.setLabel(value.label);
			territory.setCurrency(value.currency);
			territory.setPrice(value.price);
			territory.setStartDate(value.startdate);
			territory.setReportingId(value.reportingId);
			territory.setPriceCode(value.priceCode);
			territory.setDealReference(value.dealReference);
			territory.setDeleted(false);
		}
		return true;

	}

	private void commit(Ingestors ingestor, IParser parser, DropData drop, Collection<DropTrack> tracks, boolean status, boolean auto,
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
		log.setDropName(drop.name);
		ingestionLogRepository.save(log);
	}

}
