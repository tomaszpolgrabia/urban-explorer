package pl.tpolgrabia.googleutils.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlaceResponse {
    @SerializedName("html_attributions")
    private List<String> htmlAttributions;
    @SerializedName("next_page_token")
    private String nextPageToken;
    private List<GooglePlaceResult> results;

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<GooglePlaceResult> getResults() {
        return results;
    }

    public void setResults(List<GooglePlaceResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "GooglePlaceResponse{" +
            "htmlAttributions=" + htmlAttributions +
            ", nextPageToken='" + nextPageToken + '\'' +
            ", results='" + results + '\'' +
            '}';
    }
}