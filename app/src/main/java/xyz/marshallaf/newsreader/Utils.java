package xyz.marshallaf.newsreader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Static class containing utility functions.
 *
 * Created by Andrew Marshall on 1/16/2017.
 */
public final class Utils {

    private static final String LOG_TAG = Utils.class.getName();

    private static String testJSON = "[{\"id\":\"sport/2017/jan/16/obama-chicago-cubs-white-house-visit\",\"type\":\"article\",\"sectionId\":\"sport\",\"sectionName\":\"Sport\",\"webPublicationDate\":\"2017-01-16T23:11:47Z\",\"webTitle\":\"'Sports has changed attitudes': Obama welcomes Chicago Cubs to White House\",\"webUrl\":\"https://www.theguardian.com/sport/2017/jan/16/obama-chicago-cubs-white-house-visit\",\"apiUrl\":\"https://content.guardianapis.com/sport/2017/jan/16/obama-chicago-cubs-white-house-visit\",\"fields\":{\"trailText\":\"The president is a White Sox fan but he marked the Cubs’ World Series win with delight, celebrating sometimes fragile unity at his final White House function\",\"byline\":\"David Smith in Washington\",\"thumbnail\":\"https://media.guim.co.uk/217fa11dd0753aceeab7cd3a1e0ceb3c129fcbac/0_64_5067_3040/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/everton-tom-davies-leighton-baines-manchester-city\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T22:30:21Z\",\"webTitle\":\"Everton’s Tom Davies confirms Leighton Baines’ view of a proper player\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/everton-tom-davies-leighton-baines-manchester-city\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/everton-tom-davies-leighton-baines-manchester-city\",\"fields\":{\"trailText\":\"How 18-year-old Davies has handled first-team life at Goodison Park has impressed Baines, who has seen other young players be overwhelmed early in their careers\",\"byline\":\"Andy Hunter\",\"thumbnail\":\"https://media.guim.co.uk/848115e926c463a504b17b2fdcf283bb4168024e/0_80_1984_1190/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/sam-allardyce-crystal-palace-fear-failure-relegation-premier-league\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T22:30:21Z\",\"webTitle\":\"Sam Allardyce motivated by fear of failure at Crystal Palace\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/sam-allardyce-crystal-palace-fear-failure-relegation-premier-league\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/sam-allardyce-crystal-palace-fear-failure-relegation-premier-league\",\"fields\":{\"trailText\":\"Sam Allardyce is still searching for his first win after five games at the club, who are one place above the Premier League relegation zone\",\"byline\":\"Dominic Fifield\",\"thumbnail\":\"https://media.guim.co.uk/e8eb23dabf37912c2fa5020f182425c0fba04a59/0_182_4576_2745/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/juventus-unveil-new-crest-serie-a\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T21:57:22Z\",\"webTitle\":\"Juventus unveil bold new club crest at ceremony in Turin\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/juventus-unveil-new-crest-serie-a\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/juventus-unveil-new-crest-serie-a\",\"fields\":{\"trailText\":\"Juventus have unveiled a new club crest – with club president Andrea Agnelli claiming the unusual, minimalist design has been one year in the making\",\"byline\":\"Guardian sport\",\"thumbnail\":\"https://media.guim.co.uk/7bb546b87881548cb3d47cbf7ac5b980b12a568a/37_44_862_517/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/swansea-tom-carroll-martin-olsson-signings\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T21:57:04Z\",\"webTitle\":\"Swansea close in on Tom Carroll and Martin Olsson signings\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/swansea-tom-carroll-martin-olsson-signings\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/swansea-tom-carroll-martin-olsson-signings\",\"fields\":{\"trailText\":\"Tom Carroll is expected to join Swansea City from Tottenham for around £4.5m, with Norwich left-back Martin Olsson also set to arrive in south Wales to bolster their defence\",\"byline\":\"Guardian sport\",\"thumbnail\":\"https://media.guim.co.uk/0ee6951239811344d762c28de7abfea4aa39d616/0_125_2334_1400/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/louis-van-gaal-manchester-united-retire\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T20:49:30Z\",\"webTitle\":\"Ex-Manchester United manager Louis van Gaal to retire for ‘family reasons’\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/louis-van-gaal-manchester-united-retire\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/louis-van-gaal-manchester-united-retire\",\"fields\":{\"trailText\":\"Louis van Gaal has confirmed he intends to retire from coaching despite the former Manchester United manager receiving an offer to manage a club in Asia where he would have earned £44m in three seasons\",\"byline\":\"Guardian sport\",\"thumbnail\":\"https://media.guim.co.uk/f5335bf8bf5620da3d5f18a391cd2e87faaf3d72/0_80_3500_2100/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/lincoln-city-fa-cup-replay-with-ipswich\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T20:36:00Z\",\"webTitle\":\"Lincoln going for gold in lucrative FA Cup replay with Ipswich\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/lincoln-city-fa-cup-replay-with-ipswich\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/lincoln-city-fa-cup-replay-with-ipswich\",\"fields\":{\"trailText\":\"After a draw at Portman Road, Lincoln City take on Ipswich Town in a third round replay, a match eagerly anticipated by the non-league club’s ‘gold members’\",\"byline\":\"Paul MacInnes\",\"thumbnail\":\"https://media.guim.co.uk/2cdf63e1cce77bae141f70a39734041011089bfa/0_190_3619_2172/500.jpg\"},\"isHosted\":false},{\"id\":\"books/2017/jan/16/william-peter-blatty-obituary\",\"type\":\"article\",\"sectionId\":\"books\",\"sectionName\":\"Books\",\"webPublicationDate\":\"2017-01-16T18:45:15Z\",\"webTitle\":\"William Peter Blatty obituary\",\"webUrl\":\"https://www.theguardian.com/books/2017/jan/16/william-peter-blatty-obituary\",\"apiUrl\":\"https://content.guardianapis.com/books/2017/jan/16/william-peter-blatty-obituary\",\"fields\":{\"trailText\":\"Writer best known for his novel The Exorcist, adapted into a hugely successful horror film\",\"byline\":\"Michael Carlson\",\"thumbnail\":\"https://media.guim.co.uk/c264cdf467fcbdc33fa30df20caf9c64c2900cde/188_848_4161_2495/500.jpg\"},\"isHosted\":false},{\"id\":\"football/2017/jan/16/diego-costa-chelsea-antonio-conte\",\"type\":\"article\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T18:00:31Z\",\"webTitle\":\"Diego Costa will not return to Chelsea team unless his attitude improves\",\"webUrl\":\"https://www.theguardian.com/football/2017/jan/16/diego-costa-chelsea-antonio-conte\",\"apiUrl\":\"https://content.guardianapis.com/football/2017/jan/16/diego-costa-chelsea-antonio-conte\",\"fields\":{\"trailText\":\"Antonio Conte has no plans to hold clear-the-air talks with Diego Costa and the onus will be on the forward in training to prove he warrants a swift restoration to the Chelsea side\",\"byline\":\"Dominic Fifield\",\"thumbnail\":\"https://media.guim.co.uk/af5e618cec78a19cc6659da60252dccb7e21724e/26_85_2496_1498/500.jpg\"},\"isHosted\":false},{\"id\":\"football/live/2017/jan/16/ivory-coast-v-togo-afcon-2017-live\",\"type\":\"liveblog\",\"sectionId\":\"football\",\"sectionName\":\"Football\",\"webPublicationDate\":\"2017-01-16T17:57:56Z\",\"webTitle\":\"Ivory Coast 0-0 Togo: Afcon 2017 – as it happened\",\"webUrl\":\"https://www.theguardian.com/football/live/2017/jan/16/ivory-coast-v-togo-afcon-2017-live\",\"apiUrl\":\"https://content.guardianapis.com/football/live/2017/jan/16/ivory-coast-v-togo-afcon-2017-live\",\"fields\":{\"trailText\":\" The defending champions were held to a goalless draw by Togo in a cautious opening match \",\"byline\":\"Rob Smyth\",\"thumbnail\":\"https://media.guim.co.uk/c2ede92fc4b8467e1311f10d745043096dd28cfa/0_20_2556_1534/500.jpg\"},\"isHosted\":false}]";

    public static ArrayList<Article> getArticlesFromJSON() throws JSONException {
        JSONArray results = new JSONArray(testJSON);
        ArrayList<Article> articles = new ArrayList<>(results.length());

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            JSONObject fields = result.getJSONObject("fields");
            String title = result.getString("webTitle");
            String published = result.getString("webPublicationDate");
            String section = result.getString("sectionName");
            String url = result.getString("webUrl");
            String subtitle = fields.getString("trailText");
            String author = fields.getString("byline");
            String imageUrl = fields.getString("thumbnail");
            articles.add(new Article(title, subtitle, author, published, section, url, imageUrl));
        }
        return articles;
    }

    public static URL parseUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Unable to parse url", e);
        }
        return url;
    }

    public static ArrayList<Article> fetchNews() {

    }
}
