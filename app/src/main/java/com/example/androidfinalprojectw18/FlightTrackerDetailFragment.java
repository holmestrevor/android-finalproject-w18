package com.example.androidfinalprojectw18;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FlightTrackerDetailFragment extends Fragment {
    /**
     * whether is tabley
     */
    private boolean isTablet;
    /**
     * bundle passed to fragment
     */
    private Bundle dataFromActivity;
    /**
     * id from listview
     */
    private long id;

    /**
     * Used to set isTablet
     * @param tablet
     */
    public void setTablet(boolean tablet) { isTablet = tablet; }


    /**
     * Starting point for fragment loading
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(FlightStatusTracker.ITEM_ID );

        /**
         * inflate layout for this fragment
         */
        View result =  inflater.inflate(R.layout.flight_tracker_fragment_detail, container, false);

        /**
         * Get latitude textview and set it
         */
        TextView latitude = (TextView)result.findViewById(R.id.latitude);
        latitude.setText(Double.toString(dataFromActivity.getDouble(FlightStatusTracker.FLIGHT_LOCATION_LATITUDE)));

        /**
         * Get longitude textview and set it
         */
        TextView longitude = (TextView)result.findViewById(R.id.longitude);
        longitude.setText(Double.toString(dataFromActivity.getDouble(FlightStatusTracker.FLIGHT_LOCATION_LONGITUDE)));

        /**
         * Get altitude textview and set it
         */
        TextView altitude = (TextView)result.findViewById(R.id.altitude);
        altitude.setText(Double.toString(dataFromActivity.getDouble(FlightStatusTracker.FLIGHT_ALTITUDE)));

        /**
         * Get status textview and set it
         */
        TextView status = (TextView)result.findViewById(R.id.status);
        status.setText(dataFromActivity.getString(FlightStatusTracker.FLIGHT_STATUS));

//        //show the id:
//        TextView idView = (TextView)result.findViewById(R.id.idText);
//        idView.setText("ID=" + id);

        /**
         * Get delete button
         */
        Button deleteButton = (Button)result.findViewById(R.id.Flight_delete_button);
        /**
         * set click listener for delete button
         */
        deleteButton.setOnClickListener( clk -> {

            /**
             * boiler plate code if tablet
             */
            if(isTablet) { //both the list and details are on the screen:
                FlightStatusTracker parent = (FlightStatusTracker) getActivity();
                parent.deleteMessageId((int)id); //this deletes the item and updates the list


                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            /**
             * boiler plate code for phone
             */
            else //You are only looking at the details, you need to go back to the previous list page
            {
                FlightTrackerEmptyActivity parent = (FlightTrackerEmptyActivity) getActivity();
                Intent backToFragmentExample = new Intent();
                backToFragmentExample.putExtra(FlightStatusTracker.ITEM_ID, dataFromActivity.getLong(FlightStatusTracker.ITEM_ID ));

                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
        });
        return result;
    }
}
