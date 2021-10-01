package com.fdx.cookbook;

import java.util.UUID;

public class MailCard {
    private Boolean mIsReceived;
    private Boolean mIsRequest;
    private String mMessage;
    private User mUser; // from or to
    private Integer mStatus;
    private Integer SUBMITTED=1;
    private Integer ACCEPTED=2;
    private Integer REFUSED=3;
    private UUID mIdRecipe;
    private Integer mRequestId;
    private String mTitle;
    private String UUIDNULL="00000000-0000-0000-0000-000000000000";


    public MailCard(Recipe r){
        this();
        if ((r!=null)&&(r.IsMessage())){
            mUser=r.getUserFrom();
            mMessage=r.getMessage();
            mStatus=SUBMITTED;
            mIdRecipe=r.getId();
            mTitle=r.getTitle();
            mRequestId=0;
        }
    }

    public MailCard(){
        mUser=new User();
        mIsReceived=true;
        mIsRequest=false;
        mMessage="";
        mStatus=0;
        mIdRecipe=UUID.fromString(UUIDNULL);
        mTitle="";
    }

    public Boolean isReceived() {
        return mIsReceived;
    }
    public Boolean isRequest() {
        return mIsRequest;
    }

    public void setRequest() {
        mIsRequest = true;
        mIsReceived=false;
    }

    public Boolean isAccepted() {
        return mStatus==ACCEPTED;
    }
    public Boolean isRefused() {
        return mStatus==REFUSED;
    }

    public UUID getRecipeId(){return mIdRecipe;}
    public void setIdRecipe(UUID idRecipe) {
        mIdRecipe = idRecipe;
    }

    public Boolean isSubmitted() {return mStatus==SUBMITTED;}
    public void setSubmitted(){mStatus=SUBMITTED;}
    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public void setRefused() {mStatus=REFUSED;}
    public void setAccepted() {mStatus=ACCEPTED;}

    public Integer getRequestId() {
        return mRequestId;
    }

    public void setRequestId(Integer requestId) {
        mRequestId = requestId;
    }
}
