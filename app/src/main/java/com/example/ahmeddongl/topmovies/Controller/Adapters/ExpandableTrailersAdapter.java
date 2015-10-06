package com.example.ahmeddongl.topmovies.Controller.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.ahmeddongl.topmovies.R;
import com.example.ahmeddongl.topmovies.Model.Data.Trailer;

import java.util.List;

/**
 * Created by Ahmed Donkl on 9/25/2015.
 */
public class ExpandableTrailersAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Trailer> trailersList;

    public ExpandableTrailersAdapter(Context context, List<Trailer> trailersList) {
        this.context = context;
        this.trailersList = trailersList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return trailersList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Trailer trailerObject = (Trailer) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.trailer_item, null);
        }
        TextView viewTrailerName = (TextView) convertView.findViewById(R.id.trailer_name);
        viewTrailerName.setText(trailerObject.name);
        viewTrailerName.setHint(trailerObject.link);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return trailersList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "Trailers";
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = "Trailers";
        if (convertView == null) {
            LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.expandaple_header_item, null);
        }

        TextView expandableHeader = (TextView) convertView.findViewById(R.id.expanaple_header);
        expandableHeader.setTypeface(null, Typeface.BOLD);
        expandableHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}