package com.example.androidfinalprojectw18.websterdictionary.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;

import java.util.ArrayList;
import java.util.Locale;

public class DictionaryItemAdapter extends ArrayAdapter<Definition> {

    public DictionaryItemAdapter(Context ctx, ArrayList<Definition> objects) {
        super(ctx, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Definition d = getItem(position);

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dictionary_item, parent, false);
        }

        return convertView;
    }

}
