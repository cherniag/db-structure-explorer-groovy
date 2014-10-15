package mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import cucumber.api.Transformer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryTransformer extends Transformer<Word> {
    private static Map<String, String> words = load("features/dictionary.txt");

    @Override
    public Word transform(String dictionary) {
        String key = dictionary.trim().toLowerCase();

        Assert.isTrue(words.containsKey(key), "Not found in [features/dictionary.txt] value for: [" + key + "], all available:\n[" + words + "]");

        return new Word(words.get(key));
    }

    private static Map<String, String> load(String location) {
        try {
            Resource resource = new ClassPathResource(location);
            return toMap(Files.readLines(resource.getFile(), Charsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> toMap(List<String> lines) {
        Map<String, String> values = new HashMap<String, String>();
        for (String line : lines) {
            if(line.trim().startsWith("#") || line.trim().isEmpty()) {
                continue;
            }
            String[] pair = line.split("=");
            Assert.isTrue(pair.length == 2);
            values.put(pair[0].trim().toLowerCase(), pair[1].trim());
        }
        return values;
    }
}
