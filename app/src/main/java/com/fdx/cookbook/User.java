package com.fdx.cookbook;

import java.util.Date;
import java.util.UUID;

public class User {
    private UUID mId;
    private String mFamily;
    private String mName;
    private Date mDate;
    private static final String TAG = "DebugUser";

    public User(String family, String name){
        mFamily=family;
        mName=name;
        mId =UUID.randomUUID();
        mDate=new Date();
    }

    public User(){
        mFamily="not found";
        mName="not found";
        mId =UUID.randomUUID();
        mDate=new Date();
    }
    public User(UUID uuid){
        mId =uuid;
        // waiting for user database
        //todo erase !
        switch(mId.toString()){
            case "c81d4e2e-bcf2-11e6-869b-7df92533d2db":
                mFamily="Devaux_Lion de ML";
                mName="Fabrice";
                return;
            case "c81d4e2e-bcf2-11e7-869b-7df92533d2db":
                mFamily="Devaux_Lion de ML";
                mName="Lucile";
                return;
            case "c81d4e2e-bcf3-11e6-869b-7df92533d2db":
                mFamily="Devaux_Lion de ML";
                mName="Véronique";
                return;
            default:
                mFamily="not found";
                mName="not found";
        }
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        this.mId = id;
    }

    public String getFamily() {
        return mFamily;
    }

    public void setFamily(String family) {
        mFamily = family;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNameComplete() {
        return mName+"@"+ mFamily;
    }

    public boolean IsEqual(User r){return (mId.toString().equals(r.getId().toString()));}

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (o == this) return true;
        if (getClass() != o.getClass()) return false;
        User u=(User) o;
        return u.getId().toString().equals(mId.toString());
    }
    //todo P2 épurer affichage quand champs vides
    //todo P2 tuto swipe page
    //todo P1 messagerie

}
