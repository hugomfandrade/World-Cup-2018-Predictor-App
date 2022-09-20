package org.hugoandrade.worldcup2018.predictor.backend.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum Stage {

    GROUP_STAGE("Group Stage"),
    ROUND_OF_16("Round of 16"),
    QUARTER_FINALS("Quarter Finals"),
    SEMI_FINALS("Semi Finals"),
    THIRD_PLACE_PLAY_OFF("3rd Place Playoff"),
    FINAL("Final"),

    ANY("Any"),
    UNKNOWN("Unknown");

    public final String name;

    Stage(String stage) {
        name = stage;
    }

    public static Stage get(String stage) {
        for (Stage s : Stage.values()) {
            if (s.name.equalsIgnoreCase(stage)) {
                return s;
            }
        }
        return null;
    }

    public boolean is(Match match) {
        if (match == null) return false;
        return this.name.equals(match.getStage());
    }

    public List<Match> filter(Collection<Match> matches) {
        return matches.stream()
                .filter(Stage.this::is)
                .collect(Collectors.toList());
    }
}