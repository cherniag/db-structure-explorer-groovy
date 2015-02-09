package mobi.nowtechnologies.applicationtests.features.common.transformers.list;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import cucumber.api.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListValues {
    private List<String> values;

    public static ListValues from(String value) {
        ListValues listValues = new ListValues();
        listValues.values = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(value);
        return listValues;
    }

    public static ListValues from(Collection<String> values) {
        ListValues listValues = new ListValues();
        listValues.values = new ArrayList<>(values);
        return listValues;
    }

    //
    // List value methods
    //
    public List<Long> longs() {
        return new ArrayList<>(Lists.transform(values, new Function<String, Long>() {
            @Override
            public Long apply(String input) {
                return Long.valueOf(input);
            }
        }));
    }

    public <T> List<T> values(Transformer<T> transformer) {
        List<T> transformed = new ArrayList<>();
        for (final String value : values) {
            transformed.add(transformer.transform(value));
        }
        return transformed;
    }

    public List<String> strings() {
        return new ArrayList<>(values);
    }

    public List<Integer> ints() {
        return new ArrayList<>(Lists.transform(values, new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return Integer.valueOf(input);
            }
        }));
    }

    public <T extends Enum<T>> List<T> enums(final Class<T> type) {
        return new ArrayList<>(Lists.transform(values, new Function<String, T>() {
            @Override
            public T apply(String input) {
                return Enum.valueOf(type, input);
            }
        }));
    }

    //
    // Single value methods
    //
    public long firstLong() {
        return longs().get(0);
    }

    public int firstInt() {
        return ints().get(0);
    }

    public String firstString() {
        return values.get(0);
    }
}
