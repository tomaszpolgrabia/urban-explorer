package pl.tpolgrabia.urbanexplorer.dto;

import java.util.Date;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioImageInfo {
    private Double height;
    private String ownerName;
    private Long ownerId;
    private String photoFileUrl;
    private String photoTitle;
    private Date uploadDate;
    private Double width;

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

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }
}
