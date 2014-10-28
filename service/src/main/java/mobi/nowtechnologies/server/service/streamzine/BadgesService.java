package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Dimensions;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.BadgeMappingRepository;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.persistence.repository.ResolutionRepository;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.file.image.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class BadgesService {
    private FilenameAliasRepository filenameAliasRepository;
    private CloudFileImagesService cloudFileImagesService;
    private ResolutionRepository resolutionRepository;
    private BadgeMappingRepository badgeMappingRepository;
    private CloudFileService cloudFileService;
    private ImageService imageService;

    public void setFilenameAliasRepository(FilenameAliasRepository filenameAliasRepository) {
        this.filenameAliasRepository = filenameAliasRepository;
    }
    public void setCloudFileImagesService(CloudFileImagesService cloudFileImagesService) {
        this.cloudFileImagesService = cloudFileImagesService;
    }
    public void setResolutionRepository(ResolutionRepository resolutionRepository) {
        this.resolutionRepository = resolutionRepository;
    }
    public void setBadgeMappingRepository(BadgeMappingRepository badgeMappingRepository) {
        this.badgeMappingRepository = badgeMappingRepository;
    }
    public void setCloudFileService(CloudFileService cloudFileService) {
        this.cloudFileService = cloudFileService;
    }

    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Transactional(readOnly = true)
    public Map<Resolution, Map<Long, BadgeMapping>> getMatrix(Community community) {
        Map<Resolution, Map<Long, BadgeMapping>> matrix = new LinkedHashMap<Resolution, Map<Long, BadgeMapping>>();

        List<Resolution> resolutions = resolutionRepository.findAllSorted();
        for (Resolution resolution : resolutions) {
            matrix.put(resolution, new HashMap<Long, BadgeMapping>());
        }

        for (BadgeMapping badgeMapping : badgeMappingRepository.findAllDefault(community)) {
            FilenameAlias originalFilenameAlias = badgeMapping.getOriginalFilenameAlias();

            if(!resolutions.isEmpty()) {
                List<BadgeMapping> matched = badgeMappingRepository.findByCommunityResolutionsAndOriginalAlias(community, originalFilenameAlias, resolutions);
                for (BadgeMapping mapping : matched) {
                    matrix.get(mapping.getResolution()).put(originalFilenameAlias.getId(), mapping);
                }
            }

        }
        return matrix;
    }

    @Transactional
    public void uploadBadge(Community community, String title, String file, Dimensions dim) throws IOException {
        FilenameAlias generalAlias = new FilenameAlias(file, title, dim).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias);
        BadgeMapping generalMapping = BadgeMapping.general(community, generalAlias);
        badgeMappingRepository.saveAndFlush(generalMapping);

        // add grid:
        for (Resolution resolution : resolutionRepository.findAll()) {
            logger().info("Creating stub for community {} and resolution {}", community, resolution);
            BadgeMapping specificMapping = BadgeMapping.specific(resolution, community, generalAlias);
            badgeMappingRepository.saveAndFlush(specificMapping);
        }
    }

    @Transactional(rollbackFor = IOException.class)
    public void createBadgeResolution(Community community, Resolution resolution, FilenameAlias original, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        cloudFileService.downloadToStream(outputStream, original.getFileName());

        BadgeMapping found = badgeMappingRepository.findByCommunityResolutionAndOriginalAlias(community, resolution, original);

        // means that the resolution was created from another community than current one:
        if(found == null) {
            found = badgeMappingRepository.saveAndFlush(BadgeMapping.specific(resolution, community, original));
        }

        boolean needToRemovePrevious = found.getFilenameAlias() != null;
        if(needToRemovePrevious) {
            try {
                cloudFileService.deleteFile(found.getFilenameAlias().getFileName());
            } catch (Exception e) {
                logger().warn("Could not delete file assigned to {}", found.getFilenameAlias(), e);
            }
        }

        byte[] bytes = imageService.resize(outputStream.toByteArray(), width, height);
        Dimensions dim = imageService.getImageFormat(bytes).getDimension();
        FilenameAlias specificAlias = original.createSpecific(resolution, dim);
        cloudFileImagesService.uploadImageWithGivenName(bytes, specificAlias.getFileName());
        if(found.getFilenameAlias() == null) {
            found.setFilenameAlias(specificAlias);
        } else {
            found.getFilenameAlias().updateFrom(specificAlias);
        }
    }

    @Transactional(readOnly = true)
    public List<FilenameAlias> findFilenameAliases(Community community) {
        List<BadgeMapping> allDefault = badgeMappingRepository.findAllDefault(community);
        List<FilenameAlias> aliases = new ArrayList<FilenameAlias>();
        for (BadgeMapping badgeMapping : allDefault) {
            aliases.add(badgeMapping.getOriginalFilenameAlias());
        }
        return aliases;
    }

    @Transactional
    public void update(long fileNameAliasId, String newName) {
        logger().info("Updating alias: {} to {}", fileNameAliasId, newName);

        FilenameAlias found = filenameAliasRepository.findOne(fileNameAliasId);
        Assert.notNull(found);
        found.setAlias(newName);
    }

    @Transactional
    public List<FilenameAlias> removeBadge(Community community, long id) {
        FilenameAlias alias = filenameAliasRepository.findOne(id);

        if(alias == null) {
            return Collections.emptyList();
        }

        List<BadgeMapping> mappings = badgeMappingRepository.findByCommunityAndOriginalAlias(community, alias);
        badgeMappingRepository.deleteByCommunityAndOriginalAlias(community, alias);

        return getFilenameAliases(mappings);
    }

    @Transactional
    public void createResolution(Community community, Resolution resolution) {
        resolutionRepository.saveAndFlush(resolution);

        List<BadgeMapping> allDefault = badgeMappingRepository.findAllDefault(community);
        badgeMappingRepository.save(collectMappings(resolution, community, allDefault));
    }

    @Transactional
    public List<FilenameAlias> removeResolution(long id) {
        Resolution resolution = resolutionRepository.findOne(id);

        if(resolution == null) {
            return Collections.emptyList();
        }

        List<BadgeMapping> mappings = badgeMappingRepository.findByResolution(resolution);
        badgeMappingRepository.deleteByResolution(resolution);
        resolutionRepository.delete(resolution);

        return getFilenameAliases(mappings);
    }

    @Transactional(readOnly = true)
    public String getBadgeFileName(long badgeId, Community community, Resolution resolution) {
        List<BadgeMapping> mappings = getBadgeMappings(badgeId, community, resolution);
        Assert.isTrue(!mappings.isEmpty(), "Not found badges for badge id " + badgeId + " and resolution" + resolution);
        return chooseBadgeFileName(mappings);
    }

    public void deleteCloudFileByAlias(FilenameAlias filenameAlias) {
        FilenameAlias alias = filenameAliasRepository.findOne(filenameAlias.getId());
        if(alias != null) {
            filenameAliasRepository.delete(filenameAlias);
        }

        try {
            if(alias != null) {
                cloudFileService.deleteFile(filenameAlias.getFileName());
            }
        } catch (Exception e) {
            logger().warn("Did not succeeded to remove the cloud file: {} on cloud: {}", filenameAlias.getFileName(), cloudFileService.getFilesURL());
        }
    }

    private List<FilenameAlias> getFilenameAliases(List<BadgeMapping> mappings) {
        List<FilenameAlias> aliases = new ArrayList<FilenameAlias>();

        for (BadgeMapping mapping : mappings) {
            FilenameAlias filenameAlias = mapping.getFilenameAlias();
            if(filenameAlias != null) {
                aliases.add(filenameAlias);
            }
        }
        return aliases;
    }

    private List<BadgeMapping> collectMappings(Resolution resolution, Community community, List<BadgeMapping> allDefault) {
        List<BadgeMapping> placeholders = new ArrayList<BadgeMapping>();

        for (BadgeMapping mapping : allDefault) {
            placeholders.add(BadgeMapping.specific(resolution, community, mapping.getOriginalFilenameAlias()));
        }

        return placeholders;

    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

    private List<BadgeMapping> getBadgeMappings(long badgeId, Community community, Resolution resolution) {
        Resolution inDatabase = resolutionRepository.find(resolution.getDeviceType(), resolution.getWidth(), resolution.getHeight());
        if(inDatabase == null) {
            return badgeMappingRepository.findByCommunityAndFilenameId(community, badgeId);
        } else {
            return badgeMappingRepository.findByCommunityResolutionAndFilenameId(community, inDatabase, badgeId);
        }
    }

    private String chooseBadgeFileName(List<BadgeMapping> mappings) {
        // badge is backed by resolution (has specific, assigned for this resolution and community)
        if(mappings.size() == 2) {
            FilenameAlias filenameAlias = mappings.get(0).getFilenameAlias();
            // there is the placeholder
            // specified the the size and server resized the image
            if(filenameAlias != null && filenameAlias.getFileName() != null) {
                return filenameAlias.getFileName();
            } else {
                return mappings.get(1).getFilenameAlias().getFileName();
            }
        }
        // default only (only original)
        return mappings.get(0).getFilenameAlias().getFileName();
    }
}
