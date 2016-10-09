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
    @SerializedName("results")
    private List<GooglePlaceResult> results;
    @SerializedName("status")
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GooglePlaceResponse{" +
            "htmlAttributions=" + htmlAttributions +
            ", nextPageToken='" + nextPageToken + '\'' +
            ", results=" + results +
            ", status='" + status + '\'' +
            '}';
    }
}
