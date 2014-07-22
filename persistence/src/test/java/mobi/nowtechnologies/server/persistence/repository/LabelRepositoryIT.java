package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Label;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Oleg Artomov on 7/22/2014.
 */
public class LabelRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private LabelRepository labelRepository;

    @Test
    public void testFindByName() {
        Label label = new Label();
        label.setName("Sony");
        labelRepository.saveAndFlush(label);
        Label result = labelRepository.findByName(label.getName());
        assertNotNull(result);
    }
}
