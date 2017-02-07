package com.mhstudio.gasfinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahmudul on 12/28/16.
 */

public class GasModel implements Serializable {
    private Double mLattitude;
    private Double mLongitude;
    private ArrayList<GasEntries> mData;

    public GasModel(){
        mData = new ArrayList<>();
        mLattitude = null;
        mLongitude = null;
    }

    public void setLattitude(Double lat){
        mLattitude = lat;
    }
    public Double getLattitude(){
        return mLattitude;
    }

    public void setLongitude(Double lng){
        mLongitude = lng;
    }
    public Double getLongitude(){
        return mLongitude;
    }

    public GasEntries getNewEntry(){
        return new GasEntries();
    }

    public void setEntry(GasEntries entry){
        if(mData != null){
            mData.add(entry);
        }
    }

    public ArrayList<GasEntries> getEntryList(){
        return mData;
    }
    public void resetEntryList(){
        mData.clear();
    }

    public class GasEntries implements Serializable {
        private String mName;
        private String mVicinity;
        private Double lat, lng;

        GasEntries(){
            mName = null;
            mVicinity = null;
            lat = null;
            lng = null;
        }

        public void setName(String name){
            mName = name;
        }
        public String getName(){
            return mName;
        }

        public void setVicinity(String vic){
            mVicinity = vic;
        }
        public String getVicinity(){
            return mVicinity;
        }

        public void setLat(Double leLat){
            lat = leLat;
        }
        public Double getLat(){
            return lat;
        }

        public void setLng(Double leLng){
            lng = leLng;
        }
        public Double getLng(){
            return lng;
        }
    }
}
