package mobi.nowtechnologies.server.dto.asm;

import mobi.nowtechnologies.server.dto.CommunityDto;
import mobi.nowtechnologies.server.persistence.domain.Community;

import java.util.List;
import static java.util.Arrays.asList;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CommunityDtoAsmTest {

    CommunityDtoAsm communityDtoAsm = new CommunityDtoAsm();

    @Test
    public void shouldConvertToCommunityDtos() {
        //given
        List<Community> communities = asList(new Community().withRewriteUrl("o2").withLive(true), new Community().withRewriteUrl("mtv1").withLive(false));

        //when
        List<CommunityDto> communityDtos = communityDtoAsm.toCommunityDtos(communities);

        //then
        assertThat(communityDtos.size(), is(communities.size()));
        for (int i = 0; i < communities.size(); i++) {
            assertThat(communityDtos.get(i).getUrl(), is(communities.get(i).getRewriteUrlParameter()));
            assertThat(communityDtos.get(i).isActive(), is(communities.get(i).isLive()));
        }
    }

    @Test
    public void shouldConvertToCommunityDto() {
        //given
        Community community = new Community().withRewriteUrl("o2").withLive(true);

        //when
        CommunityDto communityDto = communityDtoAsm.toCommunityDto(community);

        //then
        assertThat(communityDto.getUrl(), is(community.getRewriteUrlParameter()));
        assertThat(communityDto.isActive(), is(community.isLive()));
    }
}