package mobi.nowtechnologies.server.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 5:43 PM
 */
public abstract class BasicProcessor<OUT> implements Processor<OUT>{
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    protected Parser messageParser;

    public void parserAndProcess(final Object data) {
        OUT result;
        try {
            result = messageParser != null ? (OUT)messageParser.parse(data) : (OUT)data;
        } catch (ClassCastException e){
            result = (OUT) data;
        }

        try{
            process(result);
        } catch (ClassCastException e){
            LOGGER.warn("Data "+data+" can't be processed by "+this.getClass(), e);
        }
    }

    public void setMessageParser(Parser<?, OUT> messageParser) {
        this.messageParser = messageParser;
    }

    public BasicProcessor<OUT> withMessageParser(Parser<?, OUT> messageParser) {
        setMessageParser(messageParser);

        return this;
    }
}
