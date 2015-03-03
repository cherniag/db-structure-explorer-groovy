package mobi.nowtechnologies.server.trackrepo.uits;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class XmlPayload {

    public static String buildPayload(UitsParameters params, String mediaHash) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date = sdf.format(new Date());
        StringBuilder xmlMeta = new StringBuilder();
        xmlMeta.append("<metadata>");
        xmlMeta.append("<nonce>").append(UUID.randomUUID().toString().substring(0, 8)).append("</nonce>");
        xmlMeta.append("<Time>").append(date).append("</Time>");
        xmlMeta.append("<Distributor>").append(params.getDistributor()).append("</Distributor>");
        xmlMeta.append("<ProductID type=\"UPC\" completed=\"true\">").append(params.getProductId()).append("</ProductID>");
        xmlMeta.append("<AssetID type=\"ISRC\">").append(params.getIsrc()).append("</AssetID>");
        xmlMeta.append("<UID version=\"1\">").append(params.getUser()).append("</UID>");
        xmlMeta.append("<Media algorithm=\"SHA256\">").append(mediaHash).append("</Media>");
        xmlMeta.append("</metadata>");
        String signature = Base64Encoder.encodeToString(Crypto.sign(xmlMeta.toString(), params.getKey()), false);
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><uits:UITS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:uits=\"http://www.udirector.net/schemas/2009/uits/1.1\">");
        xml.append(xmlMeta);
        xml.append("<signature algorithm=\"RSA2048\" canonicalization=\"none\" keyID=\"33dce5a4f8b67303a290dc5145037569ca38036d\">").append(signature).append("</signature>");
        xml.append("</uits:UITS>");

        return xml.toString();

    }

}
