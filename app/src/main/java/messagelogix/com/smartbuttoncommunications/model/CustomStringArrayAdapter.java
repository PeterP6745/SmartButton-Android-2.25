package messagelogix.com.smartbuttoncommunications.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;

public class CustomStringArrayAdapter  extends ArrayAdapter<String> {

        private Activity myActivity;

        public CustomStringArrayAdapter(Context context, int layoutResourceId, int textViewResourceId, List<
        String > objects, Activity act)
        {
            super(context, layoutResourceId, textViewResourceId, objects);
            this.myActivity = act;
        }

        @Override
        public View getDropDownView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView ( int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = myActivity.getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_layout, parent, false);
            TextView label = (TextView) row.findViewById(R.id.spinner_textview);
            label.setText(getItem(position));
            LinearLayout layout = (LinearLayout) row.findViewById(R.id.spinner_linear_layout);

            return row;
        }

        @Override
        public View getView ( int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = myActivity.getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_layout, parent, false);
            TextView label = (TextView) row.findViewById(R.id.spinner_textview);
            label.setText(getItem(position));

            return row;
        }

}

