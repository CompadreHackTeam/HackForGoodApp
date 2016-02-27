package com.compadrehackteam.geoforgood.ui.fragment;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.compadrehackteam.geoforgood.R;
import com.compadrehackteam.geoforgood.adapter.MessageAdapter;
import com.compadrehackteam.geoforgood.manager.GeofenceManager;
import com.compadrehackteam.geoforgood.manager.MessageManager;
import com.compadrehackteam.geoforgood.manager.SharedPreferencesManager;
import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.compadrehackteam.geoforgood.model.MessageObject;
import com.compadrehackteam.geoforgood.rest.ApiService;
import com.compadrehackteam.geoforgood.ui.activity.NavigationActivity;
import com.compadrehackteam.geoforgood.util.CustomApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    /**
     * The RecyclerView which contain the cards.
     */
    private RecyclerView mRecyclerView;
    /**
     * The LinearLayoutManager which shows the cards.
     */
    private LinearLayoutManager mLinearLayoutManager;
    /**
     * The Runner Adapter
     */
    private MessageAdapter mMessageAdapter;
    /**
     * The message List
     */
    private List<MessageObject> mMessageList;
    /**
     * The geofence object
     */
    private GeofenceObject mGeofence;
    /**
     * The Geofence ID
     */
    private String geofenceID;

    private DialogFragment mDialog;

    public MessageFragment() {
    }

    @SuppressLint("ValidFragment")
    public MessageFragment(String id) {
        geofenceID = id;
    }


    @UiThread
    protected void dataSetChanged() {
        mMessageAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessageList = new ArrayList<>();

        mGeofence= GeofenceManager.getGeofenceById(geofenceID);

        // Setup the list of poi cards */
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.scrollToPosition(0);
        mLinearLayoutManager.setSmoothScrollbarEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, v);

        ((NavigationActivity) getActivity()).getSupportActionBar().setTitle(mGeofence.getName());

        mMessageAdapter = new MessageAdapter(mMessageList == null ? new ArrayList<MessageObject>() : mMessageList);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.messageRecyclerView);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mMessageAdapter);

        getMessages();


        return v;
    }

    public void getMessages(){

        ApiService.getClient().getMessagesOfGeofence(geofenceID, new Callback<List<MessageObject>>() {
            @Override
            public void success(List<MessageObject> messageObjects, Response response) {
                mMessageList.clear();
                mMessageList.addAll(messageObjects);
                dataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getLocalizedMessage());
            }
        });


    }
    public void sendMessage(String message) {

        MessageObject messageObject = new MessageObject(geofenceID, SharedPreferencesManager.getUserName(CustomApp.getContext()), message, new Date().toString());

        MessageManager.sendMessage(messageObject, new MessageManager.MessageSendListener() {
            @Override
            public void onSendSucess() {
                getMessages();
                Snackbar.make(getActivity().getCurrentFocus(),"Mensaje enviado",Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onSendError() {
                Snackbar.make(getActivity().getCurrentFocus(),"Error enviando mensaje",Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    @OnClick(R.id.fab_message)
    public void alertEditTextKeyboardShown() {

        // creating the EditText widget programatically
        final EditText input = new EditText(CustomApp.getContext());

        input.setTextColor(getResources().getColor(R.color.colorButton));

        // create the AlertDialog as final
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("Escribe un mensaje")
                .setTitle(SharedPreferencesManager.getUserName(CustomApp.getContext()))
                .setView(input)

                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if(!input.getText().toString().isEmpty()){
                            sendMessage(input.getText().toString());
                            dialog.dismiss();
                        }
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen

                        dialog.dismiss();
                    }
                })
                .create();
        // set the focus change listener of the EditText
        // this part will make the soft keyboard automaticall visible
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        dialog.show();
    }
}
