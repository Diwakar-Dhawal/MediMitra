package com.example.medi_mitra_v1.Models;

public class Reports {
    String pdfName,docName,hosName,fileUrl,timestamp,isfav,indropbox;

    public String getIndropbox() {
        return indropbox;
    }

    public void setIndropbox(String indropbox) {
        this.indropbox = indropbox;
    }

    public String getIsfav() {
        return isfav;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getPdfName() {
        return pdfName;
    }

    public String getDocName() {
        return docName;
    }

    public String getHosName() {
        return hosName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public void setHosName(String hosName) {
        this.hosName = hosName;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setIsfav(String isfav) {
        this.isfav = isfav;
    }
}
