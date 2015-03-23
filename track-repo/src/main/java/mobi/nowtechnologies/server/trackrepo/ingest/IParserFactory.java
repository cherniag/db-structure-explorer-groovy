package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.ci.CiParser;
import mobi.nowtechnologies.server.trackrepo.ingest.emi.EmiParser;
import mobi.nowtechnologies.server.trackrepo.ingest.emi.EmiUmgParser;
import mobi.nowtechnologies.server.trackrepo.ingest.fuga.FugaParser;
import mobi.nowtechnologies.server.trackrepo.ingest.ioda.IodaParser;
import mobi.nowtechnologies.server.trackrepo.ingest.manual.ManualParser;
import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyParser;
import mobi.nowtechnologies.server.trackrepo.ingest.universal.UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13;
import mobi.nowtechnologies.server.trackrepo.ingest.universal.UniversalParser;
import mobi.nowtechnologies.server.trackrepo.ingest.warner.WarnerParser;
import mobi.nowtechnologies.server.trackrepo.ingest.warner.WarnerParserV34;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.EMI_UMG;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.SONY;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.SONY_DDEX;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.UNIVERSAL;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.WARNER;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.WARNER_OLD;

import java.io.FileNotFoundException;

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
    private String mosRoot;
    private String universalDDEX_V3_7_AssetAndMetaData_V1_13_Root;

    public IParser getParser(Ingestor ingestor) throws FileNotFoundException {
        switch (ingestor) {
            case SONY:
                return new SonyParser(sonyRoot);
            case WARNER_OLD:
                return new WarnerParser(warnerOldRoot);
            case UNIVERSAL:
                return new UniversalParser(universalRoot);
            case FUGA:
                return new FugaParser(fugaRoot);
            case EMI:
                return new EmiParser(emiRoot);
            case EMI_UMG:
                return new EmiUmgParser(emiUmgRoot);
            case IODA:
                return new IodaParser(iodaRoot);
            case CI:
                return new CiParser(ciRoot);
            case MANUAL:
                return new ManualParser(manualRoot);
            case WARNER:
                return new WarnerParserV34(warnerRoot);
            case SONY_DDEX:
                return new SonyDDEXParser(sonyDDEXRoot);
            case MOS:
                return new SonyDDEXParser(mosRoot);
            case UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13:
                return new UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13(universalDDEX_V3_7_AssetAndMetaData_V1_13_Root);
            default:
                return null;
        }
    }

    public String getName(Ingestor name) {
        if (SONY_DDEX == name) {
            return SONY.name();
        } else if (WARNER_OLD == name) {
            return WARNER.name();
        } else if (EMI_UMG == name) {
            return UNIVERSAL.name();
        } else if(Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13 == name){
            return UNIVERSAL.name();
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

    public void setMosRoot(String mosRoot) {
        this.mosRoot = mosRoot;
    }

    public void setUniversalDDEX_V3_7_AssetAndMetaData_V1_13_Root(String universalDDEX_V3_7_AssetAndMetaData_V1_13_Root) {
        this.universalDDEX_V3_7_AssetAndMetaData_V1_13_Root = universalDDEX_V3_7_AssetAndMetaData_V1_13_Root;
    }
}