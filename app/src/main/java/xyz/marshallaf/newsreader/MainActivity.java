package xyz.marshallaf.newsreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
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
    private SharedPreferences mPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener;
    private int mPage = 1;

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


        newsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // if we're at the bottom of the list
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    // load the next page of results
                    mPage++;
                    getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
                }
            }
        });

        // set the preferences on change listener
        mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.d(LOG_TAG, "preferences have changed");
                mPage = 1;
                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
            }
        };
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mPreferences.registerOnSharedPreferenceChangeListener(mPrefListener);

        // set the click action for floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View prompt = inflater.inflate(R.layout.prompt, null);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setView(prompt);

                final EditText searchTerm = (EditText) prompt.findViewById(R.id.search_prompt_input);

                // set actions on alert dialog
                dialogBuilder
                        .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putString("search_term", searchTerm.getText().toString());
                                editor.commit();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                dialogBuilder.create().show();
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
        // get preferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String orderByPref = mPreferences.getString("order_by", getString(R.string.pref_order_by_default));
        String searchTermPref = mPreferences.getString("search_term", getString(R.string.pref_search_term_default));

        // build the request url
        Uri.Builder builder = Uri.parse(GUARDIAN_BASE_URI).buildUpon();
        builder.appendQueryParameter("order-by", orderByPref);
        builder.appendQueryParameter("show-fields", "trailText%2Cbyline%2Cthumbnail");
        builder.appendQueryParameter("page", Integer.toString(mPage));
        builder.appendQueryParameter("q", searchTermPref);
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

        // if we're loading the first page then we start fresh
        if (mPage == 1) {
            // clear any data currently in the adapter since we have fresh data
            mAdapter.clear();
        }

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
