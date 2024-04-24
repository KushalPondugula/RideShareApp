package edu.uga.cs.rideshare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DriverFragment extends Fragment {
    private User currentUser;
    private List<User> userList;
    private List<Ride> rideList;
    private DatabaseReference mDatabase;
    private String dateTimeString;

    public DriverFragment(User currentUser, List<User> userList, List<Ride> rideList) {
        // Required empty public constructor
        this.currentUser = currentUser;
        this.userList = userList;
        this.rideList = rideList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find the start button
        Button homeButton = view.findViewById(R.id.home_button);
        Button dateButton = view.findViewById(R.id.date_time_button);
        Button postRequestButton = view.findViewById(R.id.post_button);

        // Set OnClickListener to go to DriverFragment when driverButton is clicked
        homeButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the DriverFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeScreenFragment(currentUser, userList, rideList))
                    .commit();
        });

        // Date and Time Selection
        dateButton.setOnClickListener((View.OnClickListener) v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year1, monthOfYear, dayOfMonth) -> {
                // Get selected date
                Calendar timeCalendar = Calendar.getInstance();
                int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = timeCalendar.get(Calendar.MINUTE);

                // Create time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view2, hourOfDay, minute1) -> {
                    // Handle selected date and time
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(year1, monthOfYear, dayOfMonth, hourOfDay, minute1);

                    // Format the selected date and time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
                    dateTimeString = dateFormat.format(selectedDateTime.getTime());

                    // Display the selected date and time
                    Toast.makeText(getContext(), "Selected Date and Time: " + dateTimeString, Toast.LENGTH_LONG).show();
                }, hour, minute, false);

                // Show time picker dialog
                timePickerDialog.show();
            }, year, month, day);

            // Show date picker dialog
            datePickerDialog.show();
        });

        EditText startLocationInput = view.findViewById(R.id.from_text);
        EditText destinationInput = view.findViewById(R.id.to_text);

        // Post ride request
        postRequestButton.setOnClickListener((View.OnClickListener) v -> {
            String startLocation = startLocationInput.getText().toString();
            String destination = destinationInput.getText().toString(); // Set start location
            postRideRequest(dateTimeString, startLocation, destination);
        });

        return view;
    }

    private void postRideRequest(String date, String startLocation, String destination) {
        String key = mDatabase.child("rides").push().getKey();
        Ride ride = new Ride(dateTimeString, destination, startLocation, currentUser, null);
        mDatabase.child("rides").child(key).setValue(ride);
    }
}
