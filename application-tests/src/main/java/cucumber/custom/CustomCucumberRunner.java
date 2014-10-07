package cucumber.custom;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.CustomJavaBackend;
import mobi.nowtechnologies.applicationtests.configuration.ApplicationConfiguration;
import org.apache.commons.lang3.SystemUtils;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CustomCucumberRunner extends Cucumber {
    public static final String PHANTOM_JS_LOCATION = "PHANTOM_JS_LOCATION";

    private AnnotationConfigApplicationContext context;

    public CustomCucumberRunner(Class clazz) throws InitializationError, IOException {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        context.close();
    }

    @Override
    protected cucumber.runtime.Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, RuntimeOptions runtimeOptions) throws InitializationError, IOException {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "application-tests");
        System.setProperty(CustomCucumberRunner.PHANTOM_JS_LOCATION, getPhantomLocation());

        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);

        context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("application-tests");
        context.register(ApplicationConfiguration.class);
        context.refresh();

        SpringObjectFactory objectFactory = new SpringObjectFactory(context);
        CustomJavaBackend customJavaBackend = new CustomJavaBackend(objectFactory, classFinder);
        CustomRuntimeGlue optionalGlue = new CustomRuntimeGlue(classLoader, customJavaBackend);
        return new CustomRuntime(resourceLoader, classLoader, runtimeOptions, Arrays.asList(customJavaBackend), optionalGlue);
    }

    public static String getPhantomLocation() {
        try {
            File rootDir = new ClassPathResource("env.properties").getFile().getParentFile();
            File location = new File(rootDir, "selenium");
            File execLocation = new File(location, (SystemUtils.IS_OS_WINDOWS) ? "win" : "unix");
            return new File(execLocation, (SystemUtils.IS_OS_WINDOWS) ? "phantomjs.exe" : "phantomjs").getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
