package mobi.nowtechnologies.server.persistence.domain;



public class ChartDetailFactory
 {
	public static ChartDetail createChartDetail() {
		Chart chart = ChartFactory.createChart();
		ChartDetail chartDetail = new ChartDetail();
		chartDetail.setI((int)(Math.random()*100));
		chartDetail.setChart(chart);
		chartDetail.setSubtitle("subtitle");
		chartDetail.setTitle("title");
		chartDetail.setInfo("desc");
		chartDetail.setPosition((byte)1);
		chartDetail.setImageFileName("imageFilename");
		chartDetail.setImageTitle("imageTitle");
		
		return chartDetail;
	}
}