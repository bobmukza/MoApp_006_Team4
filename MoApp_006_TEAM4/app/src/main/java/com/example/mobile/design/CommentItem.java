package com.example.capstone.design;

public class CommentItem {
    private String profile_image;
    private String strMessage;
    private String strName;
    private String strDate;

    public CommentItem(String profile_image, String strMessage, String strName, String strDate) {
        this.profile_image = profile_image;
        this.strMessage = strMessage;
        this.strName = strName;
        this.strDate = strDate;
    }

    public String getprofile_image() {
        return profile_image;
    }

    public void setprofile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }
}
