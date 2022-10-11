package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import java.util.Objects;

public class AccountDto {

    private String id;
    private String username;
    private int score;

    public AccountDto() { }

    public AccountDto(String username) {
        this.username = username;
    }

    public AccountDto(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public AccountDto(String id, String username, int score) {
        this.id = id;
        this.username = username;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDto that = (AccountDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                score == that.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, score);
    }
}
