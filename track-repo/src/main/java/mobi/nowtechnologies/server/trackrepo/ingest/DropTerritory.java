package mobi.nowtechnologies.server.trackrepo.ingest;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DropTerritory {
	public String country;
	public String label;
	public String currency;
	public Float price;
	public Date startdate;
	public String reportingId;
	public String distributor;
	public boolean takeDown;
	public String priceCode;
	public String dealReference;
	public String publisher;
	
	public static DropTerritory getTerritory(String country, List<DropTerritory> territories) {
		DropTerritory territoryData = null;
		for (DropTerritory territory:territories) {
			if (country.equals(territory.country)) {
				territoryData = territory;
			}
		}
		if (territoryData == null) {
			territoryData = new DropTerritory();
			territories.add(territoryData);
		}
		return territoryData;
	}
	
	public static List<DropTerritory> copyList(List<DropTerritory> list) {
		List<DropTerritory> result = new ArrayList<DropTerritory>(list.size());
		for (DropTerritory territory : list) {
			DropTerritory newTerritory = new DropTerritory();
			result.add(newTerritory);
			newTerritory.country = territory.country;
			newTerritory.label = territory.label;
			newTerritory.currency = territory.currency;
			newTerritory.price = territory.price;
			newTerritory.startdate = territory.startdate;
			newTerritory.reportingId = territory.reportingId;
			newTerritory.distributor = territory.distributor;
			newTerritory.takeDown = territory.takeDown;
			newTerritory.priceCode = territory.priceCode;
			newTerritory.dealReference = territory.dealReference;
			newTerritory.publisher = territory.publisher;

			
		}
		return result;
	}

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("country", country)
                .append("label", label)
                .append("currency", currency)
                .append("price", price)
                .append("startdate", startdate)
                .append("reportingId", reportingId)
                .append("distributor", distributor)
                .append("takeDown", takeDown)
                .append("priceCode", priceCode)
                .append("dealReference", dealReference)
                .append("publisher", publisher)
                .toString();
    }
}
