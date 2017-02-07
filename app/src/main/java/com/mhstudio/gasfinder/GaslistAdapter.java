package com.mhstudio.gasfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by mahmudul on 1/16/17.
 */

public class GaslistAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<GasModel.GasEntries> mDataList;
    private GasModel mModel;

    public GaslistAdapter(Context context, ArrayList<GasModel.GasEntries> data, GasModel leModel){
        mContext = context;
        mDataList = data;
        mModel = leModel;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gasstation_list_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GasModel.GasEntries entry = (GasModel.GasEntries) getItem(position);
        viewHolder.gasName.setText(entry.getName());
        viewHolder.gasVicinity.setText(entry.getVicinity());

        Double leDist = distFrom(mModel.getLattitude(), mModel.getLongitude(), entry.getLat(), entry.getLng());
        viewHolder.distance.setText(String.valueOf(leDist) + " miles");

        return convertView;
    }

    public Double distFrom(Double lat1, Double lng1, Double lat2, Double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (earthRadius * c);

        DecimalFormat df = new DecimalFormat("#.0");
        return Double.valueOf(df.format(dist*0.000621371192));
    }

    private class ViewHolder{
        TextView gasName;
        TextView gasVicinity;
        TextView distance;

        public ViewHolder(View view){
            gasName = (TextView) view.findViewById(R.id.row_station_name);
            gasVicinity = (TextView) view.findViewById(R.id.row_station_address);
            distance = (TextView) view.findViewById(R.id.row_station_dist);
        }
    }
}
