package com.example.androidfinalprojectw18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleAdapter extends BaseAdapter {

    private List<ArticleModel> articleModelList;

    private Context context;
    private LayoutInflater inflater;

    public ArticleAdapter(List<ArticleModel> articleModelList, Context context) {
        this.articleModelList = articleModelList;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return articleModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return articleModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
                view = inflater.inflate(R.layout.article_listview_item, null);

            TextView author = (TextView)view.findViewById(R.id.author);
            author.setText(articleModelList.get(position).getAuthor());
            TextView title = (TextView)view.findViewById(R.id.articleTitle);
            title.setText(articleModelList.get(position).getTitle());
            TextView description = (TextView)view.findViewById(R.id.description);
            description.setText(articleModelList.get(position).getDescription());
            ImageView image = (ImageView)view.findViewById(R.id.articleImage);
            Picasso.get().load(articleModelList.get(position).getImgURL()).into(image);

        }
        return view;
    }
}
