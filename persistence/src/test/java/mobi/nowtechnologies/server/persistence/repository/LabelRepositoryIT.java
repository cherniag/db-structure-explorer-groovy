package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Label;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by Oleg Artomov on 7/22/2014.
 */
public class LabelRepositoryIT extends AbstractRepositoryIT {

    @Resource
    private LabelRepository labelRepository;

    @Test
    public void testFindByName() {
        List<Label> labels = labelRepository.findAll();
        Label label = labels.get(0);
        Label label1 = labelRepository.findByName(label.getName());
        assertEquals(label.getName(), label1.getName());
    }
}
