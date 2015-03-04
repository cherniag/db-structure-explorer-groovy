package mobi.nowtechnologies.server.dto.streamzine.error;

public class ErrorDto implements Comparable<ErrorDto> {

    private String key;
    private String message;

    public ErrorDto() {
    }

    public ErrorDto(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int compareTo(ErrorDto o) {
        return key.compareTo(o.key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorDto errorDto = (ErrorDto) o;

        if (!key.equals(errorDto.key)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
