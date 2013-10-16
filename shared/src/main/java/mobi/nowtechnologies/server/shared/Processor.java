package mobi.nowtechnologies.server.shared;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 5:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Processor<T> {
    protected Parser<String, T> messageParser;

    public abstract void process(T data);

    public Parser<String, T> getMessageParser() {
        return messageParser;
    }

    public void setMessageParser(Parser<String, T> messageParser) {
        this.messageParser = messageParser;
    }

    public Processor<T> withMessageParser(Parser<String, T> messageParser) {
        setMessageParser(messageParser);

        return this;
    }
}
