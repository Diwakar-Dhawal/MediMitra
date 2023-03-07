package com.example.medi_mitra_v1.Models;

public class PdfInfoModel {
    String pdfName,docName,hosName,fileUrl,timestamp,isfav,indropbox;

    public PdfInfoModel(String pdfName, String docName, String hosName, String fileUrl, String timestamp, String isfav,String indropbox) {
        this.pdfName = pdfName;
        this.docName = docName;
        this.hosName = hosName;
        this.fileUrl = fileUrl;
        this.timestamp = timestamp;
        this.isfav = isfav;
        this.indropbox = indropbox;
    }

    public String getIndropbox() {
        return indropbox;
    }

    public void setIndropbox(String indropbox) {
        this.indropbox = indropbox;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getHosName() {
        return hosName;
    }

    public void setHosName(String hosName) {
        this.hosName = hosName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsfav() {
        return isfav;
    }

    public void setIsfav(String isfav) {
        this.isfav = isfav;
    }
}
