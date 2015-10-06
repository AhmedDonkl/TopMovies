package com.example.ahmeddongl.topmovies.Controller.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.ahmeddongl.topmovies.R;
import com.example.ahmeddongl.topmovies.Model.Data.Review;

import java.util.List;

/**
 * Created by Ahmed Donkl on 9/25/2015.
 */
public class ExpandableReviewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Review> reviewsList;

    public ExpandableReviewAdapter(Context context, List<Review> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reviewsList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Review reviewObject = (Review) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.review_item, null);
        }
        TextView viewReviewAuthor = (TextView) convertView.findViewById(R.id.review_author);
        TextView viewReviewContent = (TextView) convertView.findViewById(R.id.review_content);

        viewReviewAuthor.setText(reviewObject.author);
        viewReviewContent.setText(reviewObject.content);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return reviewsList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "Reviews";
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
        String headerTitle = "Reviews";
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