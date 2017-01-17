package xyz.marshallaf.newsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getName();

    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsList = (ListView) findViewById(R.id.news_list);

        ArrayList<Article> articles = null;
        try {
            articles = Utils.getArticlesFromJSON();
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON parsing failed", e);
        }

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
}
