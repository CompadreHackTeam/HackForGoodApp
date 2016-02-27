package com.compadrehackteam.geoforgood.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.compadrehackteam.geoforgood.R;
import com.compadrehackteam.geoforgood.manager.GeofenceManager;
import com.compadrehackteam.geoforgood.manager.SharedPreferencesManager;
import com.compadrehackteam.geoforgood.manager.UserManager;
import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.compadrehackteam.geoforgood.util.CustomApp;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @Bind(R.id.btn_login)
    Button mLoginButton;
    @Bind(R.id.input_name)
    EditText mEditTextUsername;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,v);
        initUI();



        // Inflate the layout for this fragment
        return v;
    }

    private void initUI(){
        mEditTextUsername.setText(UserManager.getUserName());
    }


    @OnClick(R.id.btn_login)
    public void updateUser(){

        SharedPreferencesManager.setUsername(CustomApp.getContext(),mEditTextUsername.getText().toString());
        mEditTextUsername.setText(SharedPreferencesManager.getUserName(CustomApp.getContext()));
        Snackbar.make(getActivity().getCurrentFocus(),"Bienvenido "+mEditTextUsername.getText().toString()+"!",Snackbar.LENGTH_LONG).show();

    }


}
