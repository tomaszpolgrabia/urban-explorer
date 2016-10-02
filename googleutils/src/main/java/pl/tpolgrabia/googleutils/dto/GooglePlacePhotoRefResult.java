package pl.tpolgrabia.googleutils.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tpolgrabia on 02.10.16.
 */
public class GooglePlacePhotoRefResult {
    @SerializedName("htmlAttributions")
    private List<String> htmlAttributions;
    private Long width;
    private Long height;
    @SerializedName("photoReference")
    private String photoReference;

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    @Override
    public String toString() {
        return "GooglePlacePhotoRefResult{" +
            "htmlAttributions=" + htmlAttributions +
            ", width=" + width +
            ", height=" + height +
            ", photoReference='" + photoReference + '\'' +
            '}';
    }
}
