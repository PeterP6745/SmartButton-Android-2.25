package messagelogix.com.smartbuttoncommunications.activities.help;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;
import messagelogix.com.smartbuttoncommunications.model.HelpModel;

/**
 * Created by Vahid
 * This is the adaptor for the expandable section of the help
 */
public class HelpExpandableAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;

    private List<List<HelpModel.HelpItem>> mParent;

    private Context mContext;

    public HelpExpandableAdapter(Context context, List<List<HelpModel.HelpItem>> parent) {

        mParent = parent;
        inflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    //counts the number of group/parent items so the list knows how many times calls getGroupView() method
    public int getGroupCount() {

        return mParent.size();
    }

    @Override
    //counts the number of children items so the list knows how many times calls getChildView() method
    public int getChildrenCount(int i) {

        return mParent.get(i).size();
    }

    @Override
    //gets the title of each parent/group
    public Object getGroup(int i) {

        return mParent.get(i).get(0).getHeaderName();
    }

    @Override
    //gets the name of each item
    public Object getChild(int i, int i1) {

        return mParent.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {

        return i;
    }

    @Override
    public long getChildId(int i, int i1) {

        return i1;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    //in this method you must set the text to see the parent/group on the list
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {

        ViewHolder holder = new ViewHolder();
        holder.groupPosition = groupPosition;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_parent, viewGroup, false);
        }
        HelpModel.HelpItem helpItem = (HelpModel.HelpItem) getChild(groupPosition, 0);
        TextView textView = (TextView) view.findViewById(R.id.list_item_text_view);
        textView.setText(getGroup(groupPosition).toString());
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        int resID = mContext.getResources().getIdentifier(helpItem.getIcon(), "drawable", mContext.getPackageName());
        imageView.setImageResource(resID);
        imageView.setColorFilter(Color.argb(255, 255, 255, 255));
        view.setTag(holder);
        //return the entire view
        return view;
    }

    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        ViewHolder holder = new ViewHolder();
        holder.childPosition = childPosition;
        holder.groupPosition = groupPosition;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_child, viewGroup, false);
        }
        HelpModel.HelpItem helpItem = (HelpModel.HelpItem) getChild(groupPosition, childPosition);
        TextView textView = (TextView) view.findViewById(R.id.list_item_text_child);
        textView.setText(helpItem.getTitle());
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        int resID = mContext.getResources().getIdentifier(helpItem.getIcon(), "drawable", mContext.getPackageName());
        imageView.setImageResource(resID);
        view.setTag(holder);
        String backgroundColor = helpItem.getColor();
        if (backgroundColor != null && !backgroundColor.trim().isEmpty()) {
            view.setBackgroundColor(Color.parseColor(backgroundColor));
        } else {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {

        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        /* used to make the notifyDataSetChanged() method work */
        super.registerDataSetObserver(observer);
    }
// Intentionally put on comment, if you need on click deactivate it
/*  @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder)view.getTag();
        if (view.getId() == holder.button.getId()){

           // DO YOUR ACTION
        }
    }*/

    protected class ViewHolder {

        protected int childPosition;

        protected int groupPosition;

        protected Button button;
    }
}
