package mobi.nowtechnologies.applicationtests.services.ui.command;

import mobi.nowtechnologies.applicationtests.services.ui.WebPage;

public interface Command {
    boolean process(WebPage page);
}
