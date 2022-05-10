package messagelogix.com.smartbuttoncommunications.adaptors;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.Contact;


public class DecListAdaptor extends ArrayAdapter<Contact> {

    public DecListAdaptor(Context context, List<Contact> users){
        super(context, 0, users);
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        Contact user = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_dec, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        //TextView tvDep = (TextView) convertView.findViewById(R.id.tvDep);
        ImageButton emailButton = (ImageButton) convertView.findViewById(R.id.ibtnEmail);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CODE GOES HERE
            }
        });

        ImageButton phoneButton = (ImageButton) convertView.findViewById(R.id.ibtnPhone);

        final String cellNumber = user.getPhoneNumber();

        //If there is no cell number (empty string), disable the phone button and gray it out
        //phoneButton.setEnabled(false);

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri number = Uri.parse("tel:" + cellNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                if (isCallingSupported(getContext(), callIntent)) {
                    //Send to dial pad
                    getContext().startActivity(callIntent);
                } else {
                    Snackbar.make(view, "Phone call is not supported in this device", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        // Populate the data into the template view using the data object
        tvName.setText(user.getName());
        tvTitle.setText(user.getTitle());
        //tvDep.setText(user.dep);

        // Return the completed view to render on screen
        return convertView;
    }
    private static boolean isCallingSupported(Context context, Intent intent) {

        boolean result = true;
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() <= 0) {
            result = false;
        }
        return result;
    }
}