package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Dimensions;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Thread.sleep;

import com.google.common.collect.Lists;

import org.junit.*;
import static org.junit.Assert.*;

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

        FilenameAlias generalAlias = new FilenameAlias(fileNameInCloud, "title", new Dimensions(12, 10)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias);

        BadgeMapping generalMapping = BadgeMapping.general(anyCommmunity, generalAlias);
        badgeMappingRepository.saveAndFlush(generalMapping);
    }


    @Test
    public void testSaveSpecific() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);
        Resolution resolution = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 5, 5));

        FilenameAlias notTransientGeneral = new FilenameAlias("name-general", "title", new Dimensions(12, 10)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(notTransientGeneral);

        BadgeMapping generalMapping = BadgeMapping.specific(resolution, anyCommmunity, notTransientGeneral);
        badgeMappingRepository.saveAndFlush(generalMapping);
    }

    @Test
    public void testFindByCommunityResolutionAndDeviceTypes() throws Exception {
        Community commmunity = communityRepository.findAll().get(0);

        // general
        FilenameAlias originalAlias = new FilenameAlias("file name in cloud", "title", new Dimensions(12, 12)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(originalAlias);
        //
        // resolutions
        //
        Resolution r1 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 1, 1));
        Resolution r2 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 2, 2));
        Resolution r3 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 3, 3));
        Resolution rIos = resolutionRepository.saveAndFlush(new Resolution("IOS", 4, 4));
        //
        // general mappings
        //
        BadgeMapping originalMapping = BadgeMapping.general(commmunity, originalAlias);
        badgeMappingRepository.saveAndFlush(originalMapping);

        //
        // specific mappings
        //
        BadgeMapping mSpec1 = BadgeMapping.specific(r1, commmunity, originalAlias);
        BadgeMapping mSpec2 = BadgeMapping.specific(r2, commmunity, originalAlias);
        BadgeMapping mSpec3 = BadgeMapping.specific(r3, commmunity, originalAlias);
        BadgeMapping mSpec4 = BadgeMapping.specific(rIos, commmunity, originalAlias);
        mSpec1.setFilenameAlias(new FilenameAlias("resized for r1", "title 1", new Dimensions(1, 1)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        mSpec2.setFilenameAlias(new FilenameAlias("resized for a2", "title 2", new Dimensions(2, 2)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        mSpec3.setFilenameAlias(new FilenameAlias("resized for a3", "title 3", new Dimensions(3, 3)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        mSpec4.setFilenameAlias(new FilenameAlias("resized for rIos", "title 4", new Dimensions(4, 4)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        badgeMappingRepository.saveAndFlush(mSpec1);
        badgeMappingRepository.saveAndFlush(mSpec2);
        badgeMappingRepository.saveAndFlush(mSpec3);
        badgeMappingRepository.saveAndFlush(mSpec4);

        List<BadgeMapping> androidBadges = badgeMappingRepository.findByCommunityResolutionAndFilenameId(commmunity, r1, mSpec1.getOriginalFilenameAlias().getId());
        assertEquals(2, androidBadges.size());

        // general has equal orig and spec badge ids
        assertEquals(androidBadges.get(1).getFilenameAlias().getId(), androidBadges.get(1).getOriginalFilenameAlias().getId());

        assertEquals(originalMapping.getOriginalFilenameAlias().getId(), androidBadges.get(0).getOriginalFilenameAlias().getId());
        assertEquals(mSpec1.getFilenameAlias().getId(), androidBadges.get(0).getFilenameAlias().getId());
    }

    @Test
    public void testFindByCommunityAndDeviceTypes() throws Exception {
        Community commmunity = communityRepository.findAll().get(0);

        // general
        FilenameAlias originalAlias = new FilenameAlias("file name in cloud", "title", new Dimensions(12, 12)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(originalAlias);
        //
        // resolutions
        //
        Resolution r1 = resolutionRepository.saveAndFlush(new Resolution("ANDROID", 1, 1));
        //
        // general mappings
        //
        BadgeMapping originalMapping = BadgeMapping.general(commmunity, originalAlias);
        badgeMappingRepository.saveAndFlush(originalMapping);

        //
        // specific mappings
        //
        BadgeMapping mSpec1 = BadgeMapping.specific(r1, commmunity, originalAlias);
        mSpec1.setFilenameAlias(new FilenameAlias("resized for r1", "title 1", new Dimensions(1, 1)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES));
        badgeMappingRepository.saveAndFlush(mSpec1);

        List<BadgeMapping> androidBadges = badgeMappingRepository.findByCommunityAndFilenameId(commmunity, mSpec1.getOriginalFilenameAlias().getId());
        assertEquals(1, androidBadges.size());
        assertEquals(originalMapping.getFilenameAlias().getId(), androidBadges.get(0).getFilenameAlias().getId());
    }

    @Test
    public void testFindAllDefaultWithCommunity() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);

        FilenameAlias generalAlias1 = new FilenameAlias("name-general-1", "title", new Dimensions(5, 5)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias1);
        BadgeMapping m0 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommmunity, generalAlias1));

        // needed for sorting by date
        sleep(200);

        FilenameAlias generalAlias2 = new FilenameAlias("name-general-2", "title", new Dimensions(6, 6)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias2);
        BadgeMapping m1 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommmunity, generalAlias2));

        // needed for sorting by date
        sleep(200);
        FilenameAlias generalAlias3 = new FilenameAlias("name-general-3", "title", new Dimensions(7, 7)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias3);
        BadgeMapping m2 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommmunity, generalAlias3));

        List<BadgeMapping> list = badgeMappingRepository.findAllDefault(anyCommmunity);
        assertEquals(m2.getId(), list.get(0).getId());
        assertEquals(m1.getId(), list.get(1).getId());
        assertEquals(m0.getId(), list.get(2).getId());

    }

    @Test
    public void testFindAllDefaultWithoutCommunity() throws Exception {
        Community anyCommunity1 = communityRepository.findAll().get(0);
        FilenameAlias generalAlias1 = new FilenameAlias("name-general-1", "title1", new Dimensions(5, 5)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias1);
        BadgeMapping m0 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommunity1, generalAlias1));

        Community anyCommunity2 = communityRepository.findAll().get(0);
        FilenameAlias generalAlias2 = new FilenameAlias("name-general-2", "title2", new Dimensions(6, 6)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias2);
        BadgeMapping m1 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommunity2, generalAlias2));

        FilenameAlias generalAlias3 = new FilenameAlias("name-general-3", "title3", new Dimensions(7, 7)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
        filenameAliasRepository.saveAndFlush(generalAlias3);
        BadgeMapping m2 = badgeMappingRepository.saveAndFlush(BadgeMapping.general(anyCommunity2, generalAlias3));


        List<BadgeMapping> list = badgeMappingRepository.findAllDefault();
        assertEquals(m0.getId(), list.get(0).getId());
        assertEquals(m1.getId(), list.get(1).getId());
        assertEquals(m2.getId(), list.get(2).getId());

    }

    @Test
    public void testFindByCommunityResolutionAndOriginalAlias() throws Exception {
        Community anyCommmunity = communityRepository.findAll().get(0);
        FilenameAlias generalAlias = new FilenameAlias("name-general-1", "title", new Dimensions(5, 5)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
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

        FilenameAlias generalAlias = new FilenameAlias("name-general-1", "title", new Dimensions(5, 5)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
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

        FilenameAlias generalAlias = new FilenameAlias("name-general-1", "title", new Dimensions(5, 5)).forDomain(FilenameAlias.Domain.HEY_LIST_BADGES);
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
            if (badgeMapping.getResolution() == null) {
                generals.add(badgeMapping);
            }
            else {
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
