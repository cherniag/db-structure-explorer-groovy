package mobi.nowtechnologies.server.persistence.repository;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BadgeMappingRepositoryIT extends AbstractRepositoryIT {
    @Resource
    BadgeMappingRepository badgeMappingRepository;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    FilenameAliasRepository filenameAliasRepository;
    @Resource
    ResolutionRepository resolutionRepository;

    @Test
    public void testSaveGeneral() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);
        String fileNameInCloud = "name";

        FilenameAlias generalAlias = new FilenameAlias(fileNameInCloud, "title", 12, 10).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias);

        BadgeMapping generalMapping = BadgeMapping.general(anyCommmunity, generalAlias);
        badgeMappingRepository.saveAndFlush(generalMapping);
    }


    @Test
    public void testSaveSpecific() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);
        Resolution resolution = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 5, 5));

        FilenameAlias notTransientGeneral = new FilenameAlias("name-general", "title", 12, 10).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(notTransientGeneral);

        BadgeMapping generalMapping = BadgeMapping.specific(resolution, anyCommmunity, notTransientGeneral);
        badgeMappingRepository.saveAndFlush(generalMapping);
    }

    @Test
    public void testFindByCommunityAndDeviceTypes() throws Exception {
        FilenameAlias alias = new FilenameAlias("file name in cloud", "title", 12, 12).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(alias);

        Community commmunity = communityRepository.findAll().get(0);

        Resolution a1 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 1, 1));
        Resolution a2 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 2, 2));
        Resolution a3 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 3, 3));
        Resolution ios = resolutionRepository.saveAndFlush(new Resolution("IOS", 4, 4));

        BadgeMapping m1 = BadgeMapping.specific(a1, commmunity, alias);
        BadgeMapping m2 = BadgeMapping.specific(a2, commmunity, alias);
        BadgeMapping m3 = BadgeMapping.specific(a3, commmunity, alias);
        BadgeMapping m4 = BadgeMapping.specific(ios, commmunity, alias);

        m1.setFilenameAlias(new FilenameAlias("resized for a1", "title 1", 1, 1).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        m2.setFilenameAlias(new FilenameAlias("resized for a2", "title 2", 2, 2).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        m3.setFilenameAlias(new FilenameAlias("resized for a3", "title 3", 3, 3).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        m4.setFilenameAlias(new FilenameAlias("resized for ios","title 4", 4, 4).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));

        badgeMappingRepository.saveAndFlush(m1);
        badgeMappingRepository.saveAndFlush(m2);
        badgeMappingRepository.saveAndFlush(m3);
        badgeMappingRepository.saveAndFlush(m4);

        List<BadgeMapping> androidBadges = badgeMappingRepository.findByCommunityAndDeviceType(commmunity, "ANDROID", alias.getId());

        assertEquals(3, androidBadges.size());

        assertEquals(m1.getFilenameAlias().getId(), androidBadges.get(0).getFilenameAlias().getId());
        assertEquals(m2.getFilenameAlias().getId(), androidBadges.get(1).getFilenameAlias().getId());
        assertEquals(m3.getFilenameAlias().getId(), androidBadges.get(2).getFilenameAlias().getId());
    }

    @Test
    public void testFindAllDefault() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);

        FilenameAlias generalAlias1 = new FilenameAlias("name-general-1", "title", 5, 5).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias1);
        BadgeMapping m0 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommmunity, generalAlias1));

        // needed for sorting by date
        sleep(200);

        FilenameAlias generalAlias2 = new FilenameAlias("name-general-2", "title", 6, 6).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias2);
        BadgeMapping m1 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommmunity, generalAlias2));

        // needed for sorting by date
        sleep(200);
        FilenameAlias generalAlias3 = new FilenameAlias("name-general-3", "title", 7, 7).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias3);
        BadgeMapping m2 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommmunity, generalAlias3));

        List<BadgeMapping> list = badgeMappingRepository.findAllDefault(anyCommmunity);
        assertEquals(m2.getId(), list.get(0).getId());
        assertEquals(m1.getId(), list.get(1).getId());
        assertEquals(m0.getId(), list.get(2).getId());

    }

    @Test
    public void testFindByCommunityResolutionAndOriginalAlias() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);
        FilenameAlias generalAlias = new FilenameAlias("name-general-1", "title", 5, 5).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias);

        Resolution r0 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 1, 1));
        Resolution r1 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 2, 2));

        badgeMappingRepository.save(BadgeMapping.specific(r0, anyCommmunity, generalAlias));
        badgeMappingRepository.save(BadgeMapping.specific(r1, anyCommmunity, generalAlias));

        BadgeMapping byCommunityResolutionAndOriginalAlias = badgeMappingRepository.findByCommunityResolutionAndOriginalAlias(anyCommmunity, r0, generalAlias);

        assertEquals(r0.getId(), byCommunityResolutionAndOriginalAlias.getResolution().getId());
    }

    @Test
    public void testFindByResolution() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(1);

        FilenameAlias generalAlias = new FilenameAlias("name-general-1", "title", 5, 5).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias);

        Resolution r0 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 1, 1));
        Resolution r1 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 2, 2));

        // expected to fetch:
        badgeMappingRepository.save(BadgeMapping.specific(r0, anyCommmunity, generalAlias));
        // not expected
        badgeMappingRepository.save(BadgeMapping.specific(r1, anyCommmunity, generalAlias));

        List<BadgeMapping> found = badgeMappingRepository.findByResolution(r0);

        assertEquals(anyCommmunity.getId(), found.get(0).getCommunity().getId());
    }

    @Test
    public void testFindByCommunityAndOriginalAlias() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(1);

        FilenameAlias generalAlias = new FilenameAlias("name-general-1", "title", 5, 5).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias);

        Resolution resolution1 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 2, 3));
        Resolution resolution2 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 3, 2));

        badgeMappingRepository.save(BadgeMapping.general(anyCommmunity, generalAlias));
        badgeMappingRepository.save(BadgeMapping.specific(resolution1, anyCommmunity, generalAlias));
        badgeMappingRepository.save(BadgeMapping.specific(resolution2, anyCommmunity, generalAlias));

        List<BadgeMapping> found = badgeMappingRepository.findByCommunityAndOriginalAlias(anyCommmunity, generalAlias);

        assertEquals(3, found.size());

        List<BadgeMapping> generals = new ArrayList<BadgeMapping>();
        List<BadgeMapping> specific = new ArrayList<BadgeMapping>();
        for (BadgeMapping badgeMapping : found) {
            if(badgeMapping.getResolution() == null) {
                generals.add(badgeMapping);
            } else {
                specific.add(badgeMapping);
            }
        }

        assertEquals(1, generals.size());
        assertEquals(2, specific.size());

        List<Resolution> resolutions = Lists.newArrayList(resolution1, resolution2);

        assertTrue(resolutions.contains(specific.get(0).getResolution()));

        resolutions.remove(specific.get(0).getResolution());
        assertTrue(resolutions.contains(specific.get(1).getResolution()));

    }

}