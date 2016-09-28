package pl.tpolgrabia.googleutils.dto;

import java.util.List;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlacePhoto {
    private Long height;
    private List<String> htmlAttributions;
    private String photoReference;
    private Long width;

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "GooglePlacePhoto{" +
            "height=" + height +
            ", htmlAttributions=" + htmlAttributions +
            ", photoReference='" + photoReference + '\'' +
            ", width=" + width +
            '}';
    }
}
