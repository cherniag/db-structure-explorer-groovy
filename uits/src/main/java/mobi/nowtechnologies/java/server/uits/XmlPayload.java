package mobi.nowtechnologies.java.server.uits;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class XmlPayload {

	static final boolean test = false;

	public static String buildPayload(UitsParameters params, String mediaHash) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String date = sdf.format(new Date());
		StringBuffer xmlMeta = new StringBuffer();
		xmlMeta.append("<metadata>");
		if (!test) {
			xmlMeta.append("<nonce>").append(UUID.randomUUID().toString().substring(0, 8)).append("</nonce>");
			xmlMeta.append("<Time>").append(date).append("</Time>");
		} else {
			xmlMeta.append("<nonce>edb551bb</nonce>");
			xmlMeta.append("<Time>2011-10-06T17:10:59Z</Time>");
		}
		xmlMeta.append("<Distributor>").append(params.getDistributor()).append("</Distributor>");
		xmlMeta.append("<ProductID type=\"UPC\" completed=\"true\">").append(params.getProductId()).append("</ProductID>");
		xmlMeta.append("<AssetID type=\"ISRC\">").append(params.getIsrc()).append("</AssetID>");
		// xml.append("<TID version=\"1\">Transaction</TID>");
		xmlMeta.append("<UID version=\"1\">").append(params.getUser()).append("</UID>");
		xmlMeta.append("<Media algorithm=\"SHA256\">").append(mediaHash).append("</Media>");
		// xml.append("<URL type=\"WPUB\">").append(params.getUrl()).append("</URL>");
		// xml.append("<Copyright value=\"allrightsreserved\">").append(params.getCopyright()).append("</Copyright>");
		// xml.append("<Extra type=\"blah\">").append("").append("</Extra>");
		xmlMeta.append("</metadata>");
		String signature = Base64Encoder.encodeToString(Crypto.sign(xmlMeta.toString(), "SHA-256", "RSA", params.getKey()), false);
		StringBuffer xml = new StringBuffer();
		xml
				.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><uits:UITS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:uits=\"http://www.udirector.net/schemas/2009/uits/1.1\">");
		xml.append(xmlMeta);
		xml.append("<signature algorithm=\"RSA2048\" canonicalization=\"none\" keyID=\"33dce5a4f8b67303a290dc5145037569ca38036d\">")
				.append(signature).append("</signature>");
		xml.append("</uits:UITS>");

		String uitsPayloadXML = xml.toString();
		System.out.println(uitsPayloadXML + " " + uitsPayloadXML.length());
		return uitsPayloadXML;

	}

}
