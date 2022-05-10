package messagelogix.com.smartbuttoncommunications.activities.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.MainActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

/**
 * Created by Vahid
 * This is the activity for showing the Youtube videos
 */
public class YoutubeActivity extends YouTubeBaseActivity {

    private Context context = this;

    /**
     * on Options Item Selected
     *
     * @param item is the menu item
     * @return true or false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                goBack();
                return true;
            case R.id.action_logout:
                Preferences.putBoolean(Config.IS_LOGGED_IN, false);
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goBack() {

        super.onBackPressed();
    }

    /**
     * on Create Options Menu
     *
     * @param menu object
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds options to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                goBack();
            }
        });
        new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {

            @Override
            public void initialize(HttpRequest hr) throws IOException {

            }
        }).setApplicationName(this.getString(R.string.app_name)).build();
        YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player_view);
        playerView.initialize(Config.YOUTUBE_DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                if (!b) {
                    String videoId = getIntent().getExtras().getString("url");
                    String title = getIntent().getExtras().getString("title");
                    youTubePlayer.cueVideo(videoId);
                    setTitle(title);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_LONG).show();
            }
        });
    }
}
