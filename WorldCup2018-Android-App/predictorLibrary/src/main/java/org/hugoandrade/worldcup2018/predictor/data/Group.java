package org.hugoandrade.worldcup2018.predictor.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Group implements Parcelable {

    private final String mGroupLetter;
    private ArrayList<Country> mCountryList;

    public Group(String groupLetter) {
        mGroupLetter = groupLetter;
        mCountryList = new ArrayList<>();
    }

    public void setCountryList(ArrayList<Country> countryList) {
        mCountryList = countryList;
    }

    public List<Country> getCountryList() {
        return mCountryList;
    }

    public void add(Country c) {
        mCountryList.add(c);
    }

    protected Group(Parcel in) {
        mGroupLetter = in.readString();
        mCountryList = in.createTypedArrayList(Country.CREATOR);
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mGroupLetter);
        dest.writeTypedList(mCountryList);
    }
}
