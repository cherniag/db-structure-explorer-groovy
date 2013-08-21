package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.dom4j.Node;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;

public class AbsoluteParser {

    public Map<String, DropTrack> parse(File file){
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        if(!file.exists()) return res;

        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(file);
            Element root = document.getRootElement();
            List<Element> records = root.getChild("ResourceList").getChildren("SoundRecording");
            for(Element node: records){
                String ISRC = node.getChild("SoundRecordingId").getChildText("ISRC");
                Element details = node.getChild("SoundRecordingDetailsByTerritory");
                String ARTIST = details.getChild("DisplayArtist").getChild("PartyName").getChild("FullName").getText();
                String TITLE = details.getChild("Title").getChild("TitleText").getText();
                String SUB_TITLE = details.getChild("ParentalWarningType").getText();

                res.put(Joiner.on('_').join(ISRC, getClass().getSimpleName()), new DropTrack()
                        .addType(INSERT)
                        .addProductCode("")
                        .addTitle(TITLE)
                        .addSubTitle(SUB_TITLE)
                        .addArtist(ARTIST)
                        .addIsrc(ISRC));
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
