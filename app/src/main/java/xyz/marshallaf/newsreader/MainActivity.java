package xyz.marshallaf.newsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    private final String LOG_TAG = MainActivity.class.getName();

    private final String GUARDIAN_BASE_URI = "https://content.guardianapis.com/search";
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsList = (ListView) findViewById(R.id.news_list);

        ArrayList<Article> articles = null;
        //ArrayList<Article> articles = Utils.fetchNews(url);

        mAdapter = new NewsAdapter(this, articles);

        newsList.setAdapter(mAdapter);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(mAdapter.getItem(position).getArticleUrl());
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        // build the request url
        Uri.Builder builder = Uri.parse(GUARDIAN_BASE_URI).buildUpon();
        builder.appendQueryParameter("order-by", "newest");
        builder.appendQueryParameter("show-fields", "trailText%2Cbyline%2Cthumbnail");
        builder.appendQueryParameter("q", "football");
        builder.appendQueryParameter("api-key", getString(R.string.guardian_api_key));

        // create the loader
        return new ArticleLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> results) {
        // clear any data currently in the adapter since we have fresh data
        mAdapter.clear();

        // add the fresh data
        if (results != null && results.size() != 0)
            mAdapter.addAll(results);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // clear any data currently in the adapter
        mAdapter.clear();
    }
}
