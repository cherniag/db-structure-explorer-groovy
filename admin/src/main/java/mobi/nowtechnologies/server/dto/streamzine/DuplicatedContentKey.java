package mobi.nowtechnologies.server.dto.streamzine;

public class DuplicatedContentKey {

    private final OrdinalBlockDto block;

    public DuplicatedContentKey(OrdinalBlockDto block) {
        this.block = block;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DuplicatedContentKey that = (DuplicatedContentKey) o;

        if (block.getContentType() != that.block.getContentType()) {
            return false;
        }
        if (!block.getKey().equals(that.block.getKey())) {
            return false;
        }
        if (block.getValue() != null ?
            !block.getValue().equals(that.block.getValue()) :
            that.block.getValue() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = block.getContentType().hashCode();
        result = 31 * result + block.getKey().hashCode();
        result = 31 * result + (block.getValue() != null ?
                                block.getValue().hashCode() :
                                0);
        return result;
    }
}
