package messagelogix.com.smartbuttoncommunications.activities.conversations;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import messagelogix.com.smartbuttoncommunications.R;

/**
 * Created by Richard on 11/9/2018.
 */
public class ConversationsMenuAdapter extends ArrayAdapter {

    Context context;
    //ArrayList<MessageSummaryItem> messages;
    ArrayList<String> locations;
    ArrayList<String> activeAlertLocations;

    public ConversationsMenuAdapter(Context mContext,ArrayList<String> data, ArrayList<String> activeLocs){
        super(mContext, R.layout.list_item_signalr_locations, data);
        locations = data;
        activeAlertLocations = activeLocs;
        context = mContext;

        //Log.d("ADAPTER MESSAGES SIZE", "" + messages.size());
        //Log.d("ADAPTER MESSAGES DATA", "" + messages.get(0).getMessage());
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtLocation;
        ImageView iconIndicatorImageView;
    }

    @Override
    public int getCount() {
        if(locations.size()<1){
            return 0;
        }
        return locations.size();
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        // MessageSummaryItem messageSummaryItem = messages.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_signalr_locations, parent, false);
            viewHolder.txtLocation = (TextView) convertView.findViewById(R.id.sigr_location_txt);

            viewHolder.iconIndicatorImageView = (ImageView) convertView.findViewById(R.id.active_broadcast_icon_imageview);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        if(position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));//edebc9
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));//eff5fa
        }

        viewHolder.txtLocation.setText(locations.get(position));
        viewHolder.iconIndicatorImageView.setTag(position);
        //Log.e("Active Alerts List:", activeAlertLocations.toString());
        //Log.e(" --> Current Object:", locations.get(position));
        if(activeAlertLocations.contains(locations.get(position))){

            viewHolder.iconIndicatorImageView.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.iconIndicatorImageView.setVisibility(View.INVISIBLE);
        }

//        String newMessageCount = messages.get(position).getNewMessageCount();
//        boolean hasNewMessage = (!newMessageCount.equals("0"));
//        if(hasNewMessage) {
//            viewHolder.iconIndicatorImageView.setVisibility(View.VISIBLE);
//        }
//        else {
//            viewHolder.iconIndicatorImageView.setVisibility(View.INVISIBLE);
//        }
        // Return the completed view to render on screen
        return result;
    }


    @Override
    public long getItemId(int i) {

        return 0;
    }




}
