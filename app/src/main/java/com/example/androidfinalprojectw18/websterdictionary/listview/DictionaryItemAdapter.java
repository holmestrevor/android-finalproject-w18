package com.example.androidfinalprojectw18.websterdictionary.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;

import java.util.ArrayList;

public class DictionaryItemAdapter extends ArrayAdapter<DictionaryItem> {

    /**
     * Constructor for DictionaryItemAdapter. Takes a context object and an arraylist of DictionaryItem objects as arguments.
     * @param ctx
     * @param objects Arraylist of DictionaryItem objects to be passed.
     */
    public DictionaryItemAdapter(Context ctx, ArrayList<DictionaryItem> objects) {
        super(ctx, 0, objects);
    }

    /**
     * Manages how the DictionaryItem objects will be interpreted and placed into the ListView.
     * @param position Position of element
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DictionaryItem d = getItem(position);

        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dictionary_item, parent, false);
        }

        TextView word = (TextView)convertView.findViewById(R.id.word);
        TextView pronunciation = (TextView)convertView.findViewById(R.id.pronunciation);
        TextView definition = (TextView)convertView.findViewById(R.id.definition);

        if(position%2==0) {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorMedGray, null));
        } else {
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.colorDarkGray, null));
        }

        word.setText(d.getWord());
        pronunciation.setText(d.getPronunciation());
        //Formatting the definitions
        String definitions = "";
        for(int i=0; i<d.getDefinitions().length; i++) {
            if(d.getDefinitions()[i]==null) {
                break;
            }
            definitions += (d.getDefinitions()[i] + "\n");
        }
        definition.setText(definitions);

        return convertView;
    }

}
