package mobi.nowtechnologies.server.shared.dto;

/**
 * Created by Oleg Artomov on 10/3/2014.
 */
public class ContentDtoResult<T> {

    private Long lastUpdatedTime;

    private T content;

    public ContentDtoResult(Long lastUpdatedTime, T content) {
        this.lastUpdatedTime = lastUpdatedTime;
        this.content = content;
    }

    public Long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public T getContent() {
        return content;
    }
}
