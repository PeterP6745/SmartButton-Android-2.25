package messagelogix.com.smartbuttoncommunications.Covid19SurveyClasses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

public class StandardConfirmationView extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_standardconfirmationview, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){

        TextView schoolTV = getView().findViewById(R.id.schoolLabel);
        TextView confirmationMessageTV = getView().findViewById(R.id.confirmationMessage);
        TextView dateAndTimeTV = getView().findViewById(R.id.dateAndTime);
        TextView nameTV = getView().findViewById(R.id.nameLabel);
        String schoolName = Preferences.getSPInstance(getActivity()).contains(Config.USER_BUILDING_NAME) ? Preferences.getString(Config.USER_BUILDING_NAME) : null;

        String userName = Preferences.getSPInstance(getActivity()).contains(Config.USER_FULL_NAME) ? Preferences.getString(Config.USER_FULL_NAME) : null;

        if(userName!=null){
            nameTV.setText(userName);
        }else{
            nameTV.setVisibility(View.GONE);
            View dividerOne = getView().findViewById(R.id.dividerOne);
            dividerOne.setVisibility(View.GONE);
        }

        if (schoolName!=null){
            schoolTV.setText(schoolName);
        }else{
            View dividerTwo = getView().findViewById(R.id.dividerTwo);
            schoolTV.setVisibility(View.GONE);
            dividerTwo.setVisibility(View.GONE);
        }

        String confirmationMessage = getString(R.string.Submission);
        confirmationMessageTV.setText(confirmationMessage);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyy h:mm",getResources().getConfiguration().locale);//Locale.ENGLISH);

        Date currDate = new Date();
        String currentDateandTime = sdf.format(currDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(currDate);
        if (cal.get(Calendar.AM_PM) == Calendar.PM) {
            currentDateandTime += " p.m.";
        } else {
            currentDateandTime += " a.m.";
        }

        dateAndTimeTV.setText(currentDateandTime);

        Button exitButton = getView().findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }
}
