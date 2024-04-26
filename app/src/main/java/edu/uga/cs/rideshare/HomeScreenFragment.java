package edu.uga.cs.rideshare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeScreenFragment extends Fragment {

    private DatabaseReference mDatabase;
    private User currentUser;
    private List<User> userList;
    private List<Ride> rideList;
    private View view;

    public HomeScreenFragment(User currentUser, List<User> userList, List<Ride> rideList) {
        this.currentUser = currentUser;
        this.userList = userList;
        this.rideList = rideList;
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        // Refresh the ride list and update the UI
//        retrieveRides(new RideListUpdateListener() {
//            @Override
//            public void onRideListUpdated(List<Ride> updatedRideList) {
//                rideList = updatedRideList;
//                populateViews(view); // Call populateViews with rootView
//            }
//        });
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_screen_home, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        retrieveRides(new RideListUpdateListener() {
            @Override
            public void onRideListUpdated(List<Ride> updatedRideList) {
                rideList = updatedRideList;
                populateViews(view);
            }
        });
        TextView points = view.findViewById(R.id.points);
        points.setText(String.valueOf(currentUser.points));

        // Find the buttons
        Button logoutButton = view.findViewById(R.id.logoutHomeScreen);
        Button driverButton = view.findViewById(R.id.give_ride_button);
        Button riderButton = view.findViewById(R.id.get_ride_button);

        // Set OnClickListener for driverButton
        driverButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DriverFragment(currentUser, userList, rideList))
                    .commit();
        });

        // Set OnClickListener for riderButton
        riderButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RiderFragment(currentUser, userList, rideList))
                    .commit();
        });

        // Set OnClickListener for logoutButton
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SplashFragment())
                    .commit();
        });

        return view;
    }

    private void retrieveRides(RideListUpdateListener listener) {
        mDatabase.child("rides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Ride> updatedRideList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    updatedRideList.add(ride);
                }
                listener.onRideListUpdated(updatedRideList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void populateViews(View view) {
        List<Ride> aList = new ArrayList<>();
        List<Ride> rList = new ArrayList<>();
        List<Ride> oList = new ArrayList<>();
        // Populate views based on the updated ride list
        aList = filterAcceptedRides();
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
                String key = ride.key;
                if (currentUser.equals(ride.driver)) {
                    // Current user is the driver, add 50 points
                    currentUser.points += 50;
                    ride.rideCompletedDriver = true;
                } else if (currentUser.equals(ride.rider)) {
                    // Current user is the rider, subtract 50 points
                    currentUser.points -= 50;
                    ride.rideCompletedRider = true;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userId = firebaseUser.getUid();
                mDatabase.child("users").child(userId).setValue(currentUser);
                mDatabase.child("rides").child(key).setValue(ride);
                // Remove the button after it's clicked
                aLayout.removeView(button);
                aLayout.removeView(textView);
                TextView points = view.findViewById(R.id.points);
                points.setText(String.valueOf(currentUser.points));

            });

            // Add the TextView and Button to the layout
            aLayout.addView(textView);
            aLayout.addView(button);
        }

        rList = filterRequestedRides();
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

                // Create a dialog for updating ride information
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Update Ride Information");

                // Inflate the layout for the dialog
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_ride, null);
                builder.setView(dialogView);

                // Initialize views in the dialog layout
                EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
                EditText destinationEditText = dialogView.findViewById(R.id.destinationEditText);
                EditText startLocationEditText = dialogView.findViewById(R.id.startLocationEditText);
                Button dateButton = dialogView.findViewById(R.id.dateButton);

                // Set the current ride information in the EditText fields
                dateEditText.setText(ride.date);
                destinationEditText.setText(ride.goingTo);
                startLocationEditText.setText(ride.from);

                // Date and Time Selection
                dateButton.setOnClickListener(v1 -> {
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
                            String dateTimeString = dateFormat.format(selectedDateTime.getTime());

                            // Display the selected date and time
                            Toast.makeText(getContext(), "Selected Date and Time: " + dateTimeString, Toast.LENGTH_LONG).show();

                            // Set the selected date/time in the EditText
                            dateEditText.setText(dateTimeString);
                        }, hour, minute, false);

                        // Show time picker dialog
                        timePickerDialog.show();
                    }, year, month, day);

                    // Show date picker dialog
                    datePickerDialog.show();
                });

                // Set positive button for updating ride
                builder.setPositiveButton("Update", (dialog, which) -> {

                    rLayout.removeView(deleteButton);
                    rLayout.removeView(updateButton);
                    rLayout.removeView(textView);
                    // Get updated information from EditText fields
                    String updatedDate = dateEditText.getText().toString();
                    String updatedDestination = destinationEditText.getText().toString();
                    String updatedStartLocation = startLocationEditText.getText().toString();

                    // Update ride information
                    ride.date = updatedDate;
                    ride.goingTo = updatedDestination;
                    ride.from = updatedStartLocation;

                    // Update ride in the database
                    mDatabase.child("rides").child(ride.key).setValue(ride)
                            .addOnSuccessListener(aVoid -> {
                                // Ride updated successfully
                                populateViews(view);
                                Toast.makeText(getContext(), "Ride updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update ride
                                Toast.makeText(getContext(), "Failed to update ride: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });

                // Set negative button for canceling the update
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            });




            deleteButton.setOnClickListener(v -> {
                //rList.remove(ride);
                String key = ride.key;
                mDatabase.child("rides").child(key).setValue(null);
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

        oList = filterOfferedRides();
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
                // Create a dialog for updating ride information
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Update Ride Information");

                // Inflate the layout for the dialog
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_ride, null);
                builder.setView(dialogView);

                // Initialize views in the dialog layout
                EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
                EditText destinationEditText = dialogView.findViewById(R.id.destinationEditText);
                EditText startLocationEditText = dialogView.findViewById(R.id.startLocationEditText);
                Button dateButton = dialogView.findViewById(R.id.dateButton);

                // Set the current ride information in the EditText fields
                dateEditText.setText(ride.date);
                destinationEditText.setText(ride.goingTo);
                startLocationEditText.setText(ride.from);

                // Date and Time Selection
                dateButton.setOnClickListener(v1 -> {
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
                            String dateTimeString = dateFormat.format(selectedDateTime.getTime());

                            // Display the selected date and time
                            Toast.makeText(getContext(), "Selected Date and Time: " + dateTimeString, Toast.LENGTH_LONG).show();

                            // Set the selected date/time in the EditText
                            dateEditText.setText(dateTimeString);
                        }, hour, minute, false);

                        // Show time picker dialog
                        timePickerDialog.show();
                    }, year, month, day);

                    // Show date picker dialog
                    datePickerDialog.show();
                });

                // Set positive button for updating ride
                builder.setPositiveButton("Update", (dialog, which) -> {
                    oLayout.removeView(deleteButton);
                    oLayout.removeView(updateButton);
                    oLayout.removeView(textView);
                    // Get updated information from EditText fields
                    String updatedDate = dateEditText.getText().toString();
                    String updatedDestination = destinationEditText.getText().toString();
                    String updatedStartLocation = startLocationEditText.getText().toString();

                    // Update ride information
                    ride.date = updatedDate;
                    ride.goingTo = updatedDestination;
                    ride.from = updatedStartLocation;

                    // Update ride in the database
                    mDatabase.child("rides").child(ride.key).setValue(ride)
                            .addOnSuccessListener(aVoid -> {
                                // Ride updated successfully
                                Toast.makeText(getContext(), "Ride updated successfully", Toast.LENGTH_SHORT).show();
                                populateViews(view);
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update ride
                                Toast.makeText(getContext(), "Failed to update ride: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                });

                // Set negative button for canceling the update
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            });


            deleteButton.setOnClickListener(v -> {
                //oList.remove(ride);
                String key = ride.key;
                mDatabase.child("rides").child(key).setValue(null);
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
    }

    private List<Ride> filterAcceptedRides() {
        List<Ride> filteredList = new ArrayList<>();
        try {
            for (Ride ride : rideList) {
                if ((ride.driver != null && ride.driver.equals(currentUser)) || (ride.rider != null && ride.rider.equals(currentUser))) {
                    if (ride.driverAccepted && ride.riderAccepted) {
                        if (!ride.rideCompletedDriver && ride.driver.equals(currentUser)) {
                            filteredList.add(ride);
                        } else if (!ride.rideCompletedRider && ride.rider.equals(currentUser)) {
                            filteredList.add(ride);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            // Handle the exception here, such as logging an error message or displaying a toast
            Log.e("NullPointerException", "One of the objects is null: " + e.getMessage());
        }
        return filteredList;
    }

    private List<Ride> filterRequestedRides() {
        List<Ride> filteredList = new ArrayList<>();
        try {
            for (Ride ride : rideList) {
                //Log.d("!(ride.rideCompletedDriver && ride.rideCompletedRider)", String.valueOf(!(ride.rideCompletedDriver && ride.rideCompletedRider)));
                if (!(ride.rideCompletedDriver && ride.rideCompletedRider)) {
                    if (ride.rider.equals(currentUser) && !(ride.driverAccepted)) {
                        filteredList.add(ride);
                    }
                }
            }
        } catch (NullPointerException e) {
            // Handle the exception here, such as logging an error message or displaying a toast
            Log.e("NullPointerException", "One of the objects is null: " + e.getMessage());
        }
        //Log.d("fileterList", String.valueOf(filteredList));
        return filteredList;
    }

    private List<Ride> filterOfferedRides() {
        List<Ride> filteredList = new ArrayList<>();
        try {
            for (Ride ride : rideList) {
                if (!(ride.rideCompletedDriver && ride.rideCompletedRider)) {
                    if (ride.driver.equals(currentUser) && !(ride.riderAccepted)) {
                        filteredList.add(ride);
                    }
                }
            }
        } catch (NullPointerException e) {
            // Handle the exception here, such as logging an error message or displaying a toast
            Log.e("NullPointerException", "One of the objects is null: " + e.getMessage());
        }
        return filteredList;
    }

    // Define callback interface
    interface RideListUpdateListener {
        void onRideListUpdated(List<Ride> updatedRideList);
    }
}
