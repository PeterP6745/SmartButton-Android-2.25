package messagelogix.com.smartbuttoncommunications.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import messagelogix.com.smartbuttoncommunications.R;

public class CustomProgressDialog extends Dialog {

    private Context context;
    private Dialog progressDialog;
    //private String progressMessage;


    public CustomProgressDialog(@NonNull Context activityContext) {
        super(activityContext);
        context = activityContext;
        progressDialog = new Dialog(context);
    }

//    public void setProgressMessage(String msg) {
//        progressMessage = msg;
//    }

    public void showDialog(String progressMessage){
        if(progressDialog != null) {
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.custom_progress_indicator);

            TextView text = progressDialog.findViewById(R.id.text_view);
            text.setText(progressMessage);
            progressDialog.show();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(progressDialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            progressDialog.getWindow().setAttributes(layoutParams);
        } else {
            progressDialog = new Dialog(context);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(R.layout.custom_progress_indicator);

            TextView text = progressDialog.findViewById(R.id.text_view);
            text.setText(progressMessage);
            progressDialog.show();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(progressDialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            progressDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public void setDialogMessage(String message) {
        TextView text = progressDialog.findViewById(R.id.text_view);
        text.setText(message);
    }

    public void dismiss() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public boolean isShowing() {
        return progressDialog.isShowing();
    }
}
