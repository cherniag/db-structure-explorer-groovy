package mobi.nowtechnologies.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

import mobi.nowtechnologies.ExternalCommandThread;
import mobi.nowtechnologies.domain.AssetFile;
import mobi.nowtechnologies.domain.Territory;
import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.java.server.uits.MP3Manager;
import mobi.nowtechnologies.java.server.uits.MP4Manager;
import mobi.nowtechnologies.mvc.controller.PublishData;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.ResourceFile.FileType;
import mobi.nowtechnologies.service.dao.*;
import mobi.nowtechnologies.util.Property;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublishService  {
	protected static final Log LOG = LogFactory.getLog(PublishService.class);

	private TrackDAO trackDAO;
	private MediaDAO mediaDAO;
	private ArtistDAO artistDAO;
	private MediaFileDAO fileDAO;
	private GenreDAO genreDAO;

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	public void setMediaDAO(MediaDAO mediaDAO) {
		this.mediaDAO = mediaDAO;
	}

	public void setArtistDAO(ArtistDAO artistDAO) {
		this.artistDAO = artistDAO;
	}

	public void setFileDAO(MediaFileDAO fileDAO) {
		this.fileDAO = fileDAO;
	}

	public void setGenreDAO(GenreDAO genreDAO) {
		this.genreDAO = genreDAO;
	}

	public Object prepareData(String id) throws Exception {
		Track track = null;
		Artist artist = null;
		PublishData data = new PublishData();
		if (id != null) {
			Long idLong = null;
			try {
				idLong = Long.valueOf(id);
			} catch (NumberFormatException e) {
				throw new UnsupportedOperationException(e);
			}

			track = (Track) trackDAO.findById(idLong);
			if (track == null)
				throw new EntityNotFoundException();
			data.setTrack(track);
			artist = artistDAO.getByRealName(track.getArtist());
			if (artist == null) {
				artist = artistDAO.getByname(track.getArtist());
				if (artist == null) {
					artist = new Artist();
					if (track.getArtist().length() > 40) {
						artist.setName(track.getArtist().substring(0, 39).replace('&', ' '));
						data.setPublishArtist(track.getArtist().substring(0, 39).replace('&', ' '));
					} else {
						artist.setName(track.getArtist().replace('&', ' '));
						data.setPublishArtist(track.getArtist().replace('&', ' '));
					}
					artist.setRealName(track.getArtist());
					artist.setInfo("");
					artistDAO.persist(artist);
				} else {
					LOG.info("Got artist from publish name " + track.getArtist());
					data.setPublishArtist(artist.getName());

				}
			} else {
				LOG.info("Got artist from real name " + track.getArtist());
				data.setPublishArtist(artist.getName());
			}
			data.setArtist(artist);
			// data.setPublishArtist(artist.getName());

			Media media = mediaDAO.getByISRC(track.getISRC());
			if (media != null) {
				data.setPublishTitle(media.getTitle());
				if (media.getiTunesUrl() != null && !"".equals(media.getiTunesUrl())) {
					try {
						LOG.info("URL " + media.getiTunesUrl());
						Matcher m = Pattern.compile("url=.*\\%26").matcher(media.getiTunesUrl());
						if (m.find()) {
							String url = m.group();
							if (url != null && !"".equals(url)) {
								url = url.substring(4, url.length() - 3).replace("%26", "&");
								data.setiTunesUrl(url);
								data.setEditiTunesUrl(url);
							}
						}
					} catch (Exception e) {
						LOG.error("Cannot get iTunes URL "+e.getMessage());
					}
				}
			} else {
				if (track.getTitle().length() > 50)
					data.setPublishTitle(track.getTitle().substring(0, 49).replace('&', ' '));
				else
					data.setPublishTitle(track.getTitle().replace('&', ' '));
			}
			if (data.getiTunesUrl() == null || "".equals(data.getiTunesUrl())) {
				ExternalCommandThread thread = new ExternalCommandThread();
				thread.setCommand(Property.getInstance().getStringValue("cn.publish.itunes.script"));
				thread.addParam(track.getArtist());
				thread.addParam(track.getTitle());
				thread.run();
				if (thread.getExitCode() == 0) {
					data.setiTunesUrl(thread.getOutBuffer());
					data.setEditiTunesUrl(data.getiTunesUrl());
					LOG.info(thread.getOutBuffer());
				}

			}

		}
		
		Set<Territory> territories = track.getTerritories();
		if (territories != null && territories.size() > 0) {
			ArrayList<Territory> validTerritories = new ArrayList<Territory>();
			Iterator<Territory> it = territories.iterator();
			while (it.hasNext()) {
				Territory territory = it.next();
				if (territory.isDeleted())
					continue;
				validTerritories.add(territory);
			}
			if (validTerritories.size() != 0)
				data.setTerritories(validTerritories);
		}

		return data;

	}

	public boolean process(PublishData data) throws Exception {
		if (data != null) {
			Long idLong = null;
			try {
				Track track = (Track) trackDAO.findById(data.getTrack().getId());
				Set<AssetFile> files = track.getFiles();

				ExternalCommandThread thread = new ExternalCommandThread();
				thread.setCommand(Property.getInstance().getStringValue("cn.publish.script"));
				if (data.isHighRate()) {
					thread.addParam(""); // Force re-encoding

				} else {
					thread.addParam(emptyNull(getFileName(files, AssetFile.FileType.MOBILE)));
				}
				// thread.addParam(getFileName(files,
				// AssetFile.FileType.PREVIEW)); Preview removed as we need m4a
				thread.addParam("");
				thread.addParam(emptyNull(getFileName(files, AssetFile.FileType.DOWNLOAD)));
				thread.addParam(emptyNull(getFileName(files, AssetFile.FileType.IMAGE)));
				thread.addParam(emptyNull(track.getTitle()));
				thread.addParam(emptyNull(track.getArtist()));
				thread.addParam(emptyNull(track.getAlbum()));
				thread.addParam(emptyNull(track.getGenre()));
				thread.addParam(emptyNull(""));
				thread.addParam(emptyNull(track.getYear()));
				thread.addParam(emptyNull(track.getCopyright()));
				thread.addParam(emptyNull(track.getISRC()));
				thread.addParam(emptyNull(Property.getInstance().getStringValue("cn.publish.destination")));
				thread.addParam(emptyNull(Property.getInstance().getStringValue("cn.publish.split.cp")));
				thread.addParam(emptyNull(Property.getInstance().getStringValue("cn.publish.nero.home")));
				thread.addParam(emptyNull(Property.getInstance().getStringValue("cn.publish.workdir")));
				if (data.isHighRate()) {
					thread.addParam("96");

				} else {
					thread.addParam("48");
				}
				thread.addParam(track.getLicensed() ? "NO" : "YES");
				thread.run();
				if (thread.getExitCode() != 0) {
					LOG.error("Cannot process files");
					return false;
				}

				// Compute media hashes
				String mp3hash = getMediaHash(Property.getInstance().getStringValue("cn.publish.workdir") + "/" + track.getISRC() + ".mp3");
				// String mp3hash = "";
				String aac48hash = getMediaHash(Property.getInstance().getStringValue("cn.publish.workdir") + "/" + track.getISRC() + "_48.m4a");
				String aac96hash = getMediaHash(Property.getInstance().getStringValue("cn.publish.workdir") + "/" + track.getISRC() + "_96.m4a");

				if (mp3hash == null || aac48hash == null || aac96hash == null) {
					LOG.error("Cannot get media hash");
					return false;
				}

				
				Media media = mediaDAO.getByISRC(track.getISRC());
				LOG.debug("MEDIA " + media);
				if (media == null) {
					media = new Media();
					media.setIsrc(track.getISRC());
				}
				LOG.debug("TITLE " + data.getPublishTitle());
				media.setTitle(data.getPublishTitle());
				if (track.getGenre() == null || "".equals(track.getGenre())) {
					track.setGenre("Default");
				}
				String genreName = track.getGenre();
				genreName = genreName.replaceAll("&", "");
				Genre genre = (Genre) genreDAO.getByname(genreName);
				if (genre == null) {
					genre = new Genre();
					genre.setName(genreName);
					genreDAO.persist(genre);
				}
				LOG.debug("GENRE " + genreName);
				media.setGenre(genre);
				Artist artist = artistDAO.getByname(data.getPublishArtist());
				if (artist == null) {
					if (artist == null) {
						artist = new Artist();
					}
				}
				media.setArtist(artist);
				artist.setName(data.getPublishArtist());
				artist.setRealName(track.getArtist());
				artist.setInfo(data.getArtist().getInfo());
				artistDAO.persist(artist);
				Set<ResourceFile> mediaFiles = buildFileList(media.getFiles(), track.getISRC(), mp3hash, aac48hash, aac96hash);
				media.setFiles(mediaFiles);
				media.setInfo(artist.getInfo());

				media.setiTunesUrl("http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=" + data.getEditiTunesUrl().replace("&", "%26")
						+ "%26partnerId=2003");
				if (data.isHighRate()) {
					MediaFile audio = fileDAO.createFile(track.getISRC() + "_96.aud", (byte) 2);
					media.setAudioFile(audio);
					MediaFile header = fileDAO.createFile(track.getISRC() + "_96.hdr", (byte) 1);
					media.setHeaderFile(header);
				} else {
					MediaFile audio = fileDAO.createFile(track.getISRC() + "_48.aud", (byte) 2);
					media.setAudioFile(audio);
					MediaFile header = fileDAO.createFile(track.getISRC() + "_48.hdr", (byte) 1);
					media.setHeaderFile(header);
				}
				MediaFile audioPre = fileDAO.createFile(track.getISRC() + "P.aud", (byte) 2);
				media.setAudioPreviewFile(audioPre);
				MediaFile headerPre = fileDAO.createFile(track.getISRC() + "P.hdr", (byte) 1);
				media.setHeaderPreviewFile(headerPre);
				if (!track.getLicensed()) {
					media.setAudioFile(audioPre);
					media.setHeaderFile(headerPre);
				}
				MediaFile imageFileLarge = fileDAO.createFile(track.getISRC() + "L.jpg", (byte) 3);
				media.setImageFIleLarge(imageFileLarge);
				MediaFile imageFileSmall = fileDAO.createFile(track.getISRC() + "S.jpg", (byte) 3);
				media.setImageFileSmall(imageFileSmall);
				MediaFile imageFileGeneric = fileDAO.createFile(track.getISRC() + ".jpg", (byte) 3);
				media.setImgFileResolution(imageFileGeneric);
				MediaFile downloadAudio = fileDAO.createFile(track.getISRC() + ".mp3", (byte) 1);
				media.setPurchasedFile(downloadAudio);


				Territory publishTerritory = getValidTerritory(data.getTrack(), "gb");
				Date publishDate = new Date();
				if (publishTerritory != null) {
					publishDate.setTime(publishTerritory.getStartDate().getTime());
				}
				media.setPublishDate((int) (publishDate.getTime()/1000));

				

				mediaDAO.persist(media);
				LOG.debug("PERSIST MEDIA " + media);
				track.setPublishDate(new Date());
				trackDAO.persist(track);

				/*
				 * Publish script parameters AUDIO_FILE=$1 PREVIEW_FILE=$2
				 * FULL_AUDIO=$3 IMAGE=$4 META_TITLE=$5 META_AUTHOR=$6
				 * META_ALBUM=$7 META_GENRE=$8 META_TRACK=$9 META_DATE=${10}
				 * META_COPY=${11} ISRC=${12} PUBLISH_DIR=${13}
				 */

			} catch (NumberFormatException e) {
				throw new UnsupportedOperationException(e);
			}
		} else {
			throw new UnsupportedOperationException("required entity id");
		}

		return true;
	}

	Set<ResourceFile> buildFileList(Set<ResourceFile> list, String Isrc, String mp3hash, String aac48Hash, String aac96hash) {
		if (list == null)
			list = new HashSet<ResourceFile>();
		addFileOrReplace(list, "header/" + Isrc + "_48.hdr", ResourceFile.FileType.MOBILE_HEADER, "48kpbs", null);
		addFileOrReplace(list, "audio/" + Isrc + "_48.aud", ResourceFile.FileType.MOBILE_AUDIO, "48kpbs", aac48Hash);
		addFileOrReplace(list, "header/" + Isrc + "_96.hdr", ResourceFile.FileType.MOBILE_HEADER, "96kpbs", null);
		addFileOrReplace(list, "audio/" + Isrc + "_96.aud", ResourceFile.FileType.MOBILE_AUDIO, "96kpbs", aac96hash);
		addFileOrReplace(list, "purchase/" + Isrc + ".mp3", ResourceFile.FileType.DOWNLOAD, "256kpbs", mp3hash);
		addFileOrReplace(list, "image/" + Isrc + "_S.jpg", ResourceFile.FileType.IMAGE, "S", null);
		addFileOrReplace(list, "image/" + Isrc + "_L.jpg", ResourceFile.FileType.IMAGE, "L", null);
		addFileOrReplace(list, "image/" + Isrc + "_21.jpg", ResourceFile.FileType.IMAGE, "21", null);
		addFileOrReplace(list, "image/" + Isrc + "_11.jpg", ResourceFile.FileType.IMAGE, "11", null);
		addFileOrReplace(list, "image/" + Isrc + "_3.jpg", ResourceFile.FileType.IMAGE, "3", null);
		addFileOrReplace(list, "image/" + Isrc + "_22.jpg", ResourceFile.FileType.IMAGE, "22", null);
		addFileOrReplace(list, "image/" + Isrc + "_6.jpg", ResourceFile.FileType.IMAGE, "6", null);

		return list;
	}

	private void addFileOrReplace(Set<ResourceFile> list, String path, FileType type, String resolution, String mediaHash) {
		Iterator<ResourceFile> it = list.iterator();
		while (it.hasNext()) {
			ResourceFile file = it.next();
			if (file.getPath().equals(path) && file.getType() == type && file.getResolution().equals(resolution)) {
				file.setMediaHash(mediaHash);
				return;
			}
		}
		ResourceFile file = new ResourceFile(path, type, resolution);
		file.setMediaHash(mediaHash);
		list.add(file);
		fileDAO.persist(file);

	}


	private String getFileName(Set<AssetFile> files, AssetFile.FileType type) {
		for (AssetFile file : files) {
			if (file.getType() == type) {
				return file.getPath();
			}
		}
		return "";

	}

	private String emptyNull(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	private String getMediaHash(String fileName) {
		InputStream in = null;
		try {
			String mediaHash = null;
			if (fileName.endsWith(".mp3") || fileName.endsWith(".MP3")) {
				MP3Manager mp3Manager = new MP3Manager();
				mediaHash = mp3Manager.mp3GetMediaHash(fileName);
			} else { // Assume AAC.....
				in = new FileInputStream(fileName);
				MP4Manager mp4manager = new MP4Manager();
				mediaHash = mp4manager.getMediaHash(in);
			}
			return mediaHash;
		} catch (Exception e) {
			LOG.error("Cannot get hash " + e.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	protected Territory getValidTerritory(Track track, String country) {
		Set<Territory> territories = track.getTerritories();
		if (territories != null && territories.size() > 0) {
			Territory countryTerritory = null;
			Territory worldwide = null;

			Iterator<Territory> it = territories.iterator();
			while (it.hasNext()) {
				Territory territory = it.next();
				if (territory.isDeleted())
					continue;
				if (country.equalsIgnoreCase(territory.getCode())) {
					countryTerritory = territory;
				}
				if ("worldwide".equalsIgnoreCase(territory.getCode())) {
					worldwide = territory;
				}
			}
			if (countryTerritory != null) {
				return countryTerritory;
			}
			if (worldwide != null) {
				return worldwide;
			}
		}
		return null;

	}
}
