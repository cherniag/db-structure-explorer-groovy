package mobi.nowtechnologies.common.dto;

public class PairDto<K, V> {

    private K key;
    private V value;

    public PairDto() {
    }

    public PairDto(K key) {
        this.key = key;
    }

    public PairDto(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PairDto pairDto = (PairDto) o;

        if (key != null ?
            !key.equals(pairDto.key) :
            pairDto.key != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ?
               key.hashCode() :
               0;
    }

    @Override
    public String toString() {
        return "PairDto{" +
               "key=" + key +
               ", value=" + value +
               '}';
    }
}
