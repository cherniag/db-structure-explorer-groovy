package mobi.nowtechnologies.applicationtests.services.ui.command.impl;

import mobi.nowtechnologies.applicationtests.services.ui.WebPage;
import mobi.nowtechnologies.applicationtests.services.ui.command.Command;

public class Wait implements Command {
    private String waitForSelector;
    private boolean invert = false;

    public Wait(String waitForSelector) {
        this.waitForSelector = waitForSelector;
    }

    public Wait(String waitForSelector, boolean invert) {
        this.waitForSelector = waitForSelector;
        this.invert = invert;
    }

    @Override
    public boolean process(WebPage page) {
        boolean visible = page.waitForVisible(waitForSelector);

        if(!visible && invert) {
            return true;
        }

        return visible;
    }

    @Override
    public String toString() {
        return "Wait[" + waitForSelector + "]";
    }
}
