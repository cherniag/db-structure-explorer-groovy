package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

import org.junit.*;

/**
 * @author Titov Mykhaylo (titov)
 */
public class FilterWithCriteriaRepositoryIT extends AbstractRepositoryIT {

    @Resource(name = "filterWithCriteriaRepository")
    private FilterWithCriteriaRepository filterWithCriteriaRepository;

    @Test
    public void testFindByNames() throws Exception {
        List<String> names = Arrays.asList("LAST_TRIAL_DAY", "NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS");
        List<AbstractFilterWithCtiteria> abstractFilterWithCtiterias = filterWithCriteriaRepository.findByNames(names);
        Assert.notEmpty(abstractFilterWithCtiterias);
    }

}