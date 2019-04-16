package com.example.androidfinalprojectw18.newyorktimes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StoryModel> storyModelArrayList;
    private AdapterCallback mAdapterCallback;

    public StoryAdapter(Context context, ArrayList<StoryModel> storyModelArrayList) {
        this.context = context;
        this.storyModelArrayList = storyModelArrayList;
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }


    @Override
    public int getCount() {
        return storyModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return storyModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.newyork_row_story, null, true);

            holder.image = (ImageView) convertView.findViewById(R.id.rowImage);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.headLine = (TextView) convertView.findViewById(R.id.headLine);
            holder.bookmark = (ImageButton)convertView.findViewById(R.id.bookmarkBtn);
            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        Picasso.get().load(storyModelArrayList.get(position).getImageURL()).into(holder.image);
        holder.title.setText(storyModelArrayList.get(position).getTitle());
        holder.author.setText(storyModelArrayList.get(position).getAuthor());
        holder.headLine.setText(storyModelArrayList.get(position).getHeadLine());
        holder.bookmark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println(storyModelArrayList.get(position).getTitle());
                try {
                    mAdapterCallback.increaseCounter();
                    mAdapterCallback.setupBadge();
                } catch (ClassCastException exception) {
                    // do something
                }
                holder.bookmark.setImageResource(R.drawable.ic_ny_bookmark_off);
            }

        });

        return convertView;
    }

    private class ViewHolder {

        protected TextView title, author, headLine;
        protected ImageView image;
        protected ImageButton bookmark;
    }

    public interface AdapterCallback {
        void setupBadge();
        void increaseCounter();
    }
}
