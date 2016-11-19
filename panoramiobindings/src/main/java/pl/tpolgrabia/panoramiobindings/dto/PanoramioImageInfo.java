package pl.tpolgrabia.panoramiobindings.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioImageInfo implements Serializable{
    private static final long serialVersionUID = -3749926831546160047L;
    @SerializedName("height")
    private Double height;
    @SerializedName("owner_name")
    private String ownerName;
    @SerializedName("owner_id")
    private Long ownerId;
    @SerializedName("photo_file_url")
    private String photoFileUrl;
    @SerializedName("photo_title")
    private String photoTitle;
    @SerializedName("upload_date")
    private String uploadDate;
    @SerializedName("width")
    private Double width;
    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;
    @SerializedName("owner_url")
    private String ownerUrl;
    @SerializedName("photo_id")
    private Long photoId;
    @SerializedName("photo_url")
    private String photoUrl;

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPhotoFileUrl() {
        return photoFileUrl;
    }

    public void setPhotoFileUrl(String photoFileUrl) {
        this.photoFileUrl = photoFileUrl;
    }

    public String getPhotoTitle() {
        return photoTitle;
    }

    public void setPhotoTitle(String photoTitle) {
        this.photoTitle = photoTitle;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    public String getOwnerUrl() {
        return ownerUrl;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public String toString() {
        return "PanoramioImageInfo{" +
            "height=" + height +
            ", ownerName='" + ownerName + '\'' +
            ", ownerId=" + ownerId +
            ", photoFileUrl='" + photoFileUrl + '\'' +
            ", photoTitle='" + photoTitle + '\'' +
            ", uploadDate='" + uploadDate + '\'' +
            ", width=" + width +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", ownerUrl='" + ownerUrl + '\'' +
            ", photoId=" + photoId +
            ", photoUrl='" + photoUrl + '\'' +
            '}';
    }
}
