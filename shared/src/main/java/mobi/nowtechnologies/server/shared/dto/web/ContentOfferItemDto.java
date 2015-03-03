package mobi.nowtechnologies.server.shared.dto.web;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ContentOfferItemDto {

    private String title;
    private String coverFileName;
    private String authorName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverFileName() {
        return coverFileName;
    }

    public void setCoverFileName(String coverFileName) {
        this.coverFileName = coverFileName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "ContentOfferItemDto [title=" + title + ", coverFileName=" + coverFileName + ", authorName=" + authorName + "]";
    }

}
