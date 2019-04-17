package com.example.androidfinalprojectw18.newyorktimes;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<StoryModel> storyModelArrayList;
    private AdapterCallback mAdapterCallback;

    //constructor
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
            holder.bookmark = (ImageButton) convertView.findViewById(R.id.bookmarkBtn);
            holder.bookmark.setTag("add");
            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) convertView.getTag();
        }

        //setup layout
        Picasso.get().load(storyModelArrayList.get(position).getImageURL()).into(holder.image);
        holder.title.setText(storyModelArrayList.get(position).getTitle());
        holder.author.setText(storyModelArrayList.get(position).getAuthor());
        //if the id not null, means the story have added to bookmark, then change the button color
        if (storyModelArrayList.get(position).getId() != null) {
            holder.bookmark.setImageResource(R.drawable.ic_ny_bookmark_off);
            holder.bookmark.setTag("remove");
        }

        //bookmark button action
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(holder.bookmark.getTag());
                if (!holder.bookmark.getTag().equals("remove")) {
                    storyModelArrayList.get(position).setId(mAdapterCallback.addToBookmark(storyModelArrayList.get(position)));
                    holder.bookmark.setTag("remove");
                    holder.bookmark.setImageResource(R.drawable.ic_ny_bookmark_off);
                    Snackbar sb = Snackbar.make(v, R.string.ny_adapter_add, Snackbar.LENGTH_LONG);
                    sb.setAction(R.string.ny_adapter_goto, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent bookmarkPage = new Intent(context, Bookmark.class);
                            context.startActivity(bookmarkPage);
                        }
                    }).show();

                } else {
                    mAdapterCallback.removeFromBookmark(storyModelArrayList.get(position).getId());
                    holder.bookmark.setTag("add");
                    System.out.println(holder.bookmark.getTag());
                    holder.bookmark.setImageResource(R.drawable.ic_ny_bookmark_on);
                    Toast.makeText(context, R.string.ny_adapter_remove, Toast.LENGTH_LONG).show();

                }
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
        long addToBookmark(StoryModel storyModel);

        void removeFromBookmark(long id);
    }
}
