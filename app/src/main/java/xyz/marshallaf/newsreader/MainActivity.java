package xyz.marshallaf.newsreader;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    private final String LOG_TAG = MainActivity.class.getName();

    private final String GUARDIAN_BASE_URI = "https://content.guardianapis.com/search";
    private NewsAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get references to layout items
        mProgressBar = (ProgressBar) findViewById(R.id.list_progress);
        mInfoText = (TextView) findViewById(R.id.list_info);
        ListView newsList = (ListView) findViewById(R.id.news_list);

        // initialize and bind an adapter for the articles
        // adapter is empty for now, but loader will populate it
        mAdapter = new NewsAdapter(this, new ArrayList<Article>());
        newsList.setAdapter(mAdapter);

        // set the click action for each list article item
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(mAdapter.getItem(position).getArticleUrl());
                startActivity(intent);
            }
        });

        // check for internet connection
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {  // there is a connection
            // start the loader
            // id = 0 (there's only 1 loader so it doesn't matter)
            // LoaderCallbacks are implemented here, so (this)
            getSupportLoaderManager().initLoader(0, null, this);
        } else {  // there is not a connection
            // remove progress bar
            mProgressBar.setVisibility(View.GONE);

            // set no connection text
            mInfoText.setText("No internet connection.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // check if it's the settings menu item
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }

        // otherwise call through to the base class
        return super.onOptionsItemSelected(item);
    }

    // Loader callbacks

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
        // remove the loading indicator
        mProgressBar.setVisibility(View.GONE);

        // set no data text
        mInfoText.setText("No articles found.");

        // clear any data currently in the adapter since we have fresh data
        mAdapter.clear();

        // add the fresh data
        if (results != null && results.size() != 0) {
            mInfoText.setVisibility(View.GONE);
            mAdapter.addAll(results);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // clear any data currently in the adapter
        mAdapter.clear();
    }

    // end of loader callbacks
}
