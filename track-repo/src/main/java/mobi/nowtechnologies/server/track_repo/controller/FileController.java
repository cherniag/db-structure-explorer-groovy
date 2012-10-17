package mobi.nowtechnologies.server.track_repo.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.track_repo.dto.AssetFileDto;
import mobi.nowtechnologies.server.track_repo.service.FileService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Controller
public class FileController extends AbstractCommonController{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);
	
	private FileService fileService;

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	@RequestMapping(value="/file")
	public void file(HttpServletResponse resp, @RequestParam("id") Long id) {	
			OutputStream respStream = null;
			try {
				AssetFileDto file = fileService.getFile(id);
				if(file != null){
					resp.setContentType(file.getType().getMime());
					try{
						respStream = resp.getOutputStream();
						respStream.write(file.getContent());
					}finally{
						IOUtils.closeQuietly(respStream);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Can't send file", e);
			}
	}
}
