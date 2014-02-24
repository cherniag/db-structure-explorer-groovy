package mobi.nowtechnologies.server.trackrepo.ingest;

import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.ABSOLUTE;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.CI;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.EMI;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.EMI_UMG;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.FUGA;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.IODA;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.MANUAL;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.SONY;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.SONY_DDEX;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.UNIVERSAL;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.WARNER;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.WARNER_OLD;

import java.io.FileNotFoundException;

import mobi.nowtechnologies.server.trackrepo.ingest.absolute.AbsoluteParser;
import mobi.nowtechnologies.server.trackrepo.ingest.ci.CiParser;
import mobi.nowtechnologies.server.trackrepo.ingest.emi.EmiParser;
import mobi.nowtechnologies.server.trackrepo.ingest.emi.EmiUmgParser;
import mobi.nowtechnologies.server.trackrepo.ingest.fuga.FugaParser;
import mobi.nowtechnologies.server.trackrepo.ingest.ioda.IodaParser;
import mobi.nowtechnologies.server.trackrepo.ingest.manual.ManualParser;
import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyParser;
import mobi.nowtechnologies.server.trackrepo.ingest.universal.UniversalParser;
import mobi.nowtechnologies.server.trackrepo.ingest.warner.WarnerParser;
import mobi.nowtechnologies.server.trackrepo.ingest.warner.WarnerParserV34;

public class IParserFactory {
    private String sonyRoot;
    private String warnerOldRoot;
    private String universalRoot;
    private String fugaRoot;
    private String emiRoot;
    private String emiUmgRoot;
    private String iodaRoot;
    private String ciRoot;
    private String manualRoot;
    private String warnerRoot;
    private String sonyDDEXRoot;
    private String absoluteRoot;

    public IParser getParser(Ingestors name) throws FileNotFoundException {
        if (SONY == name) {
            return new SonyParser(sonyRoot);
        } else if (WARNER_OLD == name) {
            return new WarnerParser(warnerOldRoot);
        } else if (UNIVERSAL == name) {
            return new UniversalParser(universalRoot);
        } else if (FUGA == name) {
            return new FugaParser(fugaRoot);
        } else if (EMI == name) {
            return new EmiParser(emiRoot);
        } else if (EMI_UMG == name) {
            return new EmiUmgParser(emiUmgRoot);
        } else if (IODA == name) {
            return new IodaParser(iodaRoot);
        } else if (CI == name) {
            return new CiParser(ciRoot);
        } else if (MANUAL == name) {
            return new ManualParser(manualRoot);
        } else if (WARNER == name) {
            return new WarnerParserV34(warnerRoot);
        } else if (SONY_DDEX == name) {
            return new SonyDDEXParser(sonyDDEXRoot);
        } else if (ABSOLUTE == name){
            return new AbsoluteParser(absoluteRoot);
        }
        return null;
    }

    ;

    public String getName(Ingestors name) {
        if (SONY_DDEX == name) {
            return SONY.name();
        } else if (WARNER_OLD == name) {
            return WARNER.name();
        }
        return name.name();
    }

    public void setSonyRoot(String sonyRoot) {
        this.sonyRoot = sonyRoot;
    }

    public void setWarnerOldRoot(String warnerOldRoot) {
        this.warnerOldRoot = warnerOldRoot;
    }

    public void setUniversalRoot(String universalRoot) {
        this.universalRoot = universalRoot;
    }

    public void setFugaRoot(String fugaRoot) {
        this.fugaRoot = fugaRoot;
    }

    public void setEmiRoot(String emiRoot) {
        this.emiRoot = emiRoot;
    }
    
	public void setEmiUmgRoot(String emiUmgRoot) {
		this.emiUmgRoot = emiUmgRoot;
	}

    public void setIodaRoot(String iodaRoot) {
        this.iodaRoot = iodaRoot;
    }

    public void setCiRoot(String ciRoot) {
        this.ciRoot = ciRoot;
    }

    public void setManualRoot(String manualRoot) {
        this.manualRoot = manualRoot;
    }

    public void setWarnerRoot(String warnerRoot) {
        this.warnerRoot = warnerRoot;
    }

    public void setSonyDDEXRoot(String sonyDDEXRoot) {
        this.sonyDDEXRoot = sonyDDEXRoot;
    }

    public void setAbsoluteRoot(String absoluteRoot) {
        this.absoluteRoot = absoluteRoot;
    }

    public enum Ingestors {SONY, WARNER_OLD, WARNER, FUGA, UNIVERSAL, EMI, EMI_UMG, IODA, CI, MANUAL, SONY_DDEX, ABSOLUTE}

}