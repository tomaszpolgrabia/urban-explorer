package pl.tpolgrabia.wikibinding.dto.generator;

import pl.tpolgrabia.wikibinding.dto.geosearch.WikiQuery;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiResponse2 implements Serializable {
    private static final long serialVersionUID = 2208673089408151268L;
    private Boolean batchComplete;
    private WikiQuery2 query;

    public Boolean getBatchComplete() {
        return batchComplete;
    }

    public void setBatchComplete(Boolean batchComplete) {
        this.batchComplete = batchComplete;
    }

    public WikiQuery2 getQuery() {
        return query;
    }

    public void setQuery(WikiQuery2 query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "WikiResponse2{" +
            "batchComplete=" + batchComplete +
            ", query=" + query +
            '}';
    }
}
