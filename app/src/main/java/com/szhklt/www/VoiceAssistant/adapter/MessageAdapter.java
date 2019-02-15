package com.szhklt.www.VoiceAssistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szhklt.www.VoiceAssistant.R;
import com.szhklt.www.VoiceAssistant.beam.Msg;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Msg>{
	private int resourceID;
	public MessageAdapter(Context context, int resource, List<Msg> objects) {
		super(context, resource, objects);
		resourceID = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Msg msg = getItem(position);
		View view;
		ViewHolder viewHolder;
		if(convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceID,null);
			viewHolder = new ViewHolder();
			viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
			viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
			viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
			viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
			view.setTag(viewHolder);
		}else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		if(msg.getType() == Msg.MSG_RECEIVE) {
			viewHolder.leftLayout.setVisibility(View.VISIBLE);
			viewHolder.rightLayout.setVisibility(View.GONE);
			viewHolder.leftMsg.setText(msg.getMessage());
		}else if(msg.getType() == Msg.MSG_SEND){
			viewHolder.rightLayout.setVisibility(View.VISIBLE);
			viewHolder.leftLayout.setVisibility(View.GONE);
			viewHolder.rightMsg.setText(msg.getMessage());
		}
		return view;
	}

	public class ViewHolder {
		LinearLayout leftLayout;
		LinearLayout rightLayout;
		TextView leftMsg;
		public TextView rightMsg;
	}
}