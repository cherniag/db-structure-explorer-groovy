package uk.co.o2.soa.utils;

import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

public final class SecurityHandler implements SOAPHandler<SOAPMessageContext> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SecurityHandler.class);

    private String username;
    private String password;


    public SecurityHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean handleMessage(final SOAPMessageContext msgCtx) {

        final Boolean outInd = (Boolean) msgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outInd.booleanValue()) {
            try {
                final SOAPEnvelope envelope = msgCtx.getMessage().getSOAPPart().getEnvelope();

                SOAPHeader header = envelope.getHeader();
                if (header == null)
                    header = envelope.addHeader();

                String prefix = "wsse";
                final SOAPElement security = header.addChildElement("Security", prefix,
                        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                final SOAPElement userToken = security.addChildElement("UsernameToken", prefix);
                userToken.addChildElement("Username", prefix).addTextNode(username);
                userToken.addChildElement("Password", prefix).addTextNode(password);

            } catch (final Exception e) {
                LOG.error("Can not put username/password for O2 SOAP message.", e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) { }


    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}