package pl.tpolgrabia.wikibinding.dto.generator;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiThumbnail {
    @SerializedName("height")
    private Long height;
    @SerializedName("source")
    private String source;
    @SerializedName("width")
    private Long width;

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "WikiThumbnail{" +
            "height=" + height +
            ", source='" + source + '\'' +
            ", width=" + width +
            '}';
    }
}
