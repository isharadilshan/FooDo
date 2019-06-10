package netbit.develop.food.Model;

import java.util.Date;

public class Item {
    private int id;
    private String title;
    private String description;
    private String photoUrl;
    private String category;
    private Date  uploadTime;
    private String pickupDetails;
    private double uploadLatitude;
    private double uploadLongitude;
    private String userId;
    private String expirePeriod;
    private int likes;

    public Item(){

    }

    public Item(String title, String description, String photoUrl, String category, Date uploadTime, double uploadLatitude, double uploadLongitude, String userId, String expirePeriod) {
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.category = category;
        this.uploadTime = uploadTime;
        this.uploadLatitude = uploadLatitude;
        this.uploadLongitude = uploadLongitude;
        this.userId = userId;
        this.expirePeriod = expirePeriod;
    }

    public Item(String title, String description, String photoUrl, String category, Date uploadTime, String pickupDetails, double uploadLatitude, double uploadLongitude, String userId, String expirePeriod) {
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.category = category;
        this.uploadTime = uploadTime;
        this.pickupDetails = pickupDetails;
        this.uploadLatitude = uploadLatitude;
        this.uploadLongitude = uploadLongitude;
        this.userId = userId;
        this.expirePeriod = expirePeriod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getPickupDetails() {
        return pickupDetails;
    }

    public void setPickupDetails(String pickupDetails) {
        this.pickupDetails = pickupDetails;
    }

    public double getUploadLatitude() {
        return uploadLatitude;
    }

    public void setUploadLatitude(double uploadLatitude) {
        this.uploadLatitude = uploadLatitude;
    }

    public double getUploadLongitude() {
        return uploadLongitude;
    }

    public void setUploadLongitude(double uploadLongitude) {
        this.uploadLongitude = uploadLongitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExpirePeriod() {
        return expirePeriod;
    }

    public void setExpirePeriod(String expirePeriod) {
        this.expirePeriod = expirePeriod;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
