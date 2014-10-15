package mobi.nowtechnologies.applicationtests.services.ui.command.impl;

import mobi.nowtechnologies.applicationtests.services.ui.WebPage;
import mobi.nowtechnologies.applicationtests.services.ui.command.Command;
import org.openqa.selenium.NoSuchElementException;

public class Compare implements Command {
    private String value;
    private String selector;

    public Compare(String value, String selector) {
        this.value = value;
        this.selector = selector;
    }

    @Override
    public boolean process(WebPage page) {
        page.waitForVisible(selector);

        String text;
        try {
            text = page.text(selector);
        } catch (NoSuchElementException notFound) {
            return false;
        }

        return value.equals(text.trim());
    }

    @Override
    public String toString() {
        return "Compare[" + value + "] in [" + selector + "]";
    }
}
