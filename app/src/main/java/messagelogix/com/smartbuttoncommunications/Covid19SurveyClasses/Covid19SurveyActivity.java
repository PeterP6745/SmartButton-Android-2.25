package messagelogix.com.smartbuttoncommunications.Covid19SurveyClasses;

//import android.app.FragmentManager;
import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TabStopSpan;
import android.util.Log;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.utils.LogUtils;

public class Covid19SurveyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentById(R.id.clearScreen);
//       // String clearScreenVar = getIntent().getStringExtra("clearScreen");
//        if(fragment == null) {
//            fragment = new DefaultNotificationsList();
//
//            FragmentTransaction ft = fragmentManager.beginTransaction();
//            ft.add(R.id.clearScreen, fragment);
//            ft.commit();
//        }
        setContentView(R.layout.activity_covid19_survey);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String confirmationPageType = extras.getString("confirmationPageType");
            if("0".equals(confirmationPageType)) {
                StandardConfirmationView scView = new StandardConfirmationView();
                InitializeFragment(scView);
            } else {
                String clearScreenVar = extras.getString("clearScreenVar");
                Bundle bundle = new Bundle();
                bundle.putString("clearScreenVar", clearScreenVar);
                ClearScreen clearScreen = new ClearScreen();
                clearScreen.setArguments(bundle);
//                Log.d("clearScreenVarinsurvey",clearScreenVar);
                InitializeFragment(clearScreen);
            }
        }
    }

    private void InitializeFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Log.d("123", "init new frag");
    }

    @Override
    public void onBackPressed() {
        //LogUtils.debug("backPressed","SmartButton Activity - back button pressed, method overridden, so does nothing");
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            finish();
            LogUtils.debug("backPressed","NotifMenuActivity - notiflist fragment is visible, so closing fragment");
        } else {
            LogUtils.debug("backPressed", "NotifMenuActivity - back button pressed - method overriden, so does nothing");
        }
    }
}