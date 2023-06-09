package pl.tpolgrabia.googleutils.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceResult {
    @SerializedName("geometry")
    private GooglePlaceGeometry geometry;
    @SerializedName("icon")
    private String icon;
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("photos")
    private List<GooglePlacePhoto> photos;
    @SerializedName("place_id")
    private String placeId;
    @SerializedName("rating")
    private Double rating;
    @SerializedName("reference")
    private String reference;
    @SerializedName("scope")
    private String scope;
    @SerializedName("types")
    private List<String> types;
    @SerializedName("vicinity")
    private String vicinity;

    public GooglePlaceGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GooglePlaceGeometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GooglePlacePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<GooglePlacePhoto> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "GooglePlaceResult{" +
            "geometry=" + geometry +
            ", icon='" + icon + '\'' +
            ", id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", photos=" + photos +
            ", placeId='" + placeId + '\'' +
            ", rating=" + rating +
            ", reference='" + reference + '\'' +
            ", scope='" + scope + '\'' +
            ", types=" + types +
            ", vicinity='" + vicinity + '\'' +
            '}';
    }
}
