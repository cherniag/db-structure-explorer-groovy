package mobi.nowtechnologies.server.trackrepo.ingest.mos;

import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;

import java.io.FileNotFoundException;

/**
 * Created by Oleg Artomov on 5/27/2014.
 */
public class MosDDEXParser extends SonyDDEXParser {

    public MosDDEXParser(String root) throws FileNotFoundException {
        super(root);
    }

}
