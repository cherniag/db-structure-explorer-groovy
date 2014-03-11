package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.shared.enums.ChartType;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by oar on 3/11/14.
 */
public class ViewTypesIT extends AbstractAdminITTest {

    @Resource
    private Map<String, String> chartViewByChartType;

    @Resource
    private Map<String, String> chartItemsViewByChartType;


    @Test
    public void checkChartView(){
        check(chartViewByChartType);
    }


    @Test
    public void checkChartItemsView(){
        check(chartItemsViewByChartType);
    }

    private void check(Map<String, String> mappingInContext) {
        for (ChartType currentType: ChartType.values()){
            Assert.isTrue(mappingInContext.containsKey(currentType.name()));
        }
    }

}
