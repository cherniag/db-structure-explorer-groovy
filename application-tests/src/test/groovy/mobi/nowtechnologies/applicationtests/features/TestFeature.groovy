package mobi.nowtechnologies.applicationtests.features

import cucumber.api.java.en.Given
import org.springframework.stereotype.Component

@Component
public class TestFeature {
    @Given("^testing the value conversion\$")
    public void testing() throws Throwable {
        // throw new RuntimeException("Some problem detected in groovy test");
    }
}

