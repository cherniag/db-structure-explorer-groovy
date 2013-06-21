package mobi.nowtechnologies.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.domain.AssetFile;
import mobi.nowtechnologies.domain.AssetFile.FileType;
import mobi.nowtechnologies.domain.Track;
import mobi.nowtechnologies.ingestors.DDEXParser;
import mobi.nowtechnologies.service.dao.TrackDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.util.StreamUtils;
import org.springframework.web.HttpRequestHandler;

public class MusicDownload implements HttpRequestHandler {
	protected static final Log LOG = LogFactory.getLog(MusicDownload.class);

	private static final long serialVersionUID = 1L;

	private TrackDAO dao;

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String isrc = (String) request.getParameter("ISRC");
		String productCode = (String) request.getParameter("productCode");
		String ingestor = (String) request.getParameter("ingestor");

		String filePath = null;
		Track track = dao.getByKey(isrc, productCode, ingestor);
		Set<AssetFile> files = track.getFiles();
		if (files != null) {
			Iterator<AssetFile> it = files.iterator();
			while (it.hasNext()) {
				AssetFile file = it.next();
				if (file.getType() == FileType.DOWNLOAD) {
					filePath = file.getPath();
					break;
				}
			}
		}
		if (filePath == null) {
			return;
		}
		String type = null;
		response.setContentType("audio/mpeg3");
		LOG.info("Start stream music for "+filePath);
		try {
			File f = new File(filePath);
			InputStream bi = new FileInputStream(f);
			OutputStream out = response.getOutputStream();
			StreamUtils.copy(bi, out);
			bi.close();
			out.close();
		} catch (Exception e) {
			LOG.error("cannot read " + filePath);
		}
		System.out.println("end xfer music");

	}

	public TrackDAO getDao() {
		return dao;
	}

	public void setDao(TrackDAO dao) {
		this.dao = dao;
	}

}