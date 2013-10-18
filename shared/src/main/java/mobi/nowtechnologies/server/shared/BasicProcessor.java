package mobi.nowtechnologies.server.shared;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 5:43 PM
 */
public abstract class BasicProcessor<OUT> {
    protected Parser messageParser;
    protected boolean atOnce;

    protected BasicProcessor(boolean atOnce){
        this.atOnce = atOnce;
    }

    public abstract void process(OUT data);

    public void parserAndProcess(final Object data) {
        OUT result;
        try {
            result = (OUT)messageParser.parse(data);
        } catch (ClassCastException e){
            result = (OUT) data;
        }

        process(result);
    }

    public void setMessageParser(Parser<?, OUT> messageParser) {
        this.messageParser = messageParser;
    }

    public BasicProcessor<OUT> withMessageParser(Parser<?, OUT> messageParser) {
        setMessageParser(messageParser);

        return this;
    }

    public boolean isAtOnce() {
        return atOnce;
    }
}
