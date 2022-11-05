package org.hugoandrade.worldcup2018.predictor.admin.view.main;

import java.util.HashMap;
import java.util.List;

import org.hugoandrade.worldcup2018.predictor.data.Group;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.view.FragComm;

public interface MainFragComm {

    interface ProvidedGroupsChildFragmentOps {
        void displayGroups(HashMap<String, Group> groupsMap);
    }

    interface ProvidedMatchesFragmentOps {
        void displayMatches(List<Match> matchList);
        void updateMatch(Match match);
        void updateFailedMatch(Match match);
    }

    interface ProvidedMainActivityOps extends FragComm.RequiredActivityBaseOps {
        void setMatch(Match match);
    }
}
