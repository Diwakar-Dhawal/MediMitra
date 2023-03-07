package com.example.medi_mitra_v1.Models;

public class PostInfoModel {
    String uploaderImg,uploaderName,caption,postImg,like,timestamp,uid;

    public PostInfoModel(String uploaderImg, String uploaderName, String caption, String postImg, String like, String timestamp,String uid) {
        this.uploaderImg = uploaderImg;
        this.uploaderName = uploaderName;
        this.caption = caption;
        this.postImg = postImg;
        this.like = like;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getUploaderImg() {
        return uploaderImg;
    }

    public void setUploaderImg(String uploaderImg) {
        this.uploaderImg = uploaderImg;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getLike() {
        return like;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
