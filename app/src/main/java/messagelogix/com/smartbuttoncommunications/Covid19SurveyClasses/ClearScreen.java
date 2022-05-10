package messagelogix.com.smartbuttoncommunications.Covid19SurveyClasses;

//import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.Config;
import messagelogix.com.smartbuttoncommunications.utils.Preferences;

public class ClearScreen extends Fragment {
    private ImageView thumb;
    private TextView covidMessageView;
    //private HomeActivity mainActivity;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String clearScreenVar;
    Bundle bundle = new Bundle();
    public ClearScreen() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ClearScreen.
//     */
    // TODO: Rename and change types and number of parameters
//    public static ClearScreen newInstance(String param1, String param2) {
        //ClearScreen fragment = new ClearScreen();

//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clear_screen, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        //mainActivity = new HomeActivity();
        thumb = getView().findViewById(R.id.imageView3);
        covidMessageView = getView().findViewById(R.id.covidMessage);
        TextView dateAndTime = (TextView) getView().findViewById(R.id.dateAndTime);

        clearScreenVar = getArguments().getString("clearScreenVar");

        String userName = Preferences.getSPInstance(getActivity()).contains(Config.USER_FULL_NAME) ? Preferences.getString(Config.USER_FULL_NAME) : null;

        if ("false".equals(clearScreenVar)){
            String covidMessage = (userName != null) ? (userName + ", " + getString(R.string.Stay_Home)) : getString(R.string.Please_Stay_Home);
            thumb.setImageResource(R.drawable.dislike);
            covidMessageView.setText(covidMessage);
        } else  {
            String covidMessage = (userName != null) ? (userName + ", " + getString(R.string.Cleared)) : getString(R.string.Is_Cleared);
            covidMessageView.setText(covidMessage);
        }

        Log.d("weirdDate",DateFormat.getDateInstance() + "");
       DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);

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

        dateAndTime.setText(currentDateandTime);

        Button exitButton = (Button) getView().findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                getFragmentManager().popBackStack();
                getActivity().finish();
            }
        });
    }
}
