package dev.votu.rfatvapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dev.votu.rfatvapp.R;

public final class TagListAdapter extends ArrayAdapter<TagListEntry> {

    public TagListAdapter(Context context, ArrayList<TagListEntry> tagListEntry) {
        super(context, R.layout.tag_list_row, tagListEntry);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TagListEntry tagListEntry = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_list_row, parent, false);
        }
        TextView textView;
        textView = convertView.findViewById(R.id.tagid);
        textView.setText(tagListEntry.mTagID);
        textView = convertView.findViewById(R.id.tagcount);
        textView.setText(String.valueOf(tagListEntry.mCount));

        return convertView;
    }
}



