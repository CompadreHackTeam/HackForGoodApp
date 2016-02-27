package com.compadrehackteam.geoforgood.manager;

import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.compadrehackteam.geoforgood.model.MessageObject;
import com.compadrehackteam.geoforgood.rest.ApiService;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ricardo on 27/02/16.
 */
public class MessageManager {

    /**
     * Interface for registration result
     */
    public interface MessageSendListener {

        // On Success register
        void onSendSucess();

        // On Error register
        void onSendError();
    }

    public static void sendMessage(MessageObject message, final MessageSendListener listener){

        ApiService.getClient().sendMessage(message, new Callback<MessageObject>() {
            @Override
            public void success(MessageObject messageObject, Response response) {
                listener.onSendSucess();
            }

            @Override
            public void failure(RetrofitError error) {
                listener.onSendError();

            }
        });

    }
}
