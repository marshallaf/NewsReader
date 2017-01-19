package xyz.marshallaf.newsreader;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A class for storing information about a news article.
 *
 * Created by Andrew Marshall on 1/16/2017.
 */

public class Article {
    // tag for logging
    private final String LOG_TAG = Article.class.getName();

    // pattern of dates returned from Guardian API
    private final String API_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // member variables
    private String mTitle;
    private String mSubtitle;
    private String mAuthor;
    private Date mPublished;
    private String mSection;
    private Uri mArticleUrl;
    private URL mImageUrl;
    private Drawable mImage;

    public Article(String title, String subtitle, String author,
                   String published, String section, String articleUrl, String imageUrl) {
        mTitle = title;
        mSubtitle = subtitle;
        mAuthor = author;
        mPublished = parseDate(published);
        mSection = section;
        mArticleUrl = Uri.parse(articleUrl);
        mImageUrl = Utils.parseUrl(imageUrl);
    }

    private Date parseDate(String dateString) {
        TimeZone tz = TimeZone.getTimeZone("Universal");
        Calendar cal = Calendar.getInstance(tz);
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_PATTERN);
        sdf.setCalendar(cal);
        try {
            cal.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            cal.setTimeInMillis(0);
        }
        return cal.getTime();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Date getPublished() {
        return mPublished;
    }

    public String getSection() {
        return mSection;
    }

    public Uri getArticleUrl() {
        return mArticleUrl;
    }

    public URL getImageUrl() {
        return mImageUrl;
    }

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable image) {
        mImage = image;
    }
}
