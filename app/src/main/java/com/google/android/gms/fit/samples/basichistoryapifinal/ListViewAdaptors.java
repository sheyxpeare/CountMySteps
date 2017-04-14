package com.google.android.gms.fit.samples.basichistoryapifinal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by smukhopadhyay on 5/1/16.
 */
public class ListViewAdaptors extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    public ListViewAdaptors(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class viewHolder{
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row, null);
            holder = new viewHolder();

            holder.txtFirst = (TextView) convertView.findViewById(R.id.Ranks);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.friendName);
            holder.txtThird = (TextView) convertView.findViewById(R.id.friendStepCount);

            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        HashMap<String, String> map = list.get(position);
        holder.txtFirst.setText(map.get(ListConstants.FIRST_COLUMN));
        holder.txtSecond.setText(map.get(ListConstants.SECOND_COLUMN));
        holder.txtThird.setText(map.get(ListConstants.THIRD_COLUMN));


        return convertView;
    }
}
