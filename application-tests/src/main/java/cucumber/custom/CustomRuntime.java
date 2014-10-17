package cucumber.custom;

import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.CustomJavaBackend;

import java.util.List;

public class CustomRuntime extends cucumber.runtime.Runtime {
    public CustomRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, RuntimeOptions runtimeOptions, List<CustomJavaBackend> backends, CustomRuntimeGlue optionalGlue) {
        super(resourceLoader, classLoader, backends, runtimeOptions, optionalGlue);
    }
}