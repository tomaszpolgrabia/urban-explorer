package pl.tpolgrabia.wikibinding.dto.geosearch;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public class WikiGeoResponse2 {
    @SerializedName("batch_complete")
    private Boolean batchComplete;
    @SerializedName("query")
    private WikiQuery query;

    public Boolean getBatchComplete() {
        return batchComplete;
    }

    public void setBatchComplete(Boolean batchComplete) {
        this.batchComplete = batchComplete;
    }

    public WikiQuery getQuery() {
        return query;
    }

    public void setQuery(WikiQuery query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "WikiGeoResponse2{" +
            "batchComplete=" + batchComplete +
            ", query=" + query +
            '}';
    }
}
