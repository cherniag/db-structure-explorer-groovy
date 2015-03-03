package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.shared.enums.ChartType;

import javax.annotation.Resource;

import java.util.Map;

import org.springframework.util.Assert;

import org.junit.*;

/**
 * Created by oar on 3/11/14.
 */
public class ViewTypesIT extends AbstractAdminITTest {

    @Resource
    private Map<ChartType, String> chartViewByChartType;

    @Resource
    private Map<ChartType, String> chartItemsViewByChartType;


    @Test
    public void checkChartView() {
        check(chartViewByChartType);
    }


    @Test
    public void checkChartItemsView() {
        check(chartItemsViewByChartType);
    }

    private void check(Map<ChartType, String> mappingInContext) {
        for (ChartType currentType : ChartType.values()) {
            Assert.isTrue(mappingInContext.containsKey(currentType));
        }
    }

}
