package messagelogix.com.smartbuttoncommunications.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Config {

    //API Constant
//  public static final String API_URL_OLD = "https://www.anonymousalerts.com/API/user/v1/index.php";

    public static final String API_URL_TEST = "https://www.anonymousalerts.com/API5/user/v1/index.php";

    public static final String API_URL = "https://k12connections.com/bluekitten/user/v1/";

    public static final String API_KEY = "TkuCD!91_8m6w9v4.O4DK5b60{]z8C3q%g9[zaWnqCe)2k5zhxS!r)p71jAXqTc";

    public static final String APP_ID = "873452dbe7fb1874f80eab6b488f718c";

    public static final String SUCCESS = "success";

    public static final String DATA = "data";

    //Location
    public static final String LONGITUDE = "longitude";

    public static final String LATITUDE = "latitude";

    public static final String ADDRESS = "address";

    //Notifications
    public static final String FIELD_NOTIF_BADGECOUNT = "total_unread_notices";

    //Conversations
    public static final String CONVERSATION_ID = "conversation_id";

    public static final String CONVERSATION_FNAME = "conversation_fname";

    public static final String FIELD_CHAT_BADGECOUNT = "total_unread_messages";

    public static final String CONVERSATION_RECEIVER_NAME = "receiver_name";

    //Device
    public static final String IS_LANGUAGE_SET = "isLanguageSet";

    public static final String IS_LOGGED_IN = "LoginSession";

    public static final String LAST_VERSION_CODE = "LAST_VERSION_CODE";

    public static final String UNIQUE_ID = "unique_id";

    public static final String LOGIN_TYPE = "login_type";

    public static final String REMIND_ME_LATER = "remind_me_later";

    public static final String TEXT_IS_ENABLED = "smartbutton_contacts_text";

    public static final String FORCE_UPDATE = "smartbutton_forceupdate";

    /**
     * TOGGLE
     */
    public static final String TWO_WAY_TOGGLE = "smartbutton_2waychat";

    public static final String NO_LOC_TOGGLE = "noLocation";

    public static final String INCIDENT_REQUIRED = "sb_message_required";

    public static final String HAS_TRANSLATIONS = "sb_translations";

    public static final String COVIDSCREENING_ACCOUNT = "survey_screening";

    public static final String QUESTIONNAIRE_ACCOUNT = "sb_multi_select";

    //Account
    public static final String ACCOUNT_ID = "acct_id";

    public static final String CONTACT_FIRST_NAME = "fname";
    //public static final String CONTACT_LAST_NAME = "lname";

    public static final String CONTACT_TITLE = "title";

    public static final String CONTACT_TITLE_NAME = "title_name";

    public static final String CONTACT_CARRIER = "carrier";

    public static final String CONTACT_CARRIER_ID = "carrier_id";

    public static final String CONTACT_USER_NAME = "username";

    public static final String CONTACT_PRIVILEGE = "privilege";

    public static final String CONTACT_SCHOOLS = "schools";

    public static final String CONTACT_SCHOOL_VALUE = "school_value";

    public static final String CONTACT_SCHOOLS_NAME = "school_name";

    public static final String CONTACT_POLICY = "policy";

    public static final String CONTACT_ACTIVE = "active";

    public static final String ADD_DEFAULT_CONTACTS = "smartbutton_adddefaultcontacts";

    public static final String LOCATOR_TIMEOUT = "locator_timeout";

    public static final String CONTACT_EMAIL = "contact_email";

    public static final String EMAIL = "email";

    public static final String CONTACT_ID = "contact_id";

    public static final String CONTACT_CELLPHONE = "cellphone";

    public static final String CONTACT_TYPE = "contact_type";

    public static final String CONTACT_POLICY_CONTENT = "policy_content";

    public static final String SCHOOLBUILDING_LIST = "schoolbuilding_list";
    public static final String SCHOOLBUILDING_LIST_LOADDATE = "schoolbuilding_list_loaddate";
    public static final String INCIDENTTYPE_LIST = "incidenttype_list";
    public static final String INCIDENTTYPE_LIST_LOADDATE = "incidenttype_list_loaddate";
    public static final String DEFAULTCONTACTS_LIST = "defaultcontacts_list";
    public static final String DEFAULTCONTACTS_LIST_LOADDATE = "defaultcontacts_list_loaddate";
    public static final String TITLE_LIST = "title_list";
    public static final String TITLE_LIST_LOADDATE = "title_list_loaddate";
    public static final String HELPRESOURCES_LIST = "helpresources_list";
    public static final String HELPRESOURCES_LIST_LOADDATE = "helpresources_list_loaddate";

    //school
    public static final String SCHOOL_ID = "sch_id";

    //Customization
    public static final String DISPLAY_NAME = "display_name";

    //Tab bar
    public static final String CURRENT_TAB = "current_tab";

    public static final String FLAG = "flag";

    //Help
    public static final String HELP_CACHE = "help_cache";

    //Messaging
    public static final String MESSAGE_RECEIVER_NAME = "message_receiver_name";

    public static final String MESSAGE_RECEIVER_ID = "message_receiver_id";

    //Confirmation
    public static final String CONFIRMATION_EMAIL = "confirmation_email";

    public static final String PASSWORD_RESET_FLAG = "password_reset_flag";

    //Locator
    public static final String USER_ROOM = "roomRSA";

    public static final String USER_FLOOR = "floorRSA";

    public static final String USER_DESCRIPTION = "descriptionRSA";

    public static final String USER_FULL_NAME = "nameRSA";

    public static final String USER_TITLE_NAME = "titleNameRSA";

    public static final String USER_BUILDING_NAME = "buildingNameRSA";

    public static final String USER_BUILDING_ID = "buildingId";

    public static final String LANGUAGE_SELECT = "Language";

    public static final String USER_TITLE_ID = "titleId";

    public static final String USER_PROFILE_PICTURE = "profile_picture";

    public static final String NEXT_TIME_ALLOWED = "nextAllowedTime";

    public static final String DEVICE_TOKEN = "device_token";

    public static final String DEFAULT_PASSWORD = "changeme";

    public static final String USERNAME = "usernameRSA";

    public static final String PASSWORD = "passwordRSA";

    public static final String REMEMBER_ME = "rememberMe";

    public static final String SHOULD_ALERT_RSS = "sb_desktop_rssfeed";

    public static final String HAS_DESKTOPS = "has_desktop";

    public static final String IS_SUPER_USER = "sb_alert_type";

    //This is the toggle for a Red Button press to update the RSS FEED for Default Emergency Contacts (DEC) ONLY
    public static final String DEC_ONLY = "dec_only";
    public static final String PUSH_TO_APP_ONLY = "has_mobile_only";
    /**
     * Please replace this with a valid API key which is enabled for the
     * YouTube Data API v3 service. Go to the
     * <a href="https://console.developers.google.com/">Google Developers Console</a>
     * to register a new developer key.
     */
    public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyAI034-nJVY-x0AXiFOXUD6k0yMZ4J6Ggw";

    public static int REMINDER_INTERVAL = 2;
    public static final String LANGUAGE="language";

    public static String CHAT_BADGE_COUNT = "0";
    public static String PUSH_BADGE_COUNT = "0";
    public static String MUTABLE_CHAT_ID = "0";
    public static String MUTABLE_RECEIVER_NAME = "null";

    public static String ROOM_TOGGLE = "sb_room_number";
    public static String ALT_LAUNCH = "sb_disable_sbButton";

    public static String GEOFENCE_TOGGLE = "sb_geofencing";
    public static String GEOFENCE_IN_ACTIVE_ZONE = "0";

    public static String SB_PRESS_LENGTH = "sb_button_press";

    public static String USER_INFO_WAS_UPDATED = "userInfoUpdated";

    public static void setChatBadgeCount(String s){
        CHAT_BADGE_COUNT = s;

    }

    public static void setPushBadgeCount(String s){
        PUSH_BADGE_COUNT = s;
    }

    public static void setChatId(String s){
        MUTABLE_CHAT_ID = s;
    }

    public static boolean calcDaysSinceListLoad(String loadDate, long numDaysElapsed) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        String currDate = sdf.format(Calendar.getInstance().getTime());

        LogUtils.debug("calcDays","loadDate is -> "+loadDate);
        LogUtils.debug("calcDays","currDate is -> "+currDate);

        Date prevLoadDate = sdf.parse(loadDate);
        Date dateToday = sdf.parse(currDate);

        long diffInMillies = Math.abs(dateToday.getTime() - prevLoadDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        LogUtils.debug("calcDays","diff is -> "+diff);

        if(diff >= 3)
            return true;
        else
            return false;
    }
}
