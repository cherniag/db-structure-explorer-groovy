package mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValues;

import java.util.*;

public class Word {
    private String word;

    public Word(String word) {
        this.word = word;
    }

    public String value() {
        return word;
    }

    //
    // Set API
    //
    public Set<String> set() {
        Set<String> values = new LinkedHashSet<String>();

        Iterable<String> iterable = Splitter.on(",").omitEmptyStrings().trimResults().split(word);

        Iterators.addAll(values, iterable.iterator());

        return values;
    }

    public <T extends Enum<T>> Set<T> set(Class<T> type) {
        return new HashSet<T>(ListValues.from(set()).enums(type));
    }

    //
    // List API
    //
    public List<String> list() {
        ArrayList<String> list = new ArrayList<String>(set());
        Collections.sort(list);
        return list;
    }

    @Override
    public String toString() {
        return word;
    }
}
