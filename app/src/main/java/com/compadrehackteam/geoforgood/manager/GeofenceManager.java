package com.compadrehackteam.geoforgood.manager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.compadrehackteam.geoforgood.R;
import com.compadrehackteam.geoforgood.listener.GeofenceListener;
import com.compadrehackteam.geoforgood.model.GeofenceObject;
import com.compadrehackteam.geoforgood.model.GeofencePersist;
import com.compadrehackteam.geoforgood.rest.ApiService;
import com.compadrehackteam.geoforgood.service.GeofenceTransitionsIntentService;
import com.compadrehackteam.geoforgood.util.CustomApp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ricardo on 16/11/15.
 */
public class GeofenceManager implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    /**
     * Tag for the logger.
     */
    private static final String TAG = "GeofenceManager";

    /**
     * Application context.
     */
    private static Context sContext;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected List<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private static boolean sCustomListenerActive;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * The geofencelistener
     */
    private static GeofenceListener sListener;

    /**
     * Singleton class of geofenceManager
     */
    private static GeofenceManager sGeofenceManager;

    /**
     * Flag for init
     */
    private static boolean sInitialized = false;

    /**
     * Interface for registration result
     */
    public interface DownloadListener {

        // On Success register
        void onDownloadSuccess(int size);

        // On Error register
        void onDownloadError(String error);
    }

    /**
     * Default constructor, Exists only to defeat instantiation.
     */
    protected GeofenceManager() {
    }

    /**
     * This method checks if the Initialization is success
     *
     * @throws RuntimeException
     */
    private static void checkInitialization() throws RuntimeException {
        if (!sInitialized) {
            throw new RuntimeException("You should initialize the Manager");
        }
    }

    /**
     * This method starts and do the singleton init
     *
     * @param context The Context
     */
    public static void init(final Context context) {
        // Check of init
        if (sInitialized) {
            return;
        }
        // We create the object instance with the context
        sGeofenceManager = new GeofenceManager(context);
        // The flag for init
        sInitialized = true;
    }

    /**
     * This method starts and do the singleton with a user custom listener
     *
     * @param context   The Context
     * @param sListener The Listener
     */
    public static void initWithListener(final Context context, final GeofenceListener sListener) {
        // Check of init
        if (sInitialized) {
            return;
        }
        // We create the object instance with the context and the listener
        sGeofenceManager = new GeofenceManager(context, sListener);
        // The flag for init
        sInitialized = true;
    }

    /**
     * Parametrize constructor with the context
     *
     * @param context The context
     */
    private GeofenceManager(Context context) {

        // Activity context
        sContext = context.getApplicationContext();

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        sListener = new GeofenceListener() {
            @Override
            public void onGeofenceFound(int geofenceTransition, GeofenceObject n) {
                triggerAction(geofenceTransition, n);
                Toast.makeText(CustomApp.getContext(),n.getName(),Toast.LENGTH_LONG).show();

            }
        };
    }

    /**
     * Parametrized constructor with the context
     *
     * @param context The context
     */
    private GeofenceManager(Context context, GeofenceListener geofenceFoundListener) {

        // The geofence found listener
        sListener = geofenceFoundListener;

        // The flag for listener
        sCustomListenerActive = true;

        // Activity context
        sContext = context.getApplicationContext();

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    /**
     * Returns the Listener
     *
     * @return The Listener
     */
    public static GeofenceListener getListener() {
        checkInitialization();
        return sListener;
    }

    /**
     * Returns the tag manager for the geofence manager
     *
     * @return The GeofenceManager instance.
     */
    public static GeofenceManager getGeofenceManager() {
        checkInitialization();
        return sGeofenceManager;
    }

    /**
     * This method start the Service
     */
    public void startService() {

        // Connect the API Client
        mGoogleApiClient.connect();
    }

    /**
     * This method stop the Service
     */
    public void stopService() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * This method remove Geofence from the service
     */
    public void disableGeofencesInService() {

        removeGeofencesFromService();
    }

    /**
     * This method load the Geofences object from the local database
     *
     */
    public void downloadGeofencesFromDatabase() {

        addGeoFences(getAllGeofencesFromDatabase());
    }

    /**
     * This method loads from the local database the Geofence to the service
     */
    public void enableGeofencesInService() {
        // The list of local database stored Geofence
        List<GeofenceObject> geofenceList = getAllGeofencesFromDatabase();

        if (geofenceList != null) {
            // We get the Geofences from localdatabase;
            addGeoFences(geofenceList);
            // We call the method to add the geofences to the service
            addGeofencesToService();
        }
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    private void addGeofencesToService() {

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(sContext, sContext.getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }


    /**
     * This method build a Geofence and add it to the list
     *
     * @param geofenceList The GeoFence List object
     */
    private void addGeoFences(List<GeofenceObject> geofenceList) {

        if(geofenceList!=null) {
            for (GeofenceObject geofence : geofenceList) {

                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(geofence.getId())
                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                geofence.getLatitude(),
                                geofence.getLongitude(),
                                geofence.getRadius()
                        )
                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time. Now is NEVER_EXPIRE
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        // Set the transition types of interest. Alerts are only generated for these
                        // transition.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        // Create the geofence.
                        .build());

                Log.i(TAG, "GeoFence " + geofence.getId() + " created");

            }
        }
    }
    /**
     * This method load the geofences object from the server database
     */
    public static void downloadGeofencesFromServer(final DownloadListener downloadListener) {

        ApiService.getClient().getGeofences(new Callback<List<GeofenceObject>>() {
            @Override
            public void success(List<GeofenceObject> geofenceObjects, Response response) {
                saveOrUpdateGeofences(geofenceObjects);
                downloadListener.onDownloadSuccess(geofenceObjects.size());
            }

            @Override
            public void failure(RetrofitError error) {
                downloadListener.onDownloadError(error.getLocalizedMessage());
            }
        });
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(sContext)
                .addConnectionCallbacks(new ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Toast.makeText(sContext, "Conectado", Toast.LENGTH_SHORT).show();
                        //GeoFence to service
                        downloadGeofencesFromDatabase();
                        enableGeofencesInService();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p/>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(sContext, "On Result = " + status.toString(), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Log.e(TAG, "Error : " + status.getStatusMessage());
        }
    }


    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    private void removeGeofencesFromService() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(sContext, sContext.getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {

        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(sContext, GeofenceTransitionsIntentService.class);
        // Check if the user passed a custom listener or not
        return PendingIntent.getService(sContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This method build a Geofence and add it to the list
     *
     */
    public void createGeofenceObjects() {

        for (GeofenceObject geofenceObject : getAllGeofencesFromDatabase()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(geofenceObject.getId())
                    // Set the circular region of this geofence.
                    .setCircularRegion(geofenceObject.getLatitude(), geofenceObject.getLongitude(), geofenceObject.getRadius())
                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time. Now is NEVER_EXPIRE
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    // Set the transition types of interest. Alerts are only generated for these
                    // transition.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    // Create the geofence.
                    .build());

            Log.i(TAG, "GeoFence " + geofenceObject.getId() + " created");
        }

    }


    /**
     * Posts a notification in the notification bar when a transition is detected.
     */
    private void sendCustomNotification(String name, String message) {

        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(sContext);

        mBuilder.setContentTitle(name);
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(R.drawable.ic_cloud_download_black_24dp);

        // Adds the Intent that starts the Activity to the top of the stack
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                sContext,
                0,
                new Intent(), // add this
                PendingIntent.FLAG_UPDATE_CURRENT);

        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager myNotificationManager = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        myNotificationManager.notify(1, mBuilder.build());
    }

    /**
     * This method trigger the desired action based on the geofencetransition with the Geofence given
     *
     * @param geofenceTransition The transition
     * @param n                  The Geofence
     */
    private void triggerAction(int geofenceTransition, GeofenceObject n) {

        String message = "hola ke ase";
        Toast.makeText(CustomApp.getContext(),n.getName(),Toast.LENGTH_LONG).show();

        //sendCustomNotification(n.getId(), message);
    }

    /**
     * This method saves a Geofence in the database
     *
     * @param geofenceDB The persistent object
     */
    public static void saveOrUpdateGeofence(GeofenceObject geofenceDB) {

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(geofenceDtoToPersist(geofenceDB));
        realm.commitTransaction();
    }

    /**
     * Get all local database objects geofences
     *
     * @return The Geofence list
     */
    public static List<GeofenceObject> getAllGeofencesFromDatabase() {

        List<GeofenceObject> geofenceObjectList = new ArrayList<>();
        List<GeofencePersist> geofenceDBList = Realm.getDefaultInstance().where(GeofencePersist.class).findAll();

        for (GeofencePersist g : geofenceDBList) {
            geofenceObjectList.add(geofencePersistToDTO(g));
        }
        return geofenceObjectList;
    }

    /**
     * This method saves a list of GeofenceObjects
     *
     * @param geofenceObjectList
     */
    public static void saveOrUpdateGeofences(List<GeofenceObject> geofenceObjectList) {

        if (geofenceObjectList != null) {
            for (GeofenceObject n : geofenceObjectList) {
                saveOrUpdateGeofence(n);
            }
        }
    }

    /**
     * This method returns the geofence object by his id
     *
     * @param id The Geofence ID
     * @return The object
     */
    public static GeofenceObject getGeofenceById(String id) {

        return geofencePersistToDTO(Realm.getDefaultInstance().where(GeofencePersist.class).equalTo("id", id).findFirst());
    }

    public static GeofencePersist geofenceDtoToPersist(GeofenceObject geofenceObject) {

        return new GeofencePersist(geofenceObject.getId(), geofenceObject.getName(), geofenceObject.getLatitude(), geofenceObject.getLongitude(), geofenceObject.getRadius());
    }

    /**
     * Convert a database object to dto
     *
     * @param geofenceObject
     * @return
     */
    public static GeofenceObject geofencePersistToDTO(GeofencePersist geofenceObject) {

        return new GeofenceObject(geofenceObject.getId(), geofenceObject.getName(), geofenceObject.getLatitude(), geofenceObject.getLongitude(), geofenceObject.getRadius());
    }


    /**
     * This method handle the securityException of the manager
     *
     * @param securityException The SecurityException
     */
    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient :D");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");
        // onConnected() will be called again automatically when the service reconnects
    }

}
