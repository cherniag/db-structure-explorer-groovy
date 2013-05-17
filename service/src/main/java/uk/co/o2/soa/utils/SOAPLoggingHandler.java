package uk.co.o2.soa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.util.Set;


public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityHandler.class);

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        logToSystemOut(smc);
        return true;
    }

    public void close(MessageContext messageContext) { }

    private void logToSystemOut(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty.booleanValue()) {
            LOG.trace("\nOutbound message:");
        } else {
            LOG.trace("\nInbound message:");
        }

        SOAPMessage message = smc.getMessage();
        try {
            LOG.trace(toString(message));
        } catch (Exception e) {
            LOG.trace("Exception in SOAPLoggingHandler: " + e);
        }
    }

    public String toString(SOAPMessage message) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            return out.toString();
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
        return String.valueOf(message);
    }
}