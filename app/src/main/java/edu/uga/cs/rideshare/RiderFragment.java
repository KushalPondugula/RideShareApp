package edu.uga.cs.rideshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RiderFragment extends Fragment {
    private User currentUser;
    private List<User> userList;
    private DatabaseReference mDatabase;
    private List<Ride> rideList;
    public RiderFragment(User currentUser, List<User> userList, List<Ride> rideList) {
        // Required empty public constructor
        this.currentUser = currentUser;
        this.userList = userList;
        this.rideList = rideList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Find the start button
        Button homeButton = view.findViewById(R.id.home_button);

        Button postRequestButton = view.findViewById(R.id.post_button);

        // Set OnClickListener to go to DriverFragment when driverButton is clicked
        homeButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the DriverFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeScreenFragment(currentUser, userList, rideList))
                    .commit();
        });


        List<Ride> rList = new ArrayList<>();
        rList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        rList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        rList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));


        LinearLayout rLayout = view.findViewById(R.id.a_rides_layout);
        for (int i = rList.size() - 1; i >= 0; i--) {
            Ride ride = rList.get(i);

            // Create a TextView to display ride information
            TextView textView = new TextView(getContext());
            textView.setTextAppearance(getContext(), R.style.MyTextViewStyle);
            textView.setText("\n\nRide Date: " + ride.date + "\n" + "To: " + ride.goingTo + ", From: " + ride.from);


            Button updateButton = new Button(getContext());
            updateButton.setTextAppearance(getContext(), R.style.MyButtonStyle);
            updateButton.setText("Update");

            Button deleteButton = new Button(getContext());
            deleteButton.setTextAppearance(getContext(), R.style.MyButtonStyle);
            deleteButton.setText("Delete");

            updateButton.setOnClickListener(v -> {

                // Remove the button after it's clicked
                rLayout.removeView(updateButton);
                rLayout.removeView(deleteButton);
                rLayout.removeView(textView);
            });



            deleteButton.setOnClickListener(v -> {
                rList.remove(ride);
                // Remove the button after it's clicked
                rLayout.removeView(deleteButton);
                rLayout.removeView(updateButton);
                rLayout.removeView(textView);
            });

            // Add the TextView and Button to the layout
            rLayout.addView(textView);
            rLayout.addView(updateButton);
            rLayout.addView(deleteButton);
        }

        EditText dateInput = view.findViewById(R.id.editTextDate);
        EditText startLocationInput = view.findViewById(R.id.from_text);
        EditText destinationInput = view.findViewById(R.id.to_text);

        postRequestButton.setOnClickListener((View.OnClickListener) v -> {
            String destination = destinationInput.getText().toString();
            String dateString = dateInput.getText().toString();
            Date date = null; // Declare date variable outside try block
            try {
                // Parse the string into a Date object
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                date = dateFormat.parse(dateString);
                // Now 'date' contains the parsed date
                // Use 'date' in your postRideRequest method or wherever you need it
            } catch (ParseException e) {
                e.printStackTrace();
                // Handle the parsing error
            }
            String startLocation = startLocationInput.getText().toString();
            postRideRequest(date, startLocation, destination);
        });

        return view;
    }

    private void postRideRequest(Date date, String startLocation, String destination) {
        String key = mDatabase.child("rides").push().getKey();
        User rider = new User(currentUser.email, "email@example.com"); // Example user
        Ride ride = new Ride(date, destination, startLocation, null, rider); // Example ride
        mDatabase.child("rides").child(key).setValue(ride);
    }

}


