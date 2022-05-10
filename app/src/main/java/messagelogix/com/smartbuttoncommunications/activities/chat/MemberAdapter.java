package messagelogix.com.smartbuttoncommunications.activities.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import messagelogix.com.smartbuttoncommunications.R;


public class MemberAdapter extends BaseAdapter {

    private final List<messagelogix.com.smartbuttoncommunications.model.ChatMembers> member;
    private Activity context;

    public MemberAdapter(Activity context, List<messagelogix.com.smartbuttoncommunications.model.ChatMembers> members) {
        this.context = context;
        this.member = members;
    }

    @Override
    public int getCount() {
        if (member != null) {
            return member.size();
        } else {
            return 0;
        }
    }

    @Override
    public messagelogix.com.smartbuttoncommunications.model.ChatMembers getItem(int position) {
        if (member != null) {
            return member.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        messagelogix.com.smartbuttoncommunications.model.ChatMembers members = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.members_list, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fName.setText(members.getFname());
        holder.email.setText(members.getEmail());

        return convertView;
    }

    public void add(messagelogix.com.smartbuttoncommunications.model.ChatMembers embers) {
        member.add(embers);
    }

    public void add(List<messagelogix.com.smartbuttoncommunications.model.ChatMembers> embers) {
        member.addAll(embers);
    }



    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.fName = (TextView) v.findViewById(R.id.members_fname);
        holder.email = (TextView) v.findViewById(R.id.members_email);
        return holder;
    }

    private static class ViewHolder {
        public TextView fName;
        public TextView email;

    }
}