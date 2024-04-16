package edu.uga.cs.rideshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeScreenFragment extends Fragment {
    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen_home, container, false);


        // Find the start button
        Button logoutButton = view.findViewById(R.id.logoutHomeScreen);
        Button driverButton = view.findViewById(R.id.give_ride_button);
        Button riderButton = view.findViewById(R.id.get_ride_button);

        // Set OnClickListener to go to DriverFragment when driverButton is clicked
        driverButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the DriverFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DriverFragment())
                    .commit();
        });

        // Set OnClickListener to go to RiderFragment when riderButton is clicked
        riderButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the RiderFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RiderFragment())
                    .commit();
        });

        // Set OnClickListener to logout back to splash screen when clicked
        logoutButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the SplashFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SplashFragment())
                    .commit();
        });

        User currentUser = new User("current@gmail.com", "pass");
        List<Ride> aList = new ArrayList<>();
        aList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        aList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        aList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));


        LinearLayout aLayout = view.findViewById(R.id.a_rides_layout);

        for (int i = aList.size() - 1; i >= 0; i--) {
            Ride ride = aList.get(i);

            // Create a TextView to display ride information
            TextView textView = new TextView(getContext());
            textView.setTextAppearance(getContext(), R.style.MyTextViewStyle);
            textView.setText("\n\nRide Date: " + ride.date + "\n" + "To: " + ride.goingTo + ", From: " + ride.from);

            // Create a Button to mark the ride as completed
            Button button = new Button(getContext());
            button.setTextAppearance(getContext(), R.style.MyButtonStyle);
            button.setText("Ride Completed");

            // Set an OnClickListener for the button to update ride status when clicked
            button.setOnClickListener(v -> {
                // Update ride status to completed
                ride.rideCompletedDriver = true;
                // Remove the button after it's clicked
                aLayout.removeView(button);
                aLayout.removeView(textView);
            });

            // Add the TextView and Button to the layout
            aLayout.addView(textView);
            aLayout.addView(button);
        }


        List<Ride> rList = new ArrayList<>();
        rList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        rList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        rList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));


        LinearLayout rLayout = view.findViewById(R.id.r_rides_layout);
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


        List<Ride> oList = new ArrayList<>();
        oList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        oList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));
        oList.add(new Ride(new Date(), "Home", "not Home", currentUser, null));


        LinearLayout oLayout = view.findViewById(R.id.o_rides_layout);
        for (int i = oList.size() - 1; i >= 0; i--) {
            Ride ride = oList.get(i);

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
                oLayout.removeView(updateButton);
                oLayout.removeView(deleteButton);
                oLayout.removeView(textView);
            });



            deleteButton.setOnClickListener(v -> {
                oList.remove(ride);
                // Remove the button after it's clicked
                oLayout.removeView(deleteButton);
                oLayout.removeView(updateButton);
                oLayout.removeView(textView);
            });

            // Add the TextView and Button to the layout
            oLayout.addView(textView);
            oLayout.addView(updateButton);
            oLayout.addView(deleteButton);
        }



        return view;
    }

}

