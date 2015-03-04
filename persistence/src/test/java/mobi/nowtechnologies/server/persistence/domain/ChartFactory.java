package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.enums.ChartType;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public class ChartFactory {

    public static Chart createChart() {
        Chart chart = new Chart();
        chart.setI(1);
        chart.setType(ChartType.BASIC_CHART);
        chart.setGenre(new Genre());

        return chart;
    }
}
