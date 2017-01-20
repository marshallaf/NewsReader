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

public class ImageTask extends AsyncTask<Void, Void, Bitmap> {
    private final String LOG_TAG = ImageTask.class.getName();

    // this reference doesn't prevent it from being garbage collected, if the view is no longer needed
    private final WeakReference<ImageView> mImageViewRef;
    private final Article mArticle;
    private final URL mUrl;


    public ImageTask(URL imageUrl, ImageView imageView, Article article) {
        super();
        mImageViewRef = new WeakReference<ImageView>(imageView);
        mArticle = article;
        mUrl = imageUrl;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        if (mUrl == null) return null;

        return Utils.imageFromInputStream(mUrl, 100, 100);
    }

    @Override
    protected void onPostExecute(Bitmap image) {
        if (image == null) Log.d(LOG_TAG, "onPost image is null");

        if (isCancelled()) image = null;

        if (mImageViewRef != null && image != null) {
            mArticle.setImage(image);
            ImageView imageView = mImageViewRef.get();
            // get the task that is currently associated with the referenced imageview
            ImageTask imageTask = NewsAdapter.getImageTask(imageView);
            // if this task is still the valid one, set the image
            if (this == imageTask && imageView != null)
                imageView.setImageBitmap(image);
        }
    }

    public URL getUrl() {
        return mUrl;
    }
}
