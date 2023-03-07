package com.example.medi_mitra_v1.Models;

public class SharedPdfInfoModel {
    String SharedpdfName, ShareddocName, SharedhosName, SharedfileUrl, Sharedtimestamp, Sharedisfav, Shareduid,Sharedindropbox;

    public SharedPdfInfoModel(String sharedpdfName, String shareddocName, String sharedhosName, String sharedfileUrl, String sharedtimestamp, String sharedisfav, String shareduid,String sharedindropbox) {
        SharedpdfName = sharedpdfName;
        ShareddocName = shareddocName;
        SharedhosName = sharedhosName;
        SharedfileUrl = sharedfileUrl;
        Sharedtimestamp = sharedtimestamp;
        Sharedisfav = sharedisfav;
        Shareduid = shareduid;
        Sharedindropbox = sharedindropbox;
    }

    public String getSharedpdfName() {
        return SharedpdfName;
    }

    public void setSharedpdfName(String sharedpdfName) {
        SharedpdfName = sharedpdfName;
    }

    public String getShareddocName() {
        return ShareddocName;
    }

    public void setShareddocName(String shareddocName) {
        ShareddocName = shareddocName;
    }

    public String getSharedhosName() {
        return SharedhosName;
    }

    public void setSharedhosName(String sharedhosName) {
        SharedhosName = sharedhosName;
    }

    public String getSharedfileUrl() {
        return SharedfileUrl;
    }

    public void setSharedfileUrl(String sharedfileUrl) {
        SharedfileUrl = sharedfileUrl;
    }

    public String getSharedtimestamp() {
        return Sharedtimestamp;
    }

    public void setSharedtimestamp(String sharedtimestamp) {
        Sharedtimestamp = sharedtimestamp;
    }

    public String getSharedisfav() {
        return Sharedisfav;
    }

    public void setSharedisfav(String sharedisfav) {
        Sharedisfav = sharedisfav;
    }

    public String getShareduid() {
        return Shareduid;
    }

    public void setShareduid(String shareduid) {
        Shareduid = shareduid;
    }
}
