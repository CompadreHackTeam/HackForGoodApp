package com.compadrehackteam.geoforgood.rest;


import android.util.Base64;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;


/**
 * Created by ricardo on 11/09/15.
 * <p/>
 * This ApiService class works with the configuration of auth and some
 * tricks to parse date in millis received from server to Java date object.
 * <p/>
 * ApiService.getClient can be called everywhere to call EndPointInterface defined methods.
 */
public class ApiService {

    /**
     * Base url for development service.
     */
    public static final String BASE_URL_DEVELOPMENT = "http://hackforgood.cloudapp.net:8080";

    /**
     * Username.
     */
    private static String username = "hackforgood";

    /**
     * Password.
     */
    private static String password = "hackforgood2016";

    /**
     * Endpoint interface
     */
    private static EndpointInterface sClient;

    // No need to instantiate this class.
    private ApiService() {}

    /**
     * This method returns a instantiate a EndpointInterface object to call API service
     *
     * @return EndpointInterface
     */
    public static EndpointInterface getClient() {
        if (sClient == null) {

            // set endpoint url and use OkHTTP as HTTP client,
            // we have to use too a custom converter between millis
            // and date from the server. Also we have auth in headerString
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL_DEVELOPMENT)
                    .setClient(new OkClient(new OkHttpClient()));


            if (username != null && password != null) {
                // concatenate username and password with colon for authentication
                final String credentials = username + ":" + password;
                builder.setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        // If we dont need auth, only comment the line below
                        request.addHeader("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                      //  request.addHeader("Content-Type","application/x-www-form-urlencoded");
                    }
                });
            }
            RestAdapter adapter = builder.build();
            sClient = adapter.create(EndpointInterface.class);
        }

        return sClient;
    }
}



