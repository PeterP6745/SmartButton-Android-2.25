package messagelogix.com.smartbuttoncommunications.activities.help;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.MainActivity;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by Vahid
 * This is taking care of the text parts of the help section
 */
public class TextActivity extends AppCompatActivity implements MaterialShowcaseSequence.OnSequenceItemDismissedListener, MaterialShowcaseSequence.OnSequenceItemShownListener {

    private int demoCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        String value = getIntent().getExtras().getString("value");
        String title = getIntent().getExtras().getString("title");
        setTitle(title);
        TextView helpTextView = (TextView) findViewById(R.id.textView);
        if (helpTextView != null) {
            helpTextView.setText(Html.fromHtml(value));
            helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        Preferences.init(this);
        buildActionBar();
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void goBack() {

        super.onBackPressed();
    }

    public void buildActionBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

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
    public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {

        if (demoCounter == 1) {
            final TabBarActivity activity = (TabBarActivity) this.getParent();
            TabHost host = activity.getTabHost();
            host.setCurrentTab(1);
        } else {
            demoCounter--;
        }
    }

    @Override
    public void onShow(MaterialShowcaseView materialShowcaseView, int i) {

        final TabBarActivity activity = (TabBarActivity) this.getParent();
        TabHost host = activity.getTabHost();
        host.getTabWidget().setVisibility(View.GONE);
    }
}
