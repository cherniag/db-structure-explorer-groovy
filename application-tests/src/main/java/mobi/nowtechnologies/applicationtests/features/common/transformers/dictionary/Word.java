package mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary;

import mobi.nowtechnologies.applicationtests.features.common.transformers.list.ListValues;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<String> strings = ListValues.from(word).strings();
        return new HashSet<String>(strings);
    }

    public <T extends Enum<T>> Set<T> set(Class<T> type) {
        List<T> enums = ListValues.from(word).enums(type);
        return new HashSet<T>(enums);
    }

    //
    // List API
    //
    public List<String> list() {
        List<String> list = ListValues.from(word).strings();
        Collections.sort(list);
        return list;
    }

    @Override
    public String toString() {
        return word;
    }
}
