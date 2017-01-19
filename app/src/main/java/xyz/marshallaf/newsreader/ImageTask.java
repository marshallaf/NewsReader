package xyz.marshallaf.newsreader;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Andrew Marshall on 1/18/2017.
 */

public class ImageTask extends AsyncTask<URL, Void, Drawable> {
    private final String LOG_TAG = ImageTask.class.getName();

    private final ImageView mImageView;
    private final Article mArticle;

    public ImageTask(ImageView imageView, Article article) {
        super();
        mImageView = imageView;
        mArticle = article;
    }

    @Override
    protected Drawable doInBackground(URL... url) {
        if (url == null) return null;

        Drawable image = null;
        try {
            InputStream stream = (InputStream) url[0].getContent();
            image = Drawable.createFromStream(stream, null);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to fetch image from " + url[0].toString());
        }
        return image;
    }

    @Override
    protected void onPostExecute(Drawable image) {
        mArticle.setImage(image);
        mImageView.setImageDrawable(image);
    }
}
