package mobi.nowtechnologies.server.persistence.domain;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ChartFactory
 {

	public static Chart createChart() {
		final Chart chart = new Chart();
		
		chart.setI(Byte.MIN_VALUE);
		chart.setImageFileName("imageFileName");
		chart.setName("name");
		chart.setNumBonusTracks(Byte.MIN_VALUE);
		chart.setNumTracks(Byte.MIN_VALUE);
		chart.setSubtitle("subtitle");
		chart.setTimestamp(Integer.MIN_VALUE);
		
		return chart;
	}
}