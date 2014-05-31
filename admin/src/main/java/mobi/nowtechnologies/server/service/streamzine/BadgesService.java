package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.service.CloudFileImagesService;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class BadgesService {
    private FilenameAliasRepository filenameAliasRepository;

    private CloudFileImagesService cloudFileImagesService;

    public void setFilenameAliasRepository(FilenameAliasRepository filenameAliasRepository) {
        this.filenameAliasRepository = filenameAliasRepository;
    }

    public void setCloudFileImagesService(CloudFileImagesService cloudFileImagesService) {
        this.cloudFileImagesService = cloudFileImagesService;
    }

    @Transactional(readOnly = true)
    public List<FilenameAlias> findAllBadges() {
        return filenameAliasRepository.findAllByDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
    }


    public boolean update(String oldName, String newName) {
        FilenameAlias found = filenameAliasRepository.findByAlias(oldName);

        Assert.notNull(found);

        found.setAlias(newName);
        try {
            filenameAliasRepository.saveAndFlush(found);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Transactional
    public void delete(String name) {
        FilenameAlias byAlias = filenameAliasRepository.findByAlias(name);

        filenameAliasRepository.delete(byAlias);

        try {
            cloudFileImagesService.deleteImage(byAlias.getFileName());
        } catch (ExternalServiceException e) {
            logger().error("Error during deleting badge: ", e);
        }
    }

    @Transactional
    public ImageDTO upload(MultipartFile file) {
        final String fileNameInCloud = generateUniqueFileName(file);

        filenameAliasRepository.save(new FilenameAlias(fileNameInCloud, fileNameInCloud).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));

        return cloudFileImagesService.uploadImageWithGivenName(file, fileNameInCloud);
    }

    private String generateUniqueFileName(MultipartFile file) {
        final String originalFilename = file.getOriginalFilename();
        return FilenameUtils.getBaseName(originalFilename) + "_" + System.nanoTime() + "." + FilenameUtils.getExtension(originalFilename);
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}
