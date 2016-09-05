package pl.tpolgrabia.urbanexplorer.dto.wiki.generator;

import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiPage {
    private List<WikiLocation> coordinates;
    private Long index;
    private Long ns;
    private Long pageId;
    private WikiThumbnail thumbnail;
    private String title;

    public List<WikiLocation> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<WikiLocation> coordinates) {
        this.coordinates = coordinates;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getNs() {
        return ns;
    }

    public void setNs(Long ns) {
        this.ns = ns;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public WikiThumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(WikiThumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "WikiPage{" +
            "coordinates=" + coordinates +
            ", index=" + index +
            ", ns=" + ns +
            ", pageId=" + pageId +
            ", thumbnail=" + thumbnail +
            ", title='" + title + '\'' +
            '}';
    }
}
