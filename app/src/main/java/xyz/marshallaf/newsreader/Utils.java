package xyz.marshallaf.newsreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Static class containing utility functions.
 *
 * Created by Andrew Marshall on 1/16/2017.
 */
public final class Utils {

    private static final String LOG_TAG = Utils.class.getName();

    public static ArrayList<Article> getArticlesFromJSON(String jsonString) throws JSONException {
        // nothing to parse
        if (jsonString == null) return null;

        JSONObject data = new JSONObject(jsonString);
        JSONArray results = data.getJSONObject("response").getJSONArray("results");
        ArrayList<Article> articles = new ArrayList<>(results.length());

        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject result = results.getJSONObject(i);
                JSONObject fields = result.optJSONObject("fields");
                String title = result.getString("webTitle");
                String published = result.getString("webPublicationDate");
                String section = result.getString("sectionName");
                String url = result.getString("webUrl");
                String subtitle = "";
                String author = "";
                String imageUrl = "";
                if (fields != null) {
                    subtitle = fields.optString("trailText");
                    author = fields.optString("byline");
                    imageUrl = fields.optString("thumbnail");
                }
                articles.add(new Article(title, subtitle, author, published, section, url, imageUrl));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Unable to parse single article");
            }
        }
        return articles;
    }

    public static URL parseUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Unable to parse url");
        }
        return url;
    }

    public static ArrayList<Article> fetchNews(String urlString) {
        URL url = parseUrl(urlString);

        // if url is null do nothing
        if (url == null) return null;

        // make api request
        String jsonString = null;
        try {
            jsonString = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem closing stream", e);
        }

        // parse json
        ArrayList<Article> articles = null;
        try {
            articles = getArticlesFromJSON(jsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Unable to parse JSON result", e);
        }

        return articles;
    }

    /**
     * Method to open a connection to a URL and return the data received as a {@link String}.
     *
     * @param url {@link URL} to make the connection to
     * @return data received from URL
     * @throws IOException if the InputStream generated could not be closed
     */
    private static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection connection = null;
        InputStream stream = null;
        String jsonString = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("GET");
            connection.connect();

            // check response code
            if (connection.getResponseCode() == 200) {
                // if good, start reading
                stream = connection.getInputStream();
                jsonString = readFromInputStream(stream);
            } else {
                Log.e(LOG_TAG, "Unsuccessful response code " + connection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem establishing connection", e);
        } finally {
            if (connection != null) connection.disconnect();
            if (stream != null) stream.close();
        }

        return jsonString;
    }

    /**
     * Reads UTF-8 characters from an {@link InputStream}.
     *
     * @param stream {@link InputStream} from which to read
     * @return {@link String} containing the UTF-8 characters
     */
    private static String readFromInputStream(InputStream stream) {
        // if stream is null do nothing
        if (stream == null) return null;

        InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            // while there is still input, continue reading/appending the lines
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading data stream", e);
        }

        return sb.toString();
    }

    public static Bitmap imageFromInputStream(URL url, int reqWidth, int reqHeight) {
        InputStream dimStream = null;
        InputStream decodeStream = null;

        // get streams
        try {
            dimStream = (InputStream) url.getContent();
            decodeStream = (InputStream) url.getContent();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Unable to fetch image from " + url.toString());
        }

        if (dimStream == null || decodeStream == null) return null;

        // first get the dims of the image
        BitmapFactory.Options options = new BitmapFactory.Options();
        // returns only image properties, no actual bitmap
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(dimStream, null, options);

        // calculate the desired in sample size
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // decode bitmap to be just large enough
        options.inJustDecodeBounds = false;
        Bitmap image = BitmapFactory.decodeStream(decodeStream, null, options);

        // close streams
        try {
            dimStream.close();
            decodeStream.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing image stream");
        }

        return image;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // width and height of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        Log.d(LOG_TAG, "width " + width + ", height " + height);
        // set to values greater than 1, it downsamples the image when decoding
        int inSampleSize = 1;

        // if the raw dims are too big
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            // decoder rounds down to nearest power of 2, so no use in being more granular
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
