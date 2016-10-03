package pl.tpolgrabia.urbanexplorer.dto;

import android.location.Location;
import pl.tpolgrabia.googleutils.callback.PlacesCallback;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlacesRequest {
    private Location location;
    private Long offset;
    private Long count;
    private Double searchRadius;
    private String searchItemType;
    private String pageToken;
    private PlacesCallback callback;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(Double searchRadius) {
        this.searchRadius = searchRadius;
    }

    public String getSearchItemType() {
        return searchItemType;
    }

    public void setSearchItemType(String searchItemType) {
        this.searchItemType = searchItemType;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public PlacesCallback getCallback() {
        return callback;
    }

    public void setCallback(PlacesCallback callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "GooglePlacesRequest{" +
            "location=" + location +
            ", offset=" + offset +
            ", count=" + count +
            ", searchRadius=" + searchRadius +
            ", searchItemType='" + searchItemType + '\'' +
            ", pageToken='" + pageToken + '\'' +
            ", callback=" + callback +
            '}';
    }
}
