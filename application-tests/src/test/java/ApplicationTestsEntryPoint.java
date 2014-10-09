import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.custom.CustomCucumberRunner;
import org.junit.runner.RunWith;


@RunWith(CustomCucumberRunner.class)
@CucumberOptions(
        tags = {
                "@Facebook",
                "@GooglePlus",
                "@Email",
                "@Ready"
                // ,"@InDevelopment"
        },
        glue = "mobi.nowtechnologies.applicationtests.features",
        monochrome = false,
        format = {"html:target/build/reports/tests/cucumber"},
        strict = false,
        snippets = SnippetType.CAMELCASE)
public class ApplicationTestsEntryPoint {
}
