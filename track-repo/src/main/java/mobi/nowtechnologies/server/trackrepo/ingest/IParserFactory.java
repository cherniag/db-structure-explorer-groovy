package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.ci.CiParser;
import mobi.nowtechnologies.server.trackrepo.ingest.emi.EmiParser;
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
    private String iodaRoot;
    private String ciRoot;
    private String manualRoot;
    private String warnerRoot;
    private String sonyDDEXRoot;

    public IParser getParser(Ingestors name) {
        if (Ingestors.SONY == name) {
            return new SonyParser(sonyRoot);
        } else if (Ingestors.WARNER_OLD == name) {
            return new WarnerParser(warnerOldRoot);
        } else if (Ingestors.UNIVERSAL == name) {
            return new UniversalParser(universalRoot);
        } else if (Ingestors.FUGA == name) {
            return new FugaParser(fugaRoot);
        } else if (Ingestors.EMI == name) {
            return new EmiParser(emiRoot);
        } else if (Ingestors.IODA == name) {
            return new IodaParser(iodaRoot);
        } else if (Ingestors.CI == name) {
            return new CiParser(ciRoot);
        } else if (Ingestors.MANUAL == name) {
            return new ManualParser(manualRoot);
        } else if (Ingestors.WARNER == name) {
            return new WarnerParserV34(warnerRoot);
        } else if (Ingestors.SONY_DDEX == name) {
            return new SonyDDEXParser(sonyDDEXRoot);
        }
        return null;
    }

    ;

    public String getName(Ingestors name) {
        if (Ingestors.SONY_DDEX == name) {
            return Ingestors.SONY.name();
        } else if (Ingestors.WARNER == name) {
            return Ingestors.WARNER.name();
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

    public enum Ingestors {SONY, WARNER_OLD, WARNER, FUGA, UNIVERSAL, EMI, IODA, CI, MANUAL, SONY_DDEX}
}