package mobi.nowtechnologies.ingestors;

import mobi.nowtechnologies.ingestors.ci.CiParser;
import mobi.nowtechnologies.ingestors.emi.EmiParser;
import mobi.nowtechnologies.ingestors.fuga.FugaParser;
import mobi.nowtechnologies.ingestors.ioda.IodaParser;
import mobi.nowtechnologies.ingestors.manual.ManualParser;
import mobi.nowtechnologies.ingestors.sony.SonyDDEXParser;
import mobi.nowtechnologies.ingestors.sony.SonyParser;
import mobi.nowtechnologies.ingestors.universal.UniversalParser;
import mobi.nowtechnologies.ingestors.warner.WarnerParser;
import mobi.nowtechnologies.ingestors.warner.WarnerParserV34;

public class IParserFactory {
	
	public enum Ingestors  {SONY, WARNER_OLD, WARNER, FUGA, UNIVERSAL, EMI, IODA, CI, MANUAL, SONY_DDEX};

	
	public static IParser getParser(Ingestors name) {
		if (Ingestors.SONY == name) {
			return new SonyParser();
		} else if (Ingestors.WARNER_OLD == name) {
			return new WarnerParser();
		} else if (Ingestors.UNIVERSAL == name) {
			return new UniversalParser();
		} else if (Ingestors.FUGA == name) {
			return new FugaParser();
		} else if (Ingestors.EMI == name) {
			return new EmiParser();
		} else if (Ingestors.IODA == name) {
			return new IodaParser();
		} else if (Ingestors.CI == name) {
			return new CiParser();
		} else if (Ingestors.MANUAL == name) {
			return new ManualParser();
		} 
		else if (Ingestors.WARNER == name) {
			return new WarnerParserV34();
		} 
		else if (Ingestors.SONY_DDEX == name) {
			return new SonyDDEXParser();
		} 
		return null;
	}
	
	public static String getName(Ingestors name) {
		if (Ingestors.SONY_DDEX == name) {
			return Ingestors.SONY.name();
		} else if (Ingestors.WARNER == name) {
			return Ingestors.WARNER.name();
		} 
		return name.name();

	}


}
