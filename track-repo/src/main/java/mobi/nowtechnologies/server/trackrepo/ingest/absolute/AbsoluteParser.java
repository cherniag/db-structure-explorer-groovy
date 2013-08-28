package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;

public class AbsoluteParser {

    public Map<String, DropTrack> parse(File file) {
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        if (!file.exists()) return res;

        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(file);
            Element root = document.getRootElement();
            String DISTRIBUTOR = root.getChild("MessageHeader").getChild("MessageSender").getChild("PartyName").getChildText("FullName");
            List<Element> sounds = root.getChild("ResourceList").getChildren("SoundRecording");
            List<Element> releases = root.getChild("ReleaseList").getChildren("Release");
            List<Element> deals = root.getChild("DealList").getChildren("ReleaseDeal");

            for (Element node : sounds) {
                String ISRC = node.getChild("SoundRecordingId").getChildText("ISRC");
                Element details = node.getChild("SoundRecordingDetailsByTerritory");
                String ARTIST = details.getChild("DisplayArtist").getChild("PartyName").getChildText("FullName");
                String TITLE = details.getChild("Title").getChildText("TitleText");
                String SUB_TITLE = details.getChildText("ParentalWarningType");
                String GENRE = details.getChild("Genre").getChildText("GenreText");
                String COPYRIGHT = details.getChild("PLine").getChildText("PLineText");
                String LABEL = details.getChildText("LabelName");
                String YEAR = details.getChild("PLine").getChildText("Year");
                List<DropTerritory> TERRITORIES = createTerritory(details, DISTRIBUTOR, LABEL, ISRC);

                res.put(getDropTrackKey(ISRC), new DropTrack()
                        .addType(INSERT)
                        .addProductCode("")
                        .addTitle(TITLE)
                        .addSubTitle(SUB_TITLE)
                        .addArtist(ARTIST)
                        .addGenre(GENRE)
                        .addCopyright(COPYRIGHT)
                        .addLabel(LABEL)
                        .addYear(YEAR)
                        .addIsrc(ISRC)
                        .addPhysicalProductId(ISRC)
                        .addInfo("")
                        .addExists(true)
                        .addExplicit(false)
                        .addProductId(ISRC)
                        .addTerritories(TERRITORIES)

                );
            }

            for (Element node : releases) {
                String ISRC = node.getChild("ReleaseId").getChildText("ISRC");
                if (ISRC == null) continue;
                String releaseReference = node.getChildText("ReleaseReference");
                DropTrack track = res.get(getDropTrackKey(ISRC));
                String ALBUM = "";//node.getChildText("");

                track.addAlbum(ALBUM);
            }

            for(Element node: deals){
                String reference = node.getChildText("DealReleaseReference");
                String DEAL_REFERENCE = node.getChild("Deal").getChildText("DealReference");
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private List<DropTerritory> createTerritory(Element details, String distributor, String label, String isrc) {
        List<DropTerritory> res = new ArrayList<DropTerritory>();
        List<Element> territoryCode = details.getChildren("TerritoryCode");
        for (Element e : territoryCode)
            res.add(new DropTerritory(e.getText())
                    .addCurrency("GBP")
                    .addDistributor(distributor)
                    .addLabel(label)
                    .addPrice(0.0f)
                    .addPriceCode("0.0")
                    .addPublisher("")
                    .addReportingId(isrc)
            );
        return res;
    }

    private String getDropTrackKey(String ISRC) {
        return Joiner.on('_').join(ISRC, getClass().getSimpleName());
    }
}
