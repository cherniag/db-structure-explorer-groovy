package mobi.nowtechnologies.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
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
import org.springframework.web.HttpRequestHandler;

public class ImageDownload implements HttpRequestHandler {
	protected static final Log LOG = LogFactory.getLog(ImageDownload.class);

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
				if (file.getType() == FileType.IMAGE) {
					filePath = file.getPath();
					break;
				}
			}
		}
		if (filePath == null) {
			LOG.info("No file path");
			return;
		}
		LOG.info("Downloading image "+filePath);
		String type = null;
		if (filePath.endsWith(".png") || filePath.endsWith(".PNG")) {
			response.setContentType("image/png");
			type ="png";

		} else {
			response.setContentType("image/jpeg");
			type ="jpg";
		}
		try {
		File f = new File(filePath);
		
	    // Open the file and output streams
	    FileInputStream in = new FileInputStream(f);
	    OutputStream out = response.getOutputStream();

	    // Copy the contents of the file to the output stream
	    byte[] buf = new byte[1024];
	    int count = 0;
	    while ((count = in.read(buf)) >= 0) {
	        out.write(buf, 0, count);
	    }
	    in.close();
	    out.close();

/*		BufferedImage bi = ImageIO.read(f);
		OutputStream out = response.getOutputStream();
		ImageIO.write(bi, type, out);
		out.close();
		*/
		} catch(Exception e) {
			LOG.error("cannot read "+filePath+ " "+e.getMessage());
		}

	}

	public TrackDAO getDao() {
		return dao;
	}

	public void setDao(TrackDAO dao) {
		this.dao = dao;
	}


}