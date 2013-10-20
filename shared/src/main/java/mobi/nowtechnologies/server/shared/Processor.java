package mobi.nowtechnologies.server.shared;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/4/13
 * Time: 5:43 PM
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
}
