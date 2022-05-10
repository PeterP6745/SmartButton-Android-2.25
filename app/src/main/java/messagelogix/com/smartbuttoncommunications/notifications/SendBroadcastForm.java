package messagelogix.com.smartbuttoncommunications.notifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.TitleTracker;
import messagelogix.com.smartbuttoncommunications.model.SpinnerItem_Building;
import messagelogix.com.smartbuttoncommunications.model.SpinnerItem_Incidents;
import messagelogix.com.smartbuttoncommunications.model.SpinnerItem_UserTypes;
import messagelogix.com.smartbuttoncommunications.utils.ApiHelper;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.CustomProgressDialog;
import messagelogix.com.smartbuttoncommunications.utils.CustomSpinnerAdapter_SelectIncident;
import messagelogix.com.smartbuttoncommunications.utils.FunctionHelper;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

public class SendBroadcastForm extends Fragment {
    TitleTracker titleTrackerInterface;

    private Button btnSendAlert;
    private Button btnClearAlert;
    private Button btnSelectBuildings;
    private Button btnSelectUserGroups;

    private Spinner spinnerIncidents;
    private EditText etAlertMessage;

    private CheckBox checkboxSendToApps;
    private CheckBox checkboxSendToDesks;

    private List<String> buildingIds = new ArrayList<>();
    private ArrayList<String> buildingNames = new ArrayList<>();
    private List<String> selectedBuildingIds = new ArrayList<>();
    private List<String> selectedBuildingNames = new ArrayList<>();
    private boolean[] defaultBuildingArrangement;

    private List<String> userGroupIds = new ArrayList<>();
    private ArrayList<String> userGroupNames = new ArrayList<>();
    private List<String> selectedUserGroupIds = new ArrayList<>();
    private List<String> selectedUserGroupNames = new ArrayList<>();
    private boolean[] defaultUserGroupArrangement;

    private String selectedBuildingName = "";

    private final String userGroupButtonDefaultText = "Select User Type Group(s)";

    private static final int MAX_CHAR_COUNT = 255;
    private TextView broadcastCounterLabel;

    private boolean pushToAppOnly = false;

    private Handler spinnerItemsHandler = new Handler();

    CustomProgressDialog activityProgressIndicator;
    CustomProgressDialog broadcastActionProgressIndicator;

    boolean errorWithActivityProgress = false;
    Vector<Integer> errorLoadingCondition = new Vector<>();

    boolean incidentsCompleted = false;
    boolean buildingsCompleted = false;
    boolean userGroupsCompleted = false;

    String defaultSelectedUserGroup;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        titleTrackerInterface = (TitleTracker) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.broadcast_title);
        titleTrackerInterface.addToTitleStack(getString(R.string.broadcast_title));

        pushToAppOnly = Preferences.getBoolean(Config.PUSH_TO_APP_ONLY);

        defaultSelectedUserGroup = returnStringFromID(R.string.broadcast_contacts_defaultmess1)+" Default Emergency Contact";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LogUtils.debug("RebuildSignalR","SendBroadcastForm - onCreateView()");
        return inflater.inflate(R.layout.activity_send_broadcast, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.debug("RebuildSignalR","SendBroadcastForm - onActivityCreated()");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.debug("RebuildSignalR","SendBroadcastForm - onViewCreated()");
        initComponents();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.debug("RebuildSignalR","SendBroadcastForm - onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.debug("RebuildSignalR","SendBroadcastForm - onResume()");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.debug("RebuildSignalR","SendBroadcastForm - onStop()");
        spinnerItemsHandler.removeCallbacks(spinnerItemsTicker);
    }

    private void initComponents() {
        btnSendAlert = getView().findViewById(R.id.btn_send_sb_alerts);
        btnClearAlert = getView().findViewById(R.id.btn_clear_sb_alerts);
        btnSelectBuildings = getView().findViewById(R.id.btn_select_sb_groups);
        btnSelectBuildings.setText(returnStringFromID(R.string.broadcast_locationgroups_defaultmess1));

        btnSelectUserGroups = getView().findViewById(R.id.btn_select_sb_usergroups);
        btnSelectUserGroups.setText(defaultSelectedUserGroup);

        broadcastCounterLabel = getView().findViewById(R.id.broadcastCounterLabel);
        //set the counter label --> Red color for text between parenthesis
        String charCount = "(" + MAX_CHAR_COUNT + ")";
        broadcastCounterLabel.setText(charCount);

        etAlertMessage = getView().findViewById(R.id.et_sb_alert_message);
        //set the max char length
        etAlertMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHAR_COUNT)});
        //set the edit text textChangedListener to update the char count
        etAlertMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Update the counter
                String charRemaining = String.valueOf(MAX_CHAR_COUNT - etAlertMessage.getText().length());
                //set the counter label --> Red color for text between parenthesis
                broadcastCounterLabel.setText("("+charRemaining+")");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        checkboxSendToApps = getView().findViewById(R.id.checkbox_send2apps);
        checkboxSendToDesks = getView().findViewById(R.id.checkbox_send2desk);

        spinnerIncidents = getView().findViewById(R.id.spinner_sb_desktop_incidents);

        //if push to app only --> hide the checkboxes and user type spinner
        if (pushToAppOnly) {
            checkboxSendToApps.setVisibility(View.GONE);
            checkboxSendToDesks.setVisibility(View.GONE);

            checkboxSendToApps.setChecked(true);
        }

        initComponentEvents();
    }

    private void initComponentEvents(){
        /*SEND BUTTON CLICK*/
        btnSendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageBroadcastActions();
            }
        });

        /*CLEAR BUTTON CLICK*/
        btnClearAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedBuildingIds.size() > 1){
                    for(int i = 0; i<selectedBuildingIds.size(); i++){
                        clearRSSFeed(selectedBuildingIds.get(i));
                    }
                } else if(selectedBuildingIds.size() == 0) {
                    clearRSSFeed("0");
                } else {
                    clearRSSFeed(selectedBuildingIds.get(0));
                }

                resetSelectedContent();
            }
        });

        /*Select Groups Click*/
        btnSelectBuildings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBuildings();
            }
        });

        btnSelectUserGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUserGroups();
            }
        });

        loadSpinnerItems();
    }

    private void loadSpinnerItems(){
        LogUtils.debug("AsyncHandler","loadSpinnerItems() --> adding spinnerItemsHandler.post()");
        spinnerItemsHandler.post(spinnerItemsTicker);

        activityProgressIndicator = new CustomProgressDialog(getActivity());
        activityProgressIndicator.showDialog(getString(R.string.broadcast_loading1));

        incidentsCompleted = false;
        buildingsCompleted = false;
        userGroupsCompleted = false;

        //load incident messages list
        getIncidentsTask();
        //load school buildings list
        getBuildingsTask();
        //load user groups list
        getUserGroupsTask();
    }

    private void manageBroadcastActions(){
        //grab data before resetting content after validation
        String alert = etAlertMessage.getText().toString();

        //validate alert message
        boolean alertIsValid = validateAlert(alert);
        if(alertIsValid) {
            if(broadcastActionProgressIndicator == null)
                broadcastActionProgressIndicator = new CustomProgressDialog(getActivity());

            broadcastActionProgressIndicator.showDialog(getString(R.string.generic_loading3));

            String buildIds = TextUtils.join(",", selectedBuildingIds);
            String userGroups = TextUtils.join(",", selectedUserGroupIds);

            //check checkboxes
            boolean desktopsSelected = !pushToAppOnly && checkboxSendToDesks.isChecked();
            boolean appsSelected = pushToAppOnly || checkboxSendToApps.isChecked();

            Log.d("selectedGroups","userGroupIds - " + userGroups);
            Log.d("selectedGroups", "locationgroupIds - " + buildIds);

            Log.d("AsyncHandler","userGroupIds - " + userGroups);
            Log.d("AsyncHandler", "locationgroupIds - " + buildIds);

            if(appsSelected && desktopsSelected)
                broadcastActionProgressIndicator.setDialogMessage(getString(R.string.broadcast_loading5));
            else if(appsSelected || pushToAppOnly)
                broadcastActionProgressIndicator.setDialogMessage(getString(R.string.broadcast_loading4));
            else
                broadcastActionProgressIndicator.setDialogMessage(getString(R.string.broadcast_loading6));

            sendEmergencyBroadcast(desktopsSelected, appsSelected, alert, buildIds, userGroups);
//            checkSelectedGroups(buildIds,userGroups);
        }
    }

    private void selectBuildings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.broadcast_locationgroups_dialogtitle1));

        // add a checkbox list
        builder.setMultiChoiceItems(buildingNames.toArray(new CharSequence[buildingNames.size()]), defaultBuildingArrangement, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedIndex, boolean isChecked) {
                // user checked or unchecked a box
                if(isChecked) {
                    if(selectedIndex == 0) {
                        selectedBuildingIds.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        selectedBuildingNames.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        defaultBuildingArrangement[0] = true;

                        for(int i = 1; i< buildingNames.size(); i++) {
                            if(!selectedBuildingIds.contains(buildingIds.get(i))) {
                                selectedBuildingIds.add(buildingIds.get(i));
                                selectedBuildingNames.add(buildingNames.get(i));
                                defaultBuildingArrangement[i] = true;
                            }

                            ((AlertDialog) dialog).getListView().setItemChecked(i, true);
                        }
                    } else {
                        String locationGroupId = buildingIds.get(selectedIndex);
                        String locationGroupName = buildingNames.get(selectedIndex);
                        selectedBuildingIds.add(locationGroupId);
                        selectedBuildingNames.add(locationGroupName);
                        defaultBuildingArrangement[selectedIndex] = true;

                        boolean allLocationsSelected = selectedBuildingIds.size() == buildingIds.size() - 1;
                        if(allLocationsSelected) {
                            Log.d("locGroup","all locations were selected manually: "+allLocationsSelected);
                            defaultBuildingArrangement[0] = true;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, true);

                            selectedBuildingIds.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                            selectedBuildingNames.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        }
                    }
                } else {
                    if(selectedIndex == 0) {
                        defaultBuildingArrangement[0] = false;
                        for(int i = 1; i<buildingNames.size(); i++) {
                            defaultBuildingArrangement[i] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                        }

                        selectedBuildingIds.clear();
                        selectedBuildingNames.clear();
                    } else {
                        int indexOfLocationGroupId = selectedBuildingIds.indexOf(buildingIds.get(selectedIndex));
                        int indexOfLocationGroupName = selectedBuildingNames.indexOf(buildingNames.get(selectedIndex));
                        selectedBuildingIds.remove(indexOfLocationGroupId);
                        selectedBuildingNames.remove(indexOfLocationGroupName);
                        defaultBuildingArrangement[selectedIndex] = false;

                        boolean allLocationsSelected = selectedBuildingIds.contains(returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        Log.d("locGroup","top option is selected: "+allLocationsSelected);
                        if(allLocationsSelected) {
                            defaultBuildingArrangement[0] = false;
                            ((AlertDialog) dialog).getListView().setItemChecked(0, false);
                            selectedBuildingIds.remove(0);
                            selectedBuildingNames.remove(0);
                        }
                    }
                }
                Log.d("locGroup","selectedLocationGroupIds: "+ selectedBuildingIds.toString());
                Log.d("locGroup","selectedLocationGroupNames: "+ selectedBuildingNames.toString());

                String displayText;
                if(selectedBuildingIds.size() == 0) {
                    displayText = returnStringFromID(R.string.broadcast_locationgroups_defaultmess1);
                    selectedBuildingName = displayText;
                }
                else if(selectedBuildingIds.size() == buildingIds.size()){
                    displayText = returnStringFromID(R.string.broadcast_locationgroups_defaultmess2);
                    selectedBuildingName = displayText;
                }
                else if(selectedBuildingIds.size()==1){
                    displayText = selectedBuildingNames.get(0);
                    selectedBuildingName = displayText;
                }
                else {
                    displayText = returnStringFromID(R.string.broadcast_locationgroups_defaultmess3);
                    selectedBuildingName = displayText;
                }

                btnSelectBuildings.setText(displayText);
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton(R.string.generic_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("locationGroup","selectedLocationGroupIds: "+ selectedBuildingIds);
                Log.d("locationGroup","selectedLocationGroupNames: "+ selectedBuildingNames);
            }
        });

        builder.setNeutralButton(R.string.broadcast_button_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        // create and show the alert dialog
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i< buildingNames.size(); i++) {
                    defaultBuildingArrangement[i] = false;
                    dialog.getListView().setItemChecked(i,false);
                }

                selectedBuildingIds.clear();
                selectedBuildingNames.clear();
                Log.d("clearSelection","locationGroupIds: "+ selectedBuildingIds.toString());

                btnSelectBuildings.setText(returnStringFromID(R.string.broadcast_locationgroups_defaultmess1));
            }
        });
    }

    private void selectUserGroups() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(returnStringFromID(R.string.broadcast_contacts_dialogtitle1));

        // add a checkbox list
        builder.setMultiChoiceItems(userGroupNames.toArray(new CharSequence[userGroupNames.size()]), defaultUserGroupArrangement, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedIndex, boolean isChecked) {
                // user checked or unchecked a box
                if(selectedIndex == 0) {
                    ((AlertDialog) dialog).getListView().setItemChecked(selectedIndex, true);
                    return;
                }

                if(isChecked) {
                    String userGroupId = userGroupIds.get(selectedIndex);
                    String userGroupName = userGroupNames.get(selectedIndex);

                    selectedUserGroupIds.add(userGroupId);
                    selectedUserGroupNames.add(userGroupName);
                    defaultUserGroupArrangement[selectedIndex] = true;
                } else {
                    int indexOfUserGroupId = selectedUserGroupIds.indexOf(userGroupIds.get(selectedIndex));
                    int indexOfUserGroupName = selectedUserGroupNames.indexOf(userGroupNames.get(selectedIndex));

                    selectedUserGroupIds.remove(indexOfUserGroupId);
                    selectedUserGroupNames.remove(indexOfUserGroupName);
                    defaultUserGroupArrangement[selectedIndex] = false;
                }

                LogUtils.debug("UserGroupsBroadcast","selectUserGroups() --> selectedUserGroupIds = "+selectedUserGroupIds);
                String displayText = selectedUserGroupIds.size() == 1 ? defaultSelectedUserGroup : returnStringFromID(R.string.broadcast_contacts_defaultmess3);
                btnSelectUserGroups.setText(displayText);
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton(R.string.generic_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("onClick","selectedUsrTypeIds: "+ selectedUserGroupIds.toString());
                Log.d("onClick","selectedUsrTypenames: "+ selectedUserGroupNames.toString());
            }
        });

//        builder.setNeutralButton(R.string.broadcast_button_clear, (dialog, which) -> {});
        builder.setNeutralButton(R.string.broadcast_button_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        // create and show the alert dialog
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                ((AlertDialog) dialog1).getListView().setItemChecked(0, true);
                ((AlertDialog) dialog1).getListView().getChildAt(0).setBackgroundColor(Color.rgb(125,210,125));
            }
        });
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=1; i<defaultUserGroupArrangement.length;i++) {
                    Log.d("forloop","inside forloop");
                    defaultUserGroupArrangement[i] = false;
                    dialog.getListView().setItemChecked(i,false);
                }
                selectedUserGroupIds = selectedUserGroupIds.subList(0,1);
                selectedUserGroupNames = selectedUserGroupNames.subList(0,1);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                LogUtils.debug("UserGroupsBroadcast","inside dialog.setOnCancelListener()");
                String displayText = selectedUserGroupIds.size() == 1 ? defaultSelectedUserGroup : returnStringFromID(R.string.broadcast_contacts_defaultmess3);
                btnSelectUserGroups.setText(displayText);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                LogUtils.debug("UserGroupsBroadcast","inside dialog.setOnDismissListener()");
                String displayText = selectedUserGroupIds.size() == 1 ? defaultSelectedUserGroup : returnStringFromID(R.string.broadcast_contacts_defaultmess3);
                btnSelectUserGroups.setText(displayText);
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == android.R.id.home) {
//            getActivity().onBackPressed();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private Boolean validateAlert(String alertMessage) {
        if(selectedBuildingIds.size() > 0 && selectedBuildingIds.get(0).equals(returnStringFromID(R.string.broadcast_locationgroups_defaultmess2))) {
            Log.d("sendingBroadcast","before removing first index: "+ selectedBuildingIds);
            selectedBuildingIds.remove(0);
            selectedBuildingNames.remove(0);
            Log.d("sendingBroadcast","after removing first index: "+ selectedBuildingIds);
        }

        //check if message is empty
        boolean validAlert = !FunctionHelper.isNullOrEmpty(alertMessage);
        /*--1) Check if alert is entered--*/
        if(validAlert){
            //alert is valid, check if an option has been selected
            boolean optionSelected = checkboxSendToDesks.isChecked() || checkboxSendToApps.isChecked();
            if(pushToAppOnly){
                optionSelected = true;
            }

            /*--2) Check if option is selected--*/
            if(optionSelected){
                boolean isBuildingValid = selectedBuildingIds.size() > 0;
                /*--3) Check if group is selected--*/
                if(isBuildingValid){
                    return true;
                } else {
                    Toast.makeText(getActivity(), this.getResources().getString(R.string.broadcast_locationgroups_errormess1), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                //display a message to user about no option selected
                Toast.makeText(getActivity(), this.getResources().getString(R.string.broadcast_locationgroups_errormess2), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            //display a message to user about an empty message
            Toast.makeText(getActivity(), this.getResources().getString(R.string.broadcast_locationgroups_errormess3), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void resetActivityContent() {
        buildingIds = new ArrayList<>();
        buildingNames = new ArrayList<>();
        userGroupIds = new ArrayList<>();
        userGroupNames = new ArrayList<>();
        selectedUserGroupIds = new ArrayList<>();
        selectedUserGroupNames = new ArrayList<>();
    }

    private void resetSelectedContent(){
        etAlertMessage.setText("");

        //if push to app only --> hide the checkboxes and user type spinner
        if(pushToAppOnly) {
            checkboxSendToApps.setChecked(true);
            checkboxSendToDesks.setChecked(false);
        } else {
            checkboxSendToApps.setChecked(false);
            checkboxSendToDesks.setChecked(false);
        }

        btnSelectBuildings.setText(getString(R.string.broadcast_locationgroups_defaultmess1));
        btnSelectUserGroups.setText(defaultSelectedUserGroup);

        //reset arrays
        selectedBuildingIds = new ArrayList<>();
        selectedBuildingNames = new ArrayList<>();

        if(buildingIds.size() > 0) {
            defaultBuildingArrangement = new boolean[buildingIds.size()];
            Arrays.fill(defaultBuildingArrangement, false);
        } else {
            defaultBuildingArrangement = new boolean[]{};
        }

        selectedUserGroupNames = new ArrayList<>();
        if(userGroupNames.size() > 0)
            selectedUserGroupNames.add(userGroupNames.get(0));

        selectedUserGroupIds = new ArrayList<>();
        selectedUserGroupIds.add("1");

        if(userGroupIds.size() > 0) {
            defaultUserGroupArrangement = new boolean[userGroupIds.size()];
            for(int i=0; i<defaultUserGroupArrangement.length; i++) {
                defaultUserGroupArrangement[i] = i == 0;
            }
        }
        else
            defaultUserGroupArrangement = new boolean[]{};

        spinnerIncidents.setSelection(0);

        //Single Selection reset (early version)
        selectedBuildingName = "";
    }

    private void getIncidentsTask() {
        final Context that = getActivity();
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetLocatorMessagesSB");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));

        ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonobject = new JSONObject(response);

                    LogUtils.debug("SendBroadcastForm","getIncidentTypesTask() --> response: "+response);

                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        final ArrayList<SpinnerItem_Incidents> spinnerItems = new ArrayList<>();
                        ArrayList<String> messageList = new ArrayList<>();

                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for(int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            String iMessage = jsonobject.getString("message");

                            spinnerItems.add(new SpinnerItem_Incidents(iMessage));
                            messageList.add(jsonobject.optString("message"));
                        }

                        messageList.add(0, getString(R.string.broadcast_cannedmess_defaultmess1));
                        spinnerItems.add(0,new SpinnerItem_Incidents(getString(R.string.broadcast_cannedmess_defaultmess1)));

                        spinnerIncidents.setAdapter(new CustomSpinnerAdapter_SelectIncident(that, messageList));
                        spinnerIncidents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                String messageName = spinnerItems.get(position).getMessage();

                                if(!messageName.equals(getString(R.string.broadcast_cannedmess_defaultmess1))){
                                    etAlertMessage.setText(messageName);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {}
                        });
                    } else
                        errorLoadingReportSettings(0);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    errorLoadingReportSettings(0);
                }

                incidentsCompleted = true;
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorLoadingReportSettings(0);
                incidentsCompleted = true;
            }
        });

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void getBuildingsTask() {
        ApiHelper apiHelper = new ApiHelper();

        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetBuildingsSb");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("typeId", "0");

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<SpinnerItem_Building> groups = new ArrayList<>();
                    ArrayList<String> nameList = new ArrayList<>();
                    ArrayList<String> idList = new ArrayList<>();

                    JSONObject jsonobject = new JSONObject(response);

                    LogUtils.debug("SendBroadcastForm","getBuildingsTask() --> response: "+response);

                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("value");
                            String id = jsonobject.getString("id");

                            groups.add(new SpinnerItem_Building(name,id));
                            buildingIds.add(id);
                            buildingNames.add(name);

                            // Populate spinner
                            nameList.add(jsonobject.optString("value"));
                            idList.add(jsonobject.optString("id"));
                        }

                        nameList.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        idList.add(0, "0");

                        //spinner items
                        groups.add(0, new SpinnerItem_Building(nameList.get(0), idList.get(0)));
                        buildingIds.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        buildingNames.add(0,returnStringFromID(R.string.broadcast_locationgroups_defaultmess2));
                        defaultBuildingArrangement = new boolean[buildingIds.size()];
                        Arrays.fill(defaultBuildingArrangement,false);

                        //give the group list the groups items
//                    groupList = groups;

                        Log.d("fromBackend","locationGroupIds: "+ buildingIds);
                        Log.d("fromBackend","locationGroupNames: "+ buildingNames);
                    } else
                        errorLoadingReportSettings(1);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    errorLoadingReportSettings(1);
                }

                buildingsCompleted = true;
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorLoadingReportSettings(1);
                buildingsCompleted = true;
            }
        });

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void getUserGroupsTask() {
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "BlueDove");
        params.put("action", "GetContactTypeSB");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));

        ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonobject = new JSONObject(response);

                    LogUtils.debug("SendBroadcastForm","getUserTypesTask() --> response: "+response);

                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        ArrayList<SpinnerItem_UserTypes> groups = new ArrayList<>();
                        ArrayList<String> nameList = new ArrayList<>();
                        ArrayList<String> idList = new ArrayList<>();

                        JSONArray jsonarray = jsonobject.getJSONArray("data");
                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);

                            String name = jsonobject.getString("value");
                            String id = jsonobject.getString("id");

                            groups.add(new SpinnerItem_UserTypes(name,id));

                            // Populate spinner
                            userGroupIds.add(jsonobject.optString("id"));
                            userGroupNames.add(jsonobject.optString("value"));

                            nameList.add(jsonobject.optString("value"));
                            idList.add(jsonobject.optString("id"));
                        }

                        nameList.add(0, defaultSelectedUserGroup);
                        idList.add(0, "");

                        //give the group list the groups items
                        Collections.swap(userGroupIds, 0 , userGroupIds.indexOf("1"));
                        Collections.swap(userGroupNames, 0 , userGroupNames.indexOf("Default Emergency Contact"));
                        selectedUserGroupIds.add(userGroupIds.get(0));
                        selectedUserGroupNames.add(userGroupNames.get(0));

                        defaultUserGroupArrangement = new boolean[userGroupIds.size()];
                        for(int i=0; i<defaultUserGroupArrangement.length; i++) {
                            defaultUserGroupArrangement[i] = i == 0;
                        }
                    } else
                        errorLoadingReportSettings(2);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    errorLoadingReportSettings(2);
                }

                userGroupsCompleted = true;
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorLoadingReportSettings(2);
                userGroupsCompleted = true;
            }
        });

        apiHelper.prepareRequest(params, false);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void checkSelectedGroups(String buildingIds, String userGroups) {
        broadcastActionProgressIndicator.dismiss();
        LogUtils.debug("SendBroadcastForm","checkSelectedGroups() - selectedBuildingIds are: "+selectedBuildingIds+"\nselectedBuildingNames are: "+selectedBuildingNames+"\nuserGroups are: "+selectedUserGroupNames);
        LogUtils.debug("SendBroadcastForm","checkSelectedGroups() - passed selectedBuildingIds are: "+buildingIds+"\nselectedBuildingNames are: "+selectedBuildingNames+"\npassed userGroups are: "+userGroups);
        resetSelectedContent();
    }

    private void sendEmergencyBroadcast(boolean sendToDesktops, boolean sendToApps, String alertMessage, String buildingIds, String userGroups) {
        final Context that = getActivity();
        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "GreenCow");
        params.put("action", "SendSBBroadcastMaster");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("pin_id", "0");
        params.put("title", "Administrator Broadcast");
        params.put("message", alertMessage);
        params.put("link", "");
        params.put("image_url", "");
        params.put("building_id", buildingIds);
        params.put("contact_message_type", userGroups);
        params.put("alert_desktop", sendToDesktops ? "1" : "0");
        params.put("alert_app", sendToApps ? "1" : "0");

        ApiHelper apiHelper = new ApiHelper();
        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                resetSelectedContent();
                broadcastActionProgressIndicator.dismiss();
                try {
                    JSONObject jsonobject = new JSONObject(response);

                    LogUtils.debug("SendBroadcastForm","sendEmergencyBroadcast() --> response: "+response);

                    boolean success = jsonobject.getBoolean("success");
                    if(success)
                        Toast.makeText(that, that.getResources().getString(R.string.broadcast_generic_successmess1), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(that, that.getResources().getString(R.string.broadcast_generic_errormess1), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(that, that.getResources().getString(R.string.broadcast_generic_errormess1), Toast.LENGTH_LONG).show();
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                broadcastActionProgressIndicator.dismiss();
                Toast.makeText(that, that.getResources().getString(R.string.broadcast_generic_errormess1), Toast.LENGTH_LONG).show();
            }
        });

        apiHelper.prepareRequest(params, true);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private void clearRSSFeed(String buildingId) {
        final Context that = getActivity();
        final CustomProgressDialog progressDialog = new CustomProgressDialog(that);
        progressDialog.showDialog("Clearing your RSS Feed, please wait...");

        HashMap<String, String> params = new HashMap<>();
        params.put("controller", "WhiteZebra");
        params.put("action", "ClearSmartButtonRSSTrack");
        params.put("accountId", Preferences.getString(Config.ACCOUNT_ID));
        params.put("building_id", buildingId);
        params.put("uniqueId", Preferences.getString(Config.CONTACT_ID));

        ApiHelper apiHelper = new ApiHelper();

        apiHelper.setOnSuccessListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonobject = new JSONObject(response);

                    LogUtils.debug("SendBroadcastForm","clearRSSFeed() --> response: "+response);

                    boolean success = jsonobject.getBoolean("success");
                    if(success) {
                        Toast.makeText(that, that.getResources().getString(R.string.broadcast_clear_successmess1), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(that, that.getResources().getString(R.string.broadcast_clear_errormess1), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        apiHelper.setOnErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(that, that.getResources().getString(R.string.broadcast_clear_errormess1), Toast.LENGTH_SHORT).show();
            }
        });

        apiHelper.prepareRequest(params, true);
        ApiHelper.getInstance(getActivity()).startRequest(apiHelper);
    }

    private String returnStringFromID(int id) {
        return this.getResources().getString(id);
    }

    private void errorLoadingReportSettings(int loadingCondition) {
        errorWithActivityProgress = true;
        errorLoadingCondition.add(loadingCondition);
    }

    private void restartActivityProgress() {
        errorLoadingCondition = new Vector<>();
        errorWithActivityProgress = false;

        //resetContent();
        resetActivityContent();
        loadSpinnerItems();
    }

    private void showActivityProgressError() {
        String[] loadedLists = new String[]{"Incident","Building","User Group"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error");

        StringBuilder alertMessage = new StringBuilder("Failed to load your ");

        for(int i=0; i<errorLoadingCondition.size(); i++) {
            alertMessage.append(loadedLists[errorLoadingCondition.get(i)]);
            if((i+1 < (errorLoadingCondition.size()-1))) {
                alertMessage.append(", ");
            } else if(i+1 == errorLoadingCondition.size()-1){
                alertMessage.append(" and ");
            }
        }

        alertMessage.append(" List at this time.");

        builder.setMessage(alertMessage.toString());

        builder.setNegativeButton("Go Back to Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().onBackPressed();
            }
        });

        builder.setPositiveButton("Reload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restartActivityProgress();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private final Runnable spinnerItemsTicker = new Runnable() {
        @Override
        public void run() {
            if(incidentsCompleted && buildingsCompleted && userGroupsCompleted) {
                LogUtils.debug("AsyncHandler","removing spinnerItemsTicker.postDelayed()");
                spinnerItemsHandler.removeCallbacks(this);
                activityProgressIndicator.dismiss();

                if(errorWithActivityProgress) {
                    //to test error condition - leads to crash without setting the value of this variable first
                    showActivityProgressError();
                }
            } else {
                LogUtils.debug("AsyncHandler","running spinnerItemsTicker.postDelayed()");
                spinnerItemsHandler.postDelayed(this,  1000);
            }
        }
    };
}
