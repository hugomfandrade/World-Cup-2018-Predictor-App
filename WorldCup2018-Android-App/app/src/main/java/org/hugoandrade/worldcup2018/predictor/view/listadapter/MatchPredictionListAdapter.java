package org.hugoandrade.worldcup2018.predictor.view.listadapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.data.Prediction;
import org.hugoandrade.worldcup2018.predictor.data.User;
import org.hugoandrade.worldcup2018.predictor.utils.BitmapUtils;
import org.hugoandrade.worldcup2018.predictor.utils.CountryUtils;
import org.hugoandrade.worldcup2018.predictor.utils.MatchAppUtils;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;

import java.util.List;

public class MatchPredictionListAdapter extends RecyclerView.Adapter<MatchPredictionListAdapter.ViewHolder> {

    private static final int COLOR_DEFAULT = Color.parseColor("#aaffffff");
    private static final int COLOR_INCORRECT_PREDICTION = Color.parseColor("#aaff0000");
    private static final int COLOR_CORRECT_OUTCOME = Color.parseColor("#aaaa7d00");
    private static final int COLOR_CORRECT_MARGIN_OF_VICTORY = Color.parseColor("#aaAAAA00");
    private static final int COLOR_CORRECT_PREDICTION = Color.parseColor("#aa00AA00");

    private Match mMatch;

    private List<Pair<User, Prediction>> mPredictionList;

    public MatchPredictionListAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_match_prediction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Prediction prediction = mPredictionList.get(holder.getAdapterPosition()).second;
        User user = mPredictionList.get(holder.getAdapterPosition()).first;

        holder.isBinding = true;

        holder.cardView.setCardBackgroundColor(MatchAppUtils.getCardColor(mMatch, prediction));
        BitmapUtils.decodeSampledBitmapFromResourceAsync(context, holder.ivHomeTeam, CountryUtils.getImageID(mMatch.getHomeTeam()));
        BitmapUtils.decodeSampledBitmapFromResourceAsync(context, holder.ivAwayTeam, CountryUtils.getImageID(mMatch.getAwayTeam()));
        holder.etHomeTeamGoals.setText(MatchUtils.getAsString(prediction.getHomeTeamGoals()));
        holder.etAwayTeamGoals.setText(MatchUtils.getAsString(prediction.getAwayTeamGoals()));
        holder.tvPoints.setText(getPointsText(prediction));
        holder.tvUser.setText(user.getUsername());

        holder.isBinding = false;
    }

    @Override
    public int getItemCount() {
        return mPredictionList == null ? 0 : mPredictionList.size();
    }

    public void setMatch(Match match) {
        mMatch = match;
    }

    public void setPredictionList(List<Pair<User, Prediction>> predictionList) {
        mPredictionList = predictionList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView ivHomeTeam;
        ImageView ivAwayTeam;
        EditText etHomeTeamGoals;
        EditText etAwayTeamGoals;
        TextView tvPoints;
        TextView tvUser;
        boolean isBinding;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_container);
            ivHomeTeam = itemView.findViewById(R.id.iv_match_home_team);
            ivAwayTeam = itemView.findViewById(R.id.iv_match_away_team);
            etHomeTeamGoals = itemView.findViewById(R.id.et_home_team_goals);
            etAwayTeamGoals = itemView.findViewById(R.id.et_away_team_goals);
            tvPoints = itemView.findViewById(R.id.tv_points);
            tvUser = itemView.findViewById(R.id.tv_user);
        }
    }
    private String getPointsText(Prediction prediction) {
        if (prediction == null || prediction.getScore() == -1) {
            return "0";
        }
        else {
            return String.valueOf(prediction.getScore());
        }
    }
}
