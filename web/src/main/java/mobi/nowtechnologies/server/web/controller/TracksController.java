package mobi.nowtechnologies.server.web.controller;

import java.io.*;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.MediaLogService;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.web.PurchasedTrackDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */

@Controller
public class TracksController extends CommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TracksController.class);

	private static final String QUATE = "\"";
	private static final String DOT = ".";
	private static final String ATTACHMENT_FILENAME = "attachment; filename=\"";
	private static final String CONTENT_DISPOSITION = "Content-Disposition";

	private FileService fileService;
	private MediaLogService mediaLogService;

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public void setMediaLogService(MediaLogService mediaLogService) {
		this.mediaLogService = mediaLogService;
	}

	@RequestMapping(value = "/purchased_tracks.html", method = RequestMethod.GET)
	public ModelAndView getPurchasedTracksPage(HttpServletRequest request) {
		LOGGER.debug("input parameters request: [{}]", request);
		ModelAndView modelAndView = null;

		int userId = getUserId();
		List<PurchasedTrackDto> purchasedTrackDtos = mediaLogService.getPurchasedTracksByUserId(userId);

		for (PurchasedTrackDto purchasedTrackDto : purchasedTrackDtos) {
			int mediaId = purchasedTrackDto.getMediaId();
			purchasedTrackDto.setDownloadedOriginal(mediaLogService.isUserAlreadyDownloadOriginal(mediaId, userId));
		}

		modelAndView = new ModelAndView("purchased_tracks");
		modelAndView.getModel().put(PurchasedTrackDto.PURCHASED_TRACK_DTO_LIST, purchasedTrackDtos);
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}

	@RequestMapping(value = "/purchased_tracks/{trackname}", method = RequestMethod.GET)
	public void downloadTrack(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute(PurchasedTrackDto.PURCHASED_TRACK_DTO) PurchasedTrackDto purchasedTrackDto, @PathVariable("trackname") String trackname) throws ServiceException, IOException {
		LOGGER.debug("input parameters request, response, purchasedTrackDto: [{}], [{}], [{}]", new Object[] { request, response, purchasedTrackDto });

		int userId = getUserId();

		boolean isUserAlreadyDownloadOriginal = mediaLogService.isUserAlreadyDownloadOriginal(purchasedTrackDto.getMediaId(), userId);

		try {
			OutputStream outputStream = response.getOutputStream();
			
			if (!isUserAlreadyDownloadOriginal) {
				ByteArrayOutputStream outstream = new ByteArrayOutputStream();
				File file = fileService.downloadOriginalFile(outstream, purchasedTrackDto.getMediaIsrc(), userId);

				final String fileName = file.getName();
				final String contentType = fileService.getContentType(fileName);
				final String ext = fileService.getExtention(fileName);
				response.setContentType(contentType);
				response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + trackname + DOT + ext + QUATE);
				outputStream.write(outstream.toByteArray());
			}

			outputStream.flush();
		} catch (IOException e) {
			LOGGER.error("Exception while closing outputstream on downloading of the file");
			throw new ServiceException("error.download.file", "Can't download puchased file");
		}
	}

}
