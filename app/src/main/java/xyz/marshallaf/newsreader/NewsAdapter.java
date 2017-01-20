package xyz.marshallaf.newsreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An ArrayAdapter for the Article class.
 *
 * Created by Andrew Marshall on 1/16/2017.
 */

public class NewsAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Article> mArticles;

    public NewsAdapter(Context context, List articles) {
        super(context, 0, articles);
        mContext = context;
        mArticles = articles;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        // recycle the view
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.news_list_item, parent, false);
        }

        // get current item
        Article article = mArticles.get(position);

        // set text views
        ((TextView) view.findViewById(R.id.item_title)).setText(article.getTitle());
        ((TextView) view.findViewById(R.id.item_subtitle)).setText(article.getSubtitle());
        ((TextView) view.findViewById(R.id.item_section)).setText(article.getSection());
        ((TextView) view.findViewById(R.id.item_author)).setText(article.getAuthor());

        // convert article date to local time then set text view
        Date published = article.getPublished();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mma z, d MMM yyyy");
        ((TextView) view.findViewById(R.id.item_date)).setText(sdf.format(published));

        // set image view
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        // check if we already have an image
        Bitmap image = article.getImage();
        if (image != null) {
            imageView.setImageBitmap(image);
        } else {  // no cached image, download it
            URL imageUrl = article.getImageUrl();
            if (imageUrl != null) {
                loadImage(imageUrl, imageView, article);
                //new ImageTask(imageView, article).execute(imageUrl);
            }
        }

        return view;
    }

    @Nullable
    @Override
    public Article getItem(int position) {
        return mArticles.get(position);
    }

    private void loadImage(URL imageUrl, ImageView imageView, Article article) {
        if (newWorkNeeded(imageUrl, imageView)) {
            ImageTask imageTask = new ImageTask(imageUrl, imageView, article);
            AsyncDrawable asyncDrawable = new AsyncDrawable(imageUrl, imageTask);
            imageView.setImageDrawable(asyncDrawable);
            imageTask.execute();
        }
    }

    private boolean newWorkNeeded(URL imageUrl, ImageView imageView) {
        ImageTask imageTask = getImageTask(imageView);

        if (imageTask != null) {
            URL taskUrl = imageTask.getUrl();
            // if task's url is not set or it is different
            if (taskUrl == null || taskUrl.equals(imageUrl)) {
                // cancel the previous task
                imageTask.cancel(true);
            } else {
                // it's asking to do the same work, let the original task continue
                return false;
            }
        }

        return true;
    }

    public static ImageTask getImageTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getImageTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends ShapeDrawable {
        private final WeakReference<ImageTask> imageTaskRef;

        public AsyncDrawable(URL imageUrl, ImageTask imageTask) {
            super();
            imageTaskRef = new WeakReference<>(imageTask);
        }

        public ImageTask getImageTask() {
            return imageTaskRef.get();
        }
    }
}
