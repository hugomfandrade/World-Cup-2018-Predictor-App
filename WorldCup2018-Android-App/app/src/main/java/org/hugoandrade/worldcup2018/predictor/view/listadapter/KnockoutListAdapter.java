package org.hugoandrade.worldcup2018.predictor.view.listadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hugoandrade.worldcup2018.predictor.GlobalData;
import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.Country;
import org.hugoandrade.worldcup2018.predictor.data.Match;
import org.hugoandrade.worldcup2018.predictor.utils.BitmapUtils;
import org.hugoandrade.worldcup2018.predictor.utils.CountryUtils;
import org.hugoandrade.worldcup2018.predictor.utils.MatchAppUtils;
import org.hugoandrade.worldcup2018.predictor.utils.MatchUtils;
import org.hugoandrade.worldcup2018.predictor.utils.StringUtils;
import org.hugoandrade.worldcup2018.predictor.utils.TranslationUtils;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class KnockoutListAdapter extends RecyclerView.Adapter<KnockoutListAdapter.ViewHolder> {

    public final String TAG = getClass().getSimpleName();

    private static final String TIME_TEMPLATE = "d MMMM - HH:mm";

    private List<Match> mMatchList;
    private SparseArray<Match> mMatchSet;

    private OnKnockoutListAdapterListener mOnKnockoutListAdapterListener;

    public KnockoutListAdapter(List<Match> matchList) {
        set(matchList);
    }

    @NonNull
    @Override
    public KnockoutListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_knockout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final KnockoutListAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        final Match match = mMatchList.get(holder.getAdapterPosition());

        holder.tvHomeTeam.setText(MatchAppUtils.tryGetTemporaryHomeTeam(context, mMatchSet, match));
        holder.tvAwayTeam.setText(MatchAppUtils.tryGetTemporaryAwayTeam(context, mMatchSet, match));

        BitmapUtils.decodeSampledBitmapFromResourceAsync(context, holder.ivHomeTeam, CountryUtils.getImageID(match.getHomeTeam()));
        BitmapUtils.decodeSampledBitmapFromResourceAsync(context, holder.ivAwayTeam, CountryUtils.getImageID(match.getAwayTeam()));
        //holder.ivHomeTeam.setImageResource(Country.getImageID(match.getHomeTeam()));
        //holder.ivAwayTeam.setImageResource(Country.getImageID(match.getAwayTeam()));

        holder.tvHomeTeamGoals.setText(MatchUtils.getScoreOfHomeTeam(match));
        holder.tvAwayTeamGoals.setText(MatchUtils.getScoreOfAwayTeam(match));
        holder.tvDateAndTime.setText(StringUtils.capitalize(DateFormat.format(TIME_TEMPLATE, match.getDateAndTime())));

        holder.tvMatchNumber.setText(TextUtils.concat(
                context.getString(R.string.match_number),
                ": ",
                String.valueOf(match.getMatchNumber())));
        holder.tvStage.setText(TranslationUtils.getAsString(context, match));
        holder.tvStadium.setText(TranslationUtils.translateStadium(context, match.getStadium()));


        holder.tvHomeTeam.setTypeface(holder.tvHomeTeam.getTypeface(), Typeface.NORMAL);
        holder.tvAwayTeam.setTypeface(holder.tvAwayTeam.getTypeface(), Typeface.NORMAL);

        if (MatchUtils.isMatchPlayed(match)) {
            if (MatchUtils.didHomeTeamWin(match))
                holder.tvHomeTeam.setTypeface(holder.tvHomeTeam.getTypeface(), Typeface.BOLD);
            else if (MatchUtils.didAwayTeamWin(match))
                holder.tvAwayTeam.setTypeface(holder.tvAwayTeam.getTypeface(), Typeface.BOLD);
        }

        boolean hasHomeCountryFlag = CountryUtils.getImageID(match.getHomeTeam()) != 0;
        boolean hasAwayCountryFlag = CountryUtils.getImageID(match.getAwayTeam()) != 0;
        ((View) holder.ivHomeTeam.getParent()).setVisibility(hasHomeCountryFlag ? VISIBLE : GONE);
        ((View) holder.ivAwayTeam.getParent()).setVisibility(hasAwayCountryFlag ? VISIBLE : GONE);
        holder.tvHomeTeam.setGravity(hasHomeCountryFlag ? Gravity.TOP | Gravity.CENTER_HORIZONTAL : Gravity.CENTER);
        holder.tvAwayTeam.setGravity(hasAwayCountryFlag ? Gravity.TOP | Gravity.CENTER_HORIZONTAL : Gravity.CENTER);
    }

    @Override
    public int getItemCount() {
        return mMatchList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void set(List<Match> matchList) {
        mMatchList = matchList;
        mMatchSet = new SparseArray<>();
        for (Match m : GlobalData.getInstance().getMatchList()) {
            mMatchSet.put(m.getMatchNumber(), m);
        }
    }

    public void setOnKnockoutListAdapterListener(OnKnockoutListAdapterListener listener) {
        mOnKnockoutListAdapterListener = listener;
    }

    public interface OnKnockoutListAdapterListener {
        void onCountryClicked(Country country);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateAndTime;
        TextView tvHomeTeam;
        TextView tvAwayTeam;
        ImageView ivHomeTeam;
        ImageView ivAwayTeam;
        TextView tvHomeTeamGoals;
        TextView tvAwayTeamGoals;

        // info
        ImageView ivInfo;

        // details
        View detailsInfoContainer;
        TextView tvMatchNumber;
        TextView tvStadium;
        TextView tvStage;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(View itemView) {
            super(itemView);

            tvMatchNumber = itemView.findViewById(R.id.tv_match_number);
            tvDateAndTime = itemView.findViewById(R.id.tv_match_date_time);
            tvHomeTeam = itemView.findViewById(R.id.tv_match_home_team);
            tvAwayTeam = itemView.findViewById(R.id.tv_match_away_team);
            ivHomeTeam = itemView.findViewById(R.id.iv_match_home_team);
            ivAwayTeam = itemView.findViewById(R.id.iv_match_away_team);
            tvHomeTeamGoals = itemView.findViewById(R.id.tv_home_team_goals);
            tvAwayTeamGoals = itemView.findViewById(R.id.tv_away_team_goals);

            ivInfo = itemView.findViewById(R.id.iv_info);
            tvStadium = itemView.findViewById(R.id.tv_match_stadium);
            tvStage = itemView.findViewById(R.id.tv_stage);
            detailsInfoContainer = itemView.findViewById(R.id.viewGroup_info_details_container);
            ivInfo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            detailsInfoContainer.setVisibility(VISIBLE);
                            break;

                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            detailsInfoContainer.setVisibility(View.INVISIBLE);
                            break;
                    }

                    return true;
                }
            });

            ivHomeTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Country c = mMatchList.get(getAdapterPosition()).getHomeTeam();

                    if (c != null && mOnKnockoutListAdapterListener != null) {
                        mOnKnockoutListAdapterListener.onCountryClicked(c);
                    }
                }
            });
            ivAwayTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Country c = mMatchList.get(getAdapterPosition()).getAwayTeam();

                    if (c != null && mOnKnockoutListAdapterListener != null) {
                        mOnKnockoutListAdapterListener.onCountryClicked(c);
                    }
                }
            });
        }
    }
}