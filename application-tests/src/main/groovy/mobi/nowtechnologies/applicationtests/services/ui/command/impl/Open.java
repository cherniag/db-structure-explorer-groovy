package mobi.nowtechnologies.applicationtests.services.ui.command.impl;

import mobi.nowtechnologies.applicationtests.services.ui.WebPage;
import mobi.nowtechnologies.applicationtests.services.ui.command.Command;

public class Open implements Command {
    private String url;

    public Open(String url) {
        this.url = url;
    }

    @Override
    public boolean process(WebPage page) {
        page.go(url);
        return page.waitForVisible("title");
    }

    @Override
    public String toString() {
        return "Open[" + url + "]";
    }
}
