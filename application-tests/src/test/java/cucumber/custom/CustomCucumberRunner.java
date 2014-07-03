package cucumber.custom;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.CustomJavaBackend;
import mobi.nowtechnologies.applicationtests.configuration.ApplicationConfiguration;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import java.io.IOException;
import java.util.Arrays;

public class CustomCucumberRunner extends Cucumber {
    private AnnotationConfigApplicationContext context;

    public CustomCucumberRunner(Class clazz) throws InitializationError, IOException {
        super(clazz);

        System.setProperty(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "application-tests");
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
        context.close();
    }

    @Override
    protected cucumber.runtime.Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, RuntimeOptions runtimeOptions) throws InitializationError, IOException {
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        SpringObjectFactory objectFactory = new SpringObjectFactory(context);
        CustomJavaBackend customJavaBackend = new CustomJavaBackend(objectFactory, classFinder);
        CustomRuntimeGlue optionalGlue = new CustomRuntimeGlue(classLoader, customJavaBackend);
        return new CustomRuntime(resourceLoader, classLoader, runtimeOptions, Arrays.asList(customJavaBackend), optionalGlue);
    }
}
