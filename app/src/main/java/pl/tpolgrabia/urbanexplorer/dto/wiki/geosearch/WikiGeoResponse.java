package pl.tpolgrabia.urbanexplorer.dto.wiki.geosearch;

import java.util.List;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public class WikiGeoResponse {
    private Boolean batchComplete;
    private List<WikiGeoObject> query;

    public Boolean getBatchComplete() {
        return batchComplete;
    }

    public void setBatchComplete(Boolean batchComplete) {
        this.batchComplete = batchComplete;
    }

    public List<WikiGeoObject> getQuery() {
        return query;
    }

    public void setQuery(List<WikiGeoObject> query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "WikiGeoResponse{" +
            "batchComplete=" + batchComplete +
            ", query=" + query +
            '}';
    }
}
