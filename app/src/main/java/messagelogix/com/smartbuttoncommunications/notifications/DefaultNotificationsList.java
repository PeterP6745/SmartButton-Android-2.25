package messagelogix.com.smartbuttoncommunications.notifications;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.activities.core.TabBarActivity;
import messagelogix.com.smartbuttoncommunications.model.PushModel;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.RetryCounter;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DefaultNotificationsList extends Fragment {
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private PushModel listOfPushMessages;

    private DefaultNotificationsList.SimpleItemRecyclerViewAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.debug("fragment","inside onAttach()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Preferences.getBoolean(Config.HAS_DESKTOPS)){
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(false); // disable the button
                actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
                //actionBar.setDisplayShowHomeEnabled(false); // remove the icon
            }
        }

//        if (getView().findViewById(R.id.pushnotifications_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w900dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            mTwoPane = true;
//        }

        LogUtils.debug("NotifListFrag","onCreate()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notiflist_default, container, false);
        if (view.findViewById(R.id.pushnotifications_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.debug("NotifListFrag","onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(NotifListReceiver, new IntentFilter("reloadNotifList"));
        getNotifList();
        LogUtils.debug("NotifListFrag","calling on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(NotifListReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.debug("NotifListFrag","onStop()");
    }

    private void getNotifList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "GreenCow");
        params.put("action", "GetPushAlerts2");
        params.put("contactid", Preferences.getString(Config.CONTACT_ID));

        final ApiHelper apiHelper = new ApiHelper();

        final CustomProgressDialog progressDialog = new CustomProgressDialog(getActivity());
        progressDialog.showDialog(getActivity().getResources().getString(R.string.notiflist_progressdialog1));

        final RetryCounter retryCounter = new RetryCounter();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    LogUtils.debug("getnotiflist","responsedata is: "+response);
                    Gson gson = new GsonBuilder().create();
                    listOfPushMessages = gson.fromJson(response, PushModel.class);
                    if(listOfPushMessages.getSuccess()) {
                        calcTotalUnreadNotifs();
                        View recyclerView = getView().findViewById(R.id.pushnotifications_list);
                        assert recyclerView != null;

                        //DefaultNotificationsList.SimpleItemRecyclerViewAdapter.ViewHolder temp = new DefaultNotificationsList.SimpleItemRecyclerViewAdapter.ViewHolder();
                        //RecyclerView temp = new RecyclerView();
                        setupRecyclerView((RecyclerView) recyclerView);
                        recyclerView.setVisibility(View.VISIBLE);
                        TextView emptyView = getView().findViewById(R.id.empty_view);
                        assert emptyView != null;
                        emptyView.setVisibility(View.GONE);
                        LogUtils.debug("getnotiflist","responsedata has a data key and success is true.");
                    } else {
                        View recyclerView = getView().findViewById(R.id.layout_child_1);

                        assert recyclerView != null;
                        recyclerView.setVisibility(View.GONE);
                        TextView emptyView = getView().findViewById(R.id.empty_view);
                        assert emptyView != null;
                        emptyView.setVisibility(View.VISIBLE);
                        LogUtils.debug("getnotiflist","responsedata has a data key and success is false.");
                    }
                } catch (Exception e) {
                    LogUtils.debug("getnotiflist","an exception was thrown while processing the responsedata");
                    showErrorPrompt();
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int currRC = retryCounter.getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();//ApiHelper.getInstance(HomeActivity.this).getRetryCount();
                LogUtils.debug("getnotiflist","retried: " + apiHelper.getUrl() +  " backend call\n" + currRC + " times. Received an error code from the server -> "+error);

                if(currRC == 0) {
                    progressDialog.dismiss();
                    showErrorPrompt();
                }
                else {
                    retryCounter.decrementRetryCount();
                    //LogUtils.debug("covid19surveytask","retryPolicy of temp obj: "+temp.getRetryPolicy().toString());
                    ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
                }
                LogUtils.debug("getnotiflist","an error was encountered while trying to establish a connection");
            }
        });

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void showErrorPrompt() {
        Toast.makeText(getActivity(), R.string.notiflist_errormess1, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser) {
//            progress = new ProgressDialog(contextOfFragment);
//            progress.setMessage("Getting push messages...");
//            progress.setIndeterminate(false);
//            progress.setCancelable(false);
//            progress.show();
            LogUtils.debug("fragment","fragment is now visible to user");
//            if(contextOfFragment == null) {
//                LogUtils.debug("fragment","context is null, setting runduringattach to true");
//                //runDuringAttach = true;
//            }
//            else {
//                Log.d("fragment","calling getpushalerts() inside visible()");
//                //new GetPushAlertsTasks().execute();
//                GetNotifList();
//            }
        }
    }

    private BroadcastReceiver NotifListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent (not necessary)
            getNotifList();
            Log.d("reloading","Default Notif list was reloaded");
        }
    };

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new DefaultNotificationsList.SimpleItemRecyclerViewAdapter(listOfPushMessages.getData());
        recyclerView.setAdapter(adapter);
    }

    private void calcTotalUnreadNotifs() {
        List<PushModel.PushItem> notifList;
        notifList = listOfPushMessages.getData();

        Integer totalUnreadNotifs = 0;
        for(int i=0; i<notifList.size(); i++) {
            Boolean notifSeen = "1".equals(notifList.get(i).wasItemSeen());
            if(!notifSeen)
                totalUnreadNotifs += 1;
        }
        LogUtils.debug("listofPushMessages",""+totalUnreadNotifs);

        TabBarActivity.updateTabBadgeCount(totalUnreadNotifs.toString(), 1, false);
        //Log.d("listofPushMessages",""+notifList.get(1).getMessage());
    }

    public String getDateString(Date dateObj) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy - h:mm:ss");
        String timeString = sdf.format(dateObj);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);

        if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            timeString += " p.m.";
        } else {
            timeString += " a.m.";
        }

        return timeString;
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<DefaultNotificationsList.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PushModel.PushItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<PushModel.PushItem> items) {

            mValues = items;
        }

        @Override
        public DefaultNotificationsList.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.notificationcell_withindicator, parent, false);
            return new DefaultNotificationsList.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DefaultNotificationsList.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            PushModel.PushItem notifItem = mValues.get(position);
            holder.mItem = notifItem;//mValues.get(position);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", getResources().getConfiguration().locale);//Locale.ENGLISH);
            Date dateObj = null;
            String notifTimestamp = notifItem.getScheduleDatetime();
            try {
                LogUtils.debug("NotifListFrag","notif timestamp is: "+notifTimestamp);
                dateObj = sdf.parse(notifTimestamp);
                notifTimestamp = getDateString(dateObj);

            } catch (Exception e) {
                LogUtils.debug("NotifListFrag","Exception thrown while reformatting timestamp for a notification");
            }

            holder.mIdView.setText(notifTimestamp);
            //Calendar cal = Calendar.getInstance(getResources().getConfiguration().locale);
            //cal.setTimeInMillis(Long.valueOf(notifItem.getScheduleDatetime()));
            //String date = DateFormat.format("MMM d, yyy h:mma", cal).toString();
            //holder.mIdView.setText(date);
            //holder.mIdView.setText(notifItem.getScheduleDatetime());

            holder.mContentView.setText(notifItem.getMessage());
            holder.unreadIndicator.setVisibility(View.INVISIBLE);

            boolean wasItemSeen = "1".equals(notifItem.wasItemSeen());
            if(wasItemSeen == false) {
                holder.unreadIndicator.setVisibility(View.VISIBLE);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(NotificationsDetailFragment.ARG_ITEM_ID, holder.mItem.getCampaignId());
                        arguments.putString(NotificationsDetailFragment.ARG_DATE, holder.mItem.getScheduleDatetime());
                        arguments.putString(NotificationsDetailFragment.ARG_MESSAGE, holder.mItem.getMessage());
                        arguments.putString(NotificationsDetailFragment.ARG_SUBJECT, holder.mItem.getSubject());
                        NotificationsDetailFragment fragment = new NotificationsDetailFragment();
                        fragment.setArguments(arguments);
                        ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.pushnotifications_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, NotificationsDetailActivity.class);
                        intent.putExtra(NotificationsDetailFragment.ARG_ITEM_ID, holder.mItem.getCampaignId());
                        intent.putExtra(NotificationsDetailFragment.ARG_DATE, holder.mItem.getScheduleDatetime());
                        intent.putExtra(NotificationsDetailFragment.ARG_MESSAGE, holder.mItem.getMessage());
                        intent.putExtra(NotificationsDetailFragment.ARG_SUBJECT, holder.mItem.getSubject());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;

            public final TextView mIdView;

            public final TextView mContentView;

            public final ImageView unreadIndicator;

            public PushModel.PushItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                unreadIndicator = (ImageView) view.findViewById(R.id.notiflist_imageview_unreadindicator);
                //if(view != null) {
                //unreadIndicator.setVisibility(View.INVISIBLE);
                //}
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
