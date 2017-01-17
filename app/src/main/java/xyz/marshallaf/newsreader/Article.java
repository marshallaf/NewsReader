package xyz.marshallaf.newsreader;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class for storing information about a news article.
 *
 * Created by Andrew Marshall on 1/16/2017.
 */

public class Article {
    // tag for logging
    private final String LOG_TAG = Article.class.getName();

    // member variables
    private String mTitle;
    private String mSubtitle;
    private String mAuthor;
    private String mPublished;
    private String mSection;
    private URL mArticleUrl;
    private URL mImageUrl;

    public Article(String title, String subtitle, String author,
                   String published, String section, String articleUrl, String imageUrl) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthor = author;
        mPublished = published;
        mSection = section;
        mArticleUrl = parseUrl(articleUrl);
        mImageUrl = parseUrl(imageUrl);
    }

    private URL parseUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Unable to parse url", e);
        }
        return url;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmSubtitle() {
        return mSubtitle;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmPublished() {
        return mPublished;
    }

    public String getmSection() {
        return mSection;
    }

    public URL getmArticleUrl() {
        return mArticleUrl;
    }

    public URL getmImageUrl() {
        return mImageUrl;
    }
}
