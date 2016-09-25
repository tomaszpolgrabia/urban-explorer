package pl.tpolgrabia.wikibinding.dto.generator;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiThumbnail {
    private Long height;
    private String source;
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
