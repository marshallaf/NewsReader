package xyz.marshallaf.newsreader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Simple loader class to load {@link Article}s asynchronously.
 *
 * Created by Andrew Marshall on 1/17/2017.
 */
public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    private String mUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        // prompt the loader to begin an async load
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) return null;

        return Utils.fetchNews(mUrl);
    }
}
