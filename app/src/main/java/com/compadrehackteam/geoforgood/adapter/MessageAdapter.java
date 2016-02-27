package com.compadrehackteam.geoforgood.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.compadrehackteam.geoforgood.R;
import com.compadrehackteam.geoforgood.model.MessageObject;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ricardo on 8/09/15.
 * <p/>
 * This adapter works with the Notifications Cards.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.NotificationViewHolder> {

    private List<MessageObject> messageObjectList;
    private SimpleDateFormat dateFormat;


    public MessageAdapter(List<MessageObject> messageObjectList) {
        this.messageObjectList = messageObjectList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    @Override
    public int getItemCount() {
        return messageObjectList.size();
    }


    public void onBindViewHolder(NotificationViewHolder notificationViewHolder, int i) {

        MessageObject message = messageObjectList.get(i);

        notificationViewHolder.vName.setText(message.getUsername());
        notificationViewHolder.vContent.setText(message.getContent());
        //  notificationViewHolder.vTime.setText(dateFormat.format(message.getDate()));

    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_message, viewGroup, false);

        return new NotificationViewHolder(itemView);
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView vName;
        TextView vContent;
        TextView vTime;

        public NotificationViewHolder(View v) {
            super(v);

            vName = (TextView) v.findViewById(R.id.notification_id);
            vContent = (TextView) v.findViewById(R.id.notification_content);
            vTime = (TextView) v.findViewById(R.id.notification_time);

        }
    }
}
