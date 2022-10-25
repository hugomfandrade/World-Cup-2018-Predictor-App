package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Admin {

    @javax.persistence.Id                       // jpa
    @org.springframework.data.annotation.Id     // mongo
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String userID;

    public Admin() { }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
