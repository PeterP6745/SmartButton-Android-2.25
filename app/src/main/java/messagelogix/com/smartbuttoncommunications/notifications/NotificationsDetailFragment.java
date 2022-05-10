package messagelogix.com.smartbuttoncommunications.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.PushModel;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;

/**
 * Created by Vahid
 * A fragment representing a single PushNotifications detail screen.
 * in two-pane mode (on tablets) or a {@link NotificationsDetailActivity}
 * on handsets.
 */
public class NotificationsDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    public static final String ARG_MESSAGE = "item_message";

    public static final String ARG_DATE = "item_date";

    public static final String ARG_SUBJECT = "item_subject";

    public static final String ARG_FIRST_VIEWED = "item_first_viewed";

    /**
     * The dummy content this fragment is presenting.
     */
    private PushModel.PushItem mItem;

    private String LOG_TAG = NotificationsDetailFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotificationsDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //THIS WILL APPLY TO ITEMS CLICKED FROM LIST OF PUSH NOTIFICATIONS ** WILL OVERRIDE THE ABOVE DATA (mutually exclusive data will not conflict)
        if(getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the push content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = new PushModel().new PushItem();
            mItem.setCampaignId(getArguments().getString(ARG_ITEM_ID));
            mItem.setSubject(getArguments().getString(ARG_SUBJECT));
            mItem.setMessage(getArguments().getString(ARG_MESSAGE));
            mItem.setScheduleDatetime(getArguments().getString(ARG_DATE));
            mItem.setFirstViewed(getArguments().getString(ARG_FIRST_VIEWED));

            LogUtils.debug("pushdetailfrag","arg_item_id in fragment from arguments: "+getArguments().getString(ARG_ITEM_ID));
            LogUtils.debug("pushdetailfrag","subject in fragment from arguments: "+getArguments().getString(ARG_SUBJECT));
            LogUtils.debug("pushdetailfrag","date in fragment from arguments: "+getArguments().getString(ARG_DATE));
            LogUtils.debug("pushdetailfrag","message in fragment from arguments: "+getArguments().getString(ARG_MESSAGE));
            LogUtils.debug("pushdetailfrag","firstviewed in fragment from arguments: "+getArguments().getString(ARG_FIRST_VIEWED));

            LogUtils.debug("NotificationsDetailFrag","arg_item_id in fragment from arguments: "+getArguments().getString(ARG_ITEM_ID));
            LogUtils.debug("NotificationsDetailFrag","subject in fragment from arguments: "+getArguments().getString(ARG_SUBJECT));
            LogUtils.debug("NotificationsDetailFrag","date in fragment from arguments: "+getArguments().getString(ARG_DATE));
            LogUtils.debug("NotificationsDetailFrag","message in fragment from arguments: "+getArguments().getString(ARG_MESSAGE));
            LogUtils.debug("NotificationsDetailFrag","firstviewed in fragment from arguments: "+getArguments().getString(ARG_FIRST_VIEWED));
        } else {
            LogUtils.debug(LOG_TAG, "=========================ITEM IS NULL==============================");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pushnotifications_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogUtils.debug("NotificationsDetailFrag","onViewCreated()");

        CollapsingToolbarLayout appBarLayout = getActivity().findViewById(R.id.toolbar_layout);
        if(appBarLayout != null) {
            boolean inSpanish = ("Administrator Broadcast".equals(mItem.getSubject()) && "es".equals(getResources().getConfiguration().locale.toString()));
            String titleToUse = inSpanish ? "Transmisión del Administrador" : mItem.getSubject();
            appBarLayout.setTitle(titleToUse);

            LogUtils.debug("pushdetailfrag","lang locale is: " + getResources().getConfiguration().locale + " --> title to use is: "+titleToUse);
            LogUtils.debug("NotificationsDetailFrag","lang locale is: " + getResources().getConfiguration().locale + " --> title to use is: "+titleToUse);
        }

        if(mItem != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", getResources().getConfiguration().locale);//Locale.ENGLISH);
            String notifTimestamp = mItem.getScheduleDatetime();
            String firstViewedTimestamp = mItem.getFirstViewed();
            try {
                LogUtils.debug("NotifListFrag","notif timestamp is: "+notifTimestamp);
                Date dateObj1 = sdf.parse(notifTimestamp);
                Date dateObj2 = sdf.parse(firstViewedTimestamp);

                notifTimestamp = getDateString(dateObj1);
                firstViewedTimestamp = getDateString(dateObj2);
            } catch (Exception e) {
                LogUtils.debug("NotifListFrag","Exception thrown while reformatting timestamp for a notification");
            }

            ((TextView) view.findViewById(R.id.notifdetail_timestamp_received)).setText(notifTimestamp);
            ((TextView) view.findViewById(R.id.pushnotifications_detail)).setText(mItem.getMessage());
            ((TextView) view.findViewById(R.id.notifdetail_timestamp_firstviewed)).setText(firstViewedTimestamp);
        }
    }

    private String getDateString(Date dateObj) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy - h:mm:ss");
        String timeString = sdf.format(dateObj);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);

        if(cal.get(Calendar.AM_PM) == Calendar.PM)
            timeString += " p.m.";
        else
            timeString += " a.m.";

        return timeString;
    }
}
