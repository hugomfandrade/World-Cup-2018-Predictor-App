package org.hugoandrade.worldcup2018.predictor.backend.authentication;

import java.util.Objects;

public class AccountDto {

    private String id;
    private String username;
    private int score;
    private int rank;

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

    public AccountDto(String id, String username, int score, int rank) {
        this.id = id;
        this.username = username;
        this.score = score;
        this.rank = rank;
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDto that = (AccountDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                score == that.score &&
                rank == that.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, score, rank);
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", score=" + score +
                ", rank=" + rank +
                '}';
    }
}
