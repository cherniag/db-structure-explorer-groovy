package mobi.nowtechnologies.applicationtests.services.ui.command.impl;

import mobi.nowtechnologies.applicationtests.services.ui.WebPage;
import mobi.nowtechnologies.applicationtests.services.ui.command.Command;

public class Click implements Command {

    private String selector;

    public Click(String selector) {
        this.selector = selector;
    }

    @Override
    public boolean process(WebPage page) {
        page.click(selector);

        return true;
    }

    @Override
    public String toString() {
        return "Click[" + selector + "]";
    }
}
