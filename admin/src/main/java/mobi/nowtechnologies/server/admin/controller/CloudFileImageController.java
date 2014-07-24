package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.service.CloudFileImagesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * Created by oar on 2/25/14.
 */
@Controller
@RequestMapping("/images")
public class CloudFileImageController {

    @Resource
    private CloudFileImagesService cloudFileImagesService;

    @RequestMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.GET})
    @ResponseBody
    public Collection<ImageDTO> find(@RequestParam("prefix") String prefix) {
        return cloudFileImagesService.findByPrefix(prefix);
    }

    @RequestMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST})
    @ResponseBody
    public ImageDTO upload(MultipartFile file) {
        return cloudFileImagesService.uploadImage(file);
    }

    @RequestMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity delete(@RequestParam("fileName") String fileName) {
        cloudFileImagesService.deleteImage(fileName);
        return new ResponseEntity(HttpStatus.OK);
    }

}
