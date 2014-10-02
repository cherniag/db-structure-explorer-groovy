package cucumber.runtime.java;

import cucumber.runtime.ClassFinder;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Please DO NOT MOVE THIS CLASS TO OTHER PACKAGES
 */
public class CustomJavaBackend extends JavaBackend {
    private Map<String, List<Method>> methods = new ConcurrentHashMap<String, List<Method>>();

    public CustomJavaBackend(ObjectFactory objectFactory, ClassFinder classFinder) {
        super(objectFactory, classFinder);
    }

    @Override
    void addStepDefinition(Annotation annotation, Method method) {
        super.addStepDefinition(annotation, method);

        String value = (String) AnnotationUtils.getValue(annotation);

        if(!methods.containsKey(value)) {
            methods.put(value, new ArrayList<Method>());
        }
        methods.get(value).add(method);
    }

    public List<Method> getMethodsByPattern(String pattern) {
        return methods.get(pattern);
    }
}
