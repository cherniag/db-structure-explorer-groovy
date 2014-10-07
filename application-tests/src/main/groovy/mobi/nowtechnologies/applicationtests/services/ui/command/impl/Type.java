package mobi.nowtechnologies.applicationtests.services.ui.command.impl;

import mobi.nowtechnologies.applicationtests.services.ui.WebPage;
import mobi.nowtechnologies.applicationtests.services.ui.command.Command;

public class Type implements Command {
    private String value;
    private String selector;

    public Type(String value, String selector) {
        this.value = value;
        this.selector = selector;
    }

    @Override
    public boolean process(WebPage page) {
        page.type(selector, value);

        return true;
    }

    @Override
    public String toString() {
        return "Type[" +value + "] to [" + selector + "]";
    }
}
