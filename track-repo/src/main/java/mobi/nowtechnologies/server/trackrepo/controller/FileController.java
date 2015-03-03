package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.dto.AssetFileDto;
import mobi.nowtechnologies.server.trackrepo.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @author Alexander Kolpakov (akolpakov)
@Controller
public class FileController extends AbstractCommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);
    private static final int DEFAULT_BUFFER_SIZE_BYTES = 10240; // 10KB

    private FileService fileService;

    private static void copy(RandomAccessFile input, OutputStream output, long start, long length) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE_BYTES];
        int read;

        if (input.length() == length) {
            // Write full range.
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        }
        else {
            // Write partial range.
            input.seek(start);
            long toRead = length;

            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                }
                else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/file")
    public void file(@RequestParam("id") Long id, HttpServletRequest req, HttpServletResponse resp) {
        try {
            AssetFile file = fileService.getFile(id);
            if (file == null) {
                return;
            }
            File respFile = new File(file.getPath());

            String contentType = file.getPath().endsWith("wav") ?
                                 "audio/wav" :
                                 AssetFileDto.toFileType(file.getType()).getMime();

            stream(respFile, contentType, req, resp);
        }
        catch (Exception e) {
            LOGGER.error("Can't send file", e);
        }
    }

    public void stream(File file, String contentType, HttpServletRequest req, HttpServletResponse resp) {
        long[] range = parseRange(req, file.length());
        long start = range[0];
        long end = range[1];
        long contentLength = end - start + 1;

        if (start <= end) {
            resp.setContentType(contentType);
            resp.setHeader("Accept-Ranges", "bytes");
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + file.length());
            resp.setHeader("Content-Length", String.valueOf(file.length()));

            RandomAccessFile in = null;
            OutputStream out = null;
            try {
                in = new RandomAccessFile(file, "r");
                copy(in, resp.getOutputStream(), start, contentLength);
            }
            catch (IOException e) {
                LOGGER.error("Can't send file", e);
            }
            finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }

    private long[] parseRange(HttpServletRequest request, long fileLength) {
        String rangeHeader = request.getHeader("range");
        if (StringUtils.isBlank(rangeHeader)) {
            return new long[] {0, fileLength - 1};
        }

        String rangeValue = rangeHeader.trim().substring("bytes=".length());
        long start, end;
        if (rangeValue.startsWith("-")) {
            end = fileLength - 1;
            start = fileLength - 1 - Long.parseLong(rangeValue.substring(1));
        }
        else {
            String[] range = rangeValue.split("-");
            start = Long.parseLong(range[0]);
            end = range.length > 1 ?
                  Long.parseLong(range[1]) :
                  fileLength - 1;
        }
        if (end > fileLength - 1) {
            end = fileLength - 1;
        }

        return new long[] {start, end};
    }
}
