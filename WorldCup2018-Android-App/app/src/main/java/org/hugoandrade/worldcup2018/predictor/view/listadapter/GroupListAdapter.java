package org.hugoandrade.worldcup2018.predictor.view.listadapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.data.raw.Country;
import org.hugoandrade.worldcup2018.predictor.utils.BitmapUtils;
import org.hugoandrade.worldcup2018.predictor.utils.TranslationUtils;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<Country> mCountryList;
    private Country mPrimaryCountry;

    public GroupListAdapter(List<Country> matchList) {
        mCountryList = matchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupListAdapter.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        final Country country = mCountryList.get(holder.getAdapterPosition());

        boolean isPrimary = mPrimaryCountry != null && mPrimaryCountry.getID().equals(country.getID());
        holder.itemView.setBackgroundColor(isPrimary? Color.parseColor("#6626629e") : Color.TRANSPARENT);

        holder.tvPosition.setText(String.valueOf(country.getPosition()));
        holder.tvCountryName.setText(TranslationUtils.translateCountryName(context, country.getName()));
        holder.tvCountryName.setGravity(Gravity.START);
        BitmapUtils.decodeSampledBitmapFromResource(context, holder.ivCountryFlag, Country.getImageID(country),
                "Portugal".equals(country.getName()));
        //holder.ivCountryFlag.setImageResource(Country.getImageID(country));
        holder.tvVictories.setText(String.valueOf(country.getVictories()));
        holder.tvDraws.setText(String.valueOf(country.getDraws()));
        holder.tvDefeats.setText(String.valueOf(country.getDefeats()));
        holder.tvGoalsFor.setText(String.valueOf(country.getGoalsFor()));
        holder.tvGoalsAgainst.setText(String.valueOf(country.getGoalsAgainst()));
        holder.tvGoalsDifference.setText(String.valueOf(country.getGoalsDifference()));
        holder.tvPoints.setText(String.valueOf(country.getPoints()));

        boolean advancedGroupStage = country.hasAdvancedGroupStage();
        holder.tvPosition.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvCountryName.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvVictories.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvDraws.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvDefeats.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvGoalsFor.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvGoalsAgainst.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvGoalsDifference.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
        holder.tvPoints.setTypeface(null, advancedGroupStage? Typeface.BOLD : Typeface.NORMAL);
    }

    @Override
    public int getItemCount() {
        return mCountryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void set(@NonNull List<Country> countryList) {
        mCountryList = countryList;
        notifyDataSetChanged();
    }

    public void setPrimaryCountry(Country country) {
        mPrimaryCountry = country;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPosition;
        TextView tvCountryName;
        TextView tvVictories;
        TextView tvDraws;
        TextView tvDefeats;
        TextView tvGoalsFor;
        TextView tvGoalsAgainst;
        TextView tvGoalsDifference;
        TextView tvPoints;
        ImageView ivCountryFlag;

        ViewHolder(View itemView) {
            super(itemView);

            tvPosition = itemView.findViewById(R.id.tv_country_position);
            tvCountryName = itemView.findViewById(R.id.tv_country_name);
            ivCountryFlag = itemView.findViewById(R.id.iv_country_flag);
            tvVictories = itemView.findViewById(R.id.tv_country_victories);
            tvDraws = itemView.findViewById(R.id.tv_country_draws);
            tvDefeats = itemView.findViewById(R.id.tv_country_defeats);
            tvGoalsFor = itemView.findViewById(R.id.tv_country_goals_for);
            tvGoalsAgainst = itemView.findViewById(R.id.tv_country_goals_against);
            tvGoalsDifference = itemView.findViewById(R.id.tv_country_goals_difference);
            tvPoints = itemView.findViewById(R.id.tv_country_points);

        }
    }
}
