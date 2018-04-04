package uk.ac.kent.fb224.mpchecker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tree1 on 02/04/2018.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder>{
    private Context context;
    public ArrayList<News> NewsList = new ArrayList<>();
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Title;
        TextView Date;
        TextView Category;
        private View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ViewHolder.this.getLayoutPosition();
                News news = NewsList.get(position);
                Intent Browser = new Intent(Intent.ACTION_VIEW, Uri.parse(news.URL));
                context.startActivity(Browser);
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);

            Title = itemView.findViewById(R.id.NewsTitle);
            Date = itemView.findViewById(R.id.NewsDateTime);
            Category = itemView.findViewById(R.id.NewsCategory);
            this.itemView.setOnClickListener(onClick);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.news_card_layout, parent, false);
        NewsListAdapter.ViewHolder vh = new NewsListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    News news = NewsList.get(position);
    holder.Title.setText(news.Name);
    holder.Category.setText(news.Category);
    holder.Date.setText(news.Date);
    }

    @Override
    public int getItemCount() {return NewsList.size();}


}
