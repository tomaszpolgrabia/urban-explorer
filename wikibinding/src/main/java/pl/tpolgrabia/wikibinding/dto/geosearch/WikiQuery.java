package pl.tpolgrabia.wikibinding.dto.geosearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tpolgrabia on 01.11.16.
 */
public class WikiQuery {
    @SerializedName("geosearch")
    private List<WikiGeoObject> geosearch;

    public List<WikiGeoObject> getGeosearch() {
        return geosearch;
    }

    public void setGeosearch(List<WikiGeoObject> geosearch) {
        this.geosearch = geosearch;
    }

    @Override
    public String toString() {
        return "WikiQuery{" +
            "geosearch=" + geosearch +
            '}';
    }
}
