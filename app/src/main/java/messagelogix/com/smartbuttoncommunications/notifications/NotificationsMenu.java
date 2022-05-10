package messagelogix.com.smartbuttoncommunications.notifications;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

public class NotificationsMenu extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout viewPushNotificationsCell = view.findViewById(R.id.view_push_notifications_lin_layout);
        viewPushNotificationsCell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DefaultNotificationsList defaultNotifList = new DefaultNotificationsList();

                fragmentTransaction.replace(R.id.notificationsmenu_framelayout, defaultNotifList).addToBackStack(null).commit();
                defaultNotifList.setUserVisibleHint(true);
            }
        });

        LinearLayout sendBroadcastsCell = view.findViewById(R.id.send_notifications_lin_layout);
        sendBroadcastsCell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                SendBroadcastForm sendBroadcastForm = new SendBroadcastForm();

                fragmentTransaction.replace(R.id.notificationsmenu_framelayout, sendBroadcastForm).addToBackStack(null).commit();
                sendBroadcastForm.setUserVisibleHint(true);
            }
        });

        TextView sendBroadcastSubtitle = view.findViewById(R.id.sendBroadcastSubtitle);
        if(Preferences.getBoolean(Config.PUSH_TO_APP_ONLY)){
            sendBroadcastSubtitle.setText(getString(R.string.Send_Broadcasts_To_Mobile_App));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
