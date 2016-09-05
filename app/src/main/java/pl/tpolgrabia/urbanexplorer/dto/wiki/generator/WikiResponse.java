package pl.tpolgrabia.urbanexplorer.dto.wiki.generator;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiResponse implements Serializable {
    private static final long serialVersionUID = 2208673089408151268L;
    private Boolean batchComplete;
    private List<WikiPage> pages;

    public Boolean getBatchComplete() {
        return batchComplete;
    }

    public void setBatchComplete(Boolean batchComplete) {
        this.batchComplete = batchComplete;
    }

    public List<WikiPage> getPages() {
        return pages;
    }

    public void setPages(List<WikiPage> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "WikiResponse{" +
            "batchComplete=" + batchComplete +
            ", pages=" + pages +
            '}';
    }
}
