package org.hugoandrade.worldcup2018.predictor.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(unique = true)
    private String username;
    private String password;
    private String salt;
    private int score;

    public Account() { }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account(String id, String username, String password, String salt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
