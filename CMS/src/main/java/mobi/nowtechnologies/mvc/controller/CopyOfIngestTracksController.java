package mobi.nowtechnologies.mvc.controller;

import mobi.nowtechnologies.domain.AssetFile;
import mobi.nowtechnologies.domain.Territory;
import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.ingestors.IParser;
import mobi.nowtechnologies.ingestors.sony.SonyParser;
import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.service.dao.MediaDAO;
import mobi.nowtechnologies.service.dao.TrackDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class CopyOfIngestTracksController extends ParameterizableViewController {

	private TrackDAO trackDAO;
	private MediaDAO mediaDAO;

	public TrackDAO getTrackDAO() {
		return trackDAO;
	}

	public void setTrackDAO(TrackDAO trackDAO) {
		this.trackDAO = trackDAO;
	}

	public MediaDAO getMediaDAO() {
		return mediaDAO;
	}

	public void setMediaDAO(MediaDAO mediaDAO) {
		this.mediaDAO = mediaDAO;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {

		ModelAndView mv = new ModelAndView(getViewName());

		return mv;
	}
	
	private void AddOrUpdateFile(Set<AssetFile> files, Map<String, Object> value, String key, AssetFile.FileType type) {
		if (value.get(key) != null) {
			boolean found = false;
			for (AssetFile file:files) {
				if (file.getType() == type) {
					file.setPath((String) value.get(key));
					//trackDAO.persist(file);
					found = true;
				}
			}
			if (!found) {
				AssetFile file = new AssetFile();
				file.setType(type);
				file.setPath((String) value.get(key));
				//trackDAO.persist(file);
				files.add(file);
			}
		}

	}
	private void AddOrUpdateTerritory(Set<Territory> territories, Map<String, Object> value) {
		System.out.println(value.get("country")  +" "+value.get("label"));
		if (value.get("country") != null) {
			boolean found = false;
			for (Territory territory:territories) {
				if (territory.getCode().equals(value.get("country"))) {
					found = true;
				}
			}
			if (!found) {
				Territory territory = new Territory();
				territories.add(territory);
				territory.setCode((String) value.get("country"));
				territory.setDistributor((String) value.get("distributor"));
				territory.setLabel((String) value.get("label"));
				territory.setCurrency((String) value.get("currency"));
				territory.setPrice((Float) value.get("price"));
				territory.setStartDate((Date) value.get("startdate"));
				territory.setReportingId((String) value.get("reportingId"));
				System.out.println("Adding "+value.get("country"));

			}
		}

	}

}
