package messagelogix.com.smartbuttoncommunications.activities.conversations;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;
import messagelogix.com.smartbuttoncommunications.utils.TitleTracker;

public class SignalRGroupList extends Fragment {
    TitleTracker titleTrackerInterface;

    private ArrayList<String> bNameList = new ArrayList<>();
    private List<String> bIdList = new ArrayList<>();
    private ArrayList<String> activeAlertLocations = new ArrayList<>();
    private ListView mListView;

    CustomProgressDialog activityProgressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        titleTrackerInterface = (TitleTracker) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.e_2way_conversations);
        titleTrackerInterface.addToTitleStack(getString(R.string.e_2way_conversations));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signalr_grouplist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = view.findViewById(R.id.signalr_chatlist);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBuildingsList();
    }

    public void getBuildingsList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetBuildingsSb");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("typeId", "0");

        final ApiHelper apiHelper = new ApiHelper();

        activityProgressDialog = new CustomProgressDialog(getActivity());
        activityProgressDialog.showDialog(getActivity().getResources().getString(R.string.generic_progressdialog_mess1));

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LogUtils.debug("RebuildSignalR","SignalRGroupList - getBuildingsList() - response: "+response);
                    LogUtils.debug("getbuilding1","in here");
                    JSONObject jsonobject = new JSONObject(response);
                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        ArrayList<String> nameList = new ArrayList<>();
                        ArrayList<String> idList = new ArrayList<>();

                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            if(Preferences.getBoolean(Config.IS_SUPER_USER)) {
                                nameList.add(jsonobject.optString("value"));
                                idList.add(jsonobject.optString("id"));
                            }  else {
                                String mBuildingId = Preferences.getString(Config.USER_BUILDING_ID);
                                if(mBuildingId.equals(jsonobject.optString("id"))){
                                    nameList.add(jsonobject.optString("value"));
                                    idList.add(jsonobject.optString("id"));
                                }
                            }
                        }

                        bNameList = nameList;
                        bIdList = idList;

                        getActiveBroadcastCount();
                    } else {
                        activityProgressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    activityProgressDialog.dismiss();
                    LogUtils.debug("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activityProgressDialog.dismiss();
            }
        });

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void getActiveBroadcastCount() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetBuildingAlertCount");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));

        final ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LogUtils.debug("RebuildSignalR","SignalRGroupList - getActiveBroadcastCount() - response: "+response);
                    JSONObject jsonobject = new JSONObject(response);
                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        ArrayList<String> activeLocationsList = new ArrayList<>();

                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            int alertCount = Integer.parseInt(jsonobject.optString("active_alerts"));
                            if (alertCount > 0) {
                                activeLocationsList.add(jsonobject.optString("sch_name"));
                            }
                        }

                        activeAlertLocations = activeLocationsList;

                        ConversationsMenuAdapter arrayAdapter = new ConversationsMenuAdapter(getActivity(), bNameList, activeAlertLocations);

                        mListView.setAdapter(arrayAdapter);
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Bundle bundle = new Bundle();
                                bundle.putString("locationId", bIdList.get(position));
                                bundle.putString("locationName", bNameList.get(position));

                                Fragment signalRChatWindow = new SignalRChatWindow();
                                signalRChatWindow.setArguments(bundle);
                                FragmentManager fragmentManager = getActivity().getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.conversationsmenu_activity_framelayout, signalRChatWindow).addToBackStack(null).commit();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                activityProgressDialog.dismiss();
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activityProgressDialog.dismiss();
            }
        });

        apiHelper.prepareRequest(params,false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }
}
