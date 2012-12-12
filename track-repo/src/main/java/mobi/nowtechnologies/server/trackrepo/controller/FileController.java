package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.dto.AssetFileDto;
import mobi.nowtechnologies.server.trackrepo.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;

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
				AssetFile file = fileService.getFile(id);
				if(file != null){
					resp.setContentType(AssetFileDto.toFileType(file.getType()).getMime());
					try{
						respStream = resp.getOutputStream();
						respStream.write(FileUtils.readFileToByteArray(new File(file.getPath())));
					}finally{
						IOUtils.closeQuietly(respStream);
					}
				}
			} catch (Exception e) {
				LOGGER.error("Can't send file", e);
			}
	}
}
