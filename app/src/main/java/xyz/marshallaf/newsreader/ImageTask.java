package xyz.marshallaf.newsreader;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by Andrew Marshall on 1/18/2017.
 */

public class ImageTask extends AsyncTask<URL, Void, Bitmap> {
    private final String LOG_TAG = ImageTask.class.getName();

    // this reference doesn't prevent it from being garbage collected, if the view is no longer needed
    private final WeakReference<ImageView> mImageView;
    private final Article mArticle;

    public ImageTask(ImageView imageView, Article article) {
        super();
        mImageView = new WeakReference<ImageView>(imageView);
        mArticle = article;
    }

    @Override
    protected Bitmap doInBackground(URL... url) {
        if (url == null) return null;

        // get required height/width


        return Utils.imageFromInputStream(url[0], 100, 100);
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        if (image == null) Log.d(LOG_TAG, "onPost image is null");
        if (mImageView != null && image != null) {
            mArticle.setImage(image);
            ImageView imageView = mImageView.get();
            if (imageView != null)
                imageView.setImageBitmap(image);
        }
    }
}
