package messagelogix.com.smartbuttoncommunications.activities.conversations;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import messagelogix.com.smartbuttoncommunications.R;

public class ConversationsMenu extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversations_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout twoWayChatCell = view.findViewById(R.id.two_way_chat_lin_layout);
        twoWayChatCell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DefaultChatList defaultChatList = new DefaultChatList();

                fragmentTransaction.replace(R.id.conversationsmenu_activity_framelayout, defaultChatList).addToBackStack(null).commit();
            }
        });

        LinearLayout signalRChatCell = view.findViewById(R.id.signalr_chat_lin_layout);
        signalRChatCell.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                SignalRGroupList signalRGroupList = new SignalRGroupList();

                fragmentTransaction.replace(R.id.conversationsmenu_activity_framelayout, signalRGroupList).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
