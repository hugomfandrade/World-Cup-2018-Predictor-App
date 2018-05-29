package org.hugoandrade.worldcup2018.predictor.view.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.hugoandrade.worldcup2018.predictor.R;
import org.hugoandrade.worldcup2018.predictor.common.VerticalLinearLayoutManager;

import java.util.List;

public class FilterPopup extends PopupWindow {

    private final Context mContext;
    private final int mDarkColor;
    private final int mWhiteColor;
    private final View mParentView;
    private final List<String> mFilterList;
    private final int mTheme;
    private int mStartingPosition;
    private int mMaxRows;
    private int mMinFilter = -1;
    private int mMaxFilter = -1;

    private OnFilterItemClickedListener mListener;

    public FilterPopup(View view, List<String> filterList, int startingPosition) {
        this(view, filterList, startingPosition, 0);
    }

    public FilterPopup(View view, List<String> filterList, int startingPosition, int maxRows) {
        this(view, filterList, startingPosition, maxRows, FilterTheme.LIGHT);
    }

    public FilterPopup(View view, List<String> filterList, int startingPosition, int maxRows, int theme) {
        super(View.inflate(view.getContext(), R.layout.layout_popup, null),
                view.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        mContext = view.getContext();
        mParentView = view;
        mFilterList = filterList;
        mMaxRows = maxRows;
        mTheme = theme;
        mStartingPosition = startingPosition;

        mDarkColor = mContext.getResources().getColor(R.color.colorMain);
        mWhiteColor = Color.WHITE;
    }

    private void initializeUI(int startingPosition) {

        RecyclerView rvFilter = getContentView().findViewById(R.id.rv_filter);
        rvFilter.setLayoutManager(new VerticalLinearLayoutManager(mContext));
        rvFilter.setAdapter(new FilterListAdapter());

        if (mMaxRows > 0) {
            int rows = mFilterList.size() < mMaxRows ? mFilterList.size() : mMaxRows;
            ViewGroup.LayoutParams params = rvFilter.getLayoutParams();
            params.height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    rows * 40,
                    mContext.getResources().getDisplayMetrics());
            rvFilter.setLayoutParams(params);
        }
        rvFilter.scrollToPosition(startingPosition);

        showAsDropDown(mParentView, 0,0);
    }

    public void setOnFilterItemClickedListener(OnFilterItemClickedListener listener) {
        mListener = listener;
    }

    public void setMaxRows(int maxRows) {
        mMaxRows = maxRows;
    }

    public void build() {
        initializeUI(mStartingPosition);
    }

    public void setMax(int maxFilter) {
        mMaxFilter = maxFilter;
    }

    public void setMin(int minFilter) {
        mMinFilter = minFilter;
    }

    public interface OnFilterItemClickedListener {
        void onFilterItemClicked(int position);
    }

    private class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.ViewHolder> {

        FilterListAdapter() { }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater vi = LayoutInflater.from(parent.getContext());
            return new ViewHolder(vi.inflate(R.layout.list_item_filter, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

            String filter = mFilterList.get(holder.getAdjustedAdapterPosition());

            holder.tvFilter.setText(filter);

            holder.tvFilter.setTextColor(mTheme == FilterTheme.DARK ? mWhiteColor : mDarkColor);
            holder.itemView.setBackgroundColor(mTheme == FilterTheme.DARK ? mDarkColor : mWhiteColor);
        }

        @Override
        public int getItemCount() {
            int size = mFilterList.size();

            if (mMinFilter != -1) {
                if (mMinFilter >= mFilterList.size()) {
                    return 0;
                }
                else
                    size = size - mMinFilter;
            }

            if (mMaxFilter != -1) {
                if (mMaxFilter >= mFilterList.size()) {
                    return size;
                }
                if (mMinFilter != -1) {
                    if (mMinFilter > mMaxFilter) {
                        return 0;
                    } else {
                        return mMaxFilter - mMinFilter + 1;
                    }
                }
                else {
                    return mMaxFilter + 1;
                }
            }

            return size;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFilter;

            ViewHolder(View itemView) {
                super(itemView);

                tvFilter = itemView.findViewById(R.id.tv_filter);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onFilterItemClicked(getAdjustedAdapterPosition());
                        dismiss();
                    }
                });
            }

            private int getAdjustedAdapterPosition() {
                int pos = getAdapterPosition();
                if (mMinFilter != -1)
                    pos = pos + mMinFilter;
                return pos;
            }
        }
    }
}
