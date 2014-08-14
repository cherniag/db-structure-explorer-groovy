package cucumber.custom;

import cucumber.runtime.*;
import cucumber.runtime.java.CustomJavaBackend;
import cucumber.runtime.xstream.LocalizedXStreams;
import gherkin.I18n;
import gherkin.formatter.Argument;
import gherkin.formatter.model.Step;
import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CustomRuntimeGlue extends RuntimeGlue {
    private final Map<String, List<StepDefinition>> allStepDefinitions = new TreeMap<String, List<StepDefinition>>();
    private final UndefinedStepsTracker tracker = new UndefinedStepsTracker();

    private CustomJavaBackend customJavaBackend;
    private LocalizedXStreams localizedXStreams;

    public CustomRuntimeGlue(ClassLoader classLoader, CustomJavaBackend customJavaBackend) {
        super(null, null);
        this.customJavaBackend = customJavaBackend;
        this.localizedXStreams = new LocalizedXStreams(classLoader);
    }

    @Override
    public void addStepDefinition(StepDefinition stepDefinition) throws DuplicateStepDefinitionException {
        final String pattern = stepDefinition.getPattern();
        if(!allStepDefinitions.containsKey(pattern)) {
            allStepDefinitions.put(pattern, new ArrayList<StepDefinition>());
        }
        allStepDefinitions.get(pattern).add(stepDefinition);
    }

    @Override
    public StepDefinitionMatch stepDefinitionMatch(String featurePath, Step step, I18n i18n) {
        // filter by step
        List<StepDefinitionMatch> matches = stepDefinitionMatches(featurePath, step);
        try {
            if (matches.size() == 0) {
                tracker.addUndefinedStep(step, i18n);
                return null;
            }
            // filter by method and feature path
            return filterByMethodAndFeaturePath(featurePath, matches);

        } finally {
            tracker.storeStepKeyword(step, i18n);
        }
    }

    private StepDefinitionMatch filterByMethodAndFeaturePath(String featurePath, List<StepDefinitionMatch> matches) {
        final Class<?> aClass = restoreClass(featurePath);

        for (StepDefinitionMatch match : matches) {
            final String pattern = match.getPattern();
            List<Method> methodsByPattern = customJavaBackend.getMethodsByPattern(pattern);
            List<Method> methodsByClass = filterByClass(methodsByPattern, aClass);
            List<String> locations = convertToLocations(methodsByClass);
            if(locations.contains(match.getLocation())) {
                return match;
            }
        }

        return null;
    }

    private List<String> convertToLocations(List<Method> methodsByClass) {
        List<String> locations = new ArrayList<String>();
        for (Method m : methodsByClass) {
            locations.add(MethodFormat.SHORT.format(m));
        }
        return locations;
    }

    private List<StepDefinitionMatch> stepDefinitionMatches(String featurePath, Step step) {
        List<StepDefinitionMatch> result = new ArrayList<StepDefinitionMatch>();

        for (Map.Entry<String, List<StepDefinition>> entry : allStepDefinitions.entrySet()) {
            for (StepDefinition stepDefinition : entry.getValue()) {
                List<Argument> arguments = stepDefinition.matchedArguments(step);
                if (arguments != null) {
                    result.add(new StepDefinitionMatch(arguments, stepDefinition, featurePath, step, localizedXStreams));
                }
            }
        }

        return result;
    }

    private static List<Method> filterByClass(List<Method> inputMethods, Class<?> aClass) {
        List<Method> methods = new ArrayList<Method>();

        for (Method method : inputMethods) {
            if(method.getDeclaringClass().equals(aClass)) {
                methods.add(method);
            }
        }

        return methods;
    }

    private Class<?> restoreClass(String featurePath) {
        String baseName = FilenameUtils.getBaseName(featurePath) + "Feature";
        String packageName = FilenameUtils.getPathNoEndSeparator(featurePath).replace('/', '.');

        StringBuilder normalizedName = new StringBuilder(baseName.length());
        for(int i = 0; i < baseName.length(); i++) {
            final char charAt = baseName.charAt(i);

            if(i==0) {
                normalizedName.append(String.valueOf(charAt).toUpperCase());
                continue;
            }

            if(charAt == '-') {
                char afterDefis = baseName.charAt(++i);
                normalizedName.append(String.valueOf(afterDefis).toUpperCase());
                continue;
            }

            normalizedName.append(charAt);
        }

        final String className = packageName + "." + normalizedName;
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not restore class for path: " + featurePath + ", should be: [" + className + "]");
        }
    }
}
