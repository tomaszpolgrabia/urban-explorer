package pl.tpolgrabia.wikibinding.dto.generator;

import java.io.Serializable;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiLocation implements Serializable{
    private static final long serialVersionUID = 2574692501816893919L;
    private String globe;
    private Double latitude;
    private Double longitude;
    private String primary;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getGlobe() {
        return globe;
    }

    public void setGlobe(String globe) {
        this.globe = globe;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return "WikiLocation{" +
            "globe='" + globe + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", primary='" + primary + '\'' +
            '}';
    }
}
