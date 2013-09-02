package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.Parser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;

public class AbsoluteParser implements Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteParser.class);

    public Map<String, DropTrack> loadXml(File file) {
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        if (!file.exists()) return res;

        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(file);
            Element root = document.getRootElement();
            String distributor = root.getChild("MessageHeader").getChild("MessageSender").getChild("PartyName").getChildText("FullName");
            List<Element> sounds = root.getChild("ResourceList").getChildren("SoundRecording");
            List<Element> releases = root.getChild("ReleaseList").getChildren("Release");
            List<Element> deals = root.getChild("DealList").getChildren("ReleaseDeal");

            for (Element node : sounds) {
                String isrc = node.getChild("SoundRecordingId").getChildText("ISRC");
                Element details = node.getChild("SoundRecordingDetailsByTerritory");
                String artist = details.getChild("DisplayArtist").getChild("PartyName").getChildText("FullName");
                String title = details.getChild("Title").getChildText("TitleText");
                String subTitle = details.getChildText("ParentalWarningType");
                String genre = details.getChild("Genre").getChildText("GenreText");
                String copyright = details.getChild("PLine").getChildText("PLineText");
                String label = details.getChildText("LabelName");
                String year = details.getChild("PLine").getChildText("Year");
                List<DropTerritory> territories = createTerritory(details, distributor, label, isrc);

                res.put(getDropTrackKey(isrc), new DropTrack()
                        .addType(INSERT)
                        .addProductCode("")
                        .addTitle(title)
                        .addSubTitle(subTitle)
                        .addArtist(artist)
                        .addGenre(genre)
                        .addCopyright(copyright)
                        .addLabel(label)
                        .addYear(year)
                        .addIsrc(isrc)
                        .addPhysicalProductId(isrc)
                        .addInfo("")
                        .addExists(true)
                        .addExplicit(false)
                        .addProductId(isrc)
                        .addTerritories(territories)
                );
            }

            for (Element node : releases) {
                String isrc = node.getChild("ReleaseId").getChildText("ISRC");
                if (isrc == null) continue;
                String releaseReference = node.getChildText("ReleaseReference");
                DropTrack track = res.get(getDropTrackKey(isrc));
                String album = "";//node.getChildText("");

                track.addAlbum(album);
            }

            for(Element node: deals){
                String reference = node.getChildText("DealReleaseReference");
                String dealReference = node.getChild("Deal").getChildText("DealReference");
            }
        } catch (JDOMException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
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

    private String getDropTrackKey(String isrc) {
        return Joiner.on('_').join(isrc, getClass().getSimpleName());
    }
}
