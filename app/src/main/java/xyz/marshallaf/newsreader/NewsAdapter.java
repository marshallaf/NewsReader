package xyz.marshallaf.newsreader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * An ArrayAdapter for the Article class.
 *
 * Created by Andrew Marshall on 1/16/2017.
 */

public class NewsAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Article> mArticles;

    public NewsAdapter(Context context, List articles) {
        super(context, 0, articles);
        mContext = context;
        mArticles = articles;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        // recycle the view
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.news_list_item, parent, false);
        }

        // set text views

        // set image view
    }
}
