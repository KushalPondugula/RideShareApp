package edu.uga.cs.rideshare;

import android.app.Activity;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeScreenFragment extends Fragment {

    private DatabaseReference mDatabase;
    private User currentUser;
    private List<User> userList;

    private List<Ride> rideList;

    public HomeScreenFragment(User currentUser, List<User> userList, List<Ride> rideList) {
        // Required empty public constructor
        this.currentUser = currentUser;
        this.userList = userList;
        this.rideList = rideList;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen_home, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        retrieveRides();
        TextView points = view.findViewById(R.id.points);
        points.setText(String.valueOf(currentUser.points));

        // Find the start button
        Button logoutButton = view.findViewById(R.id.logoutHomeScreen);
        Button driverButton = view.findViewById(R.id.give_ride_button);
        Button riderButton = view.findViewById(R.id.get_ride_button);

        // Set OnClickListener to go to DriverFragment when driverButton is clicked
        driverButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the DriverFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DriverFragment(currentUser, userList, rideList))
                    .commit();
        });

        // Set OnClickListener to go to RiderFragment when riderButton is clicked
        riderButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the RiderFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RiderFragment(currentUser, userList, rideList))
                    .commit();
        });

        // Set OnClickListener to logout back to splash screen when clicked
        logoutButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the SplashFragment
            FirebaseAuth.getInstance().signOut();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SplashFragment())
                    .commit();
        });

        //Log.d("rideList, HS:", String.valueOf(rideList));
        List<Ride> aList = filterAcceptedRides(rideList);
//        List<Ride> aList = new ArrayList<>();
//        aList.add(new Ride("key","date", "Home", "not Home", currentUser, null));
//        aList.add(new Ride("key","date", "Home", "not Home", currentUser, null));
//        aList.add(new Ride("key","date", "Home", "not Home", currentUser, null));


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
                points.setText(String.valueOf(currentUser.points));
                aList.remove(ride);
            });

            // Add the TextView and Button to the layout
            aLayout.addView(textView);
            aLayout.addView(button);
        }


        List<Ride> rList = filterRequestedRides(rideList);
        Log.d("rList", String.valueOf(rList));

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
                rList.remove(ride);
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


        List<Ride> oList = filterOfferedRides(rideList);

//        oList.add(new Ride("date", "Home", "not Home", currentUser, null));
//        oList.add(new Ride("date", "Home", "not Home", currentUser, null));
//        oList.add(new Ride("date", "Home", "not Home", currentUser, null));


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
                oList.remove(ride);
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
        //retrieveRequestedRides(view);
        //retrieveOfferedRide(view);
        return view;
    }

    private void retrieveRequestedRides(View view) {

        // Query the database for requested rides by the current user
        Query requestedRidesQuery = mDatabase.child("rides").orderByChild("rider/email").equalTo(currentUser.email);
        requestedRidesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing layout
                LinearLayout requestedRidesLayout = view.findViewById(R.id.r_rides_layout);
                requestedRidesLayout.removeAllViews();

                // Iterate through the retrieved rides and display them
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    if (ride != null && getContext() != null) {
                        // Create a TextView to display ride information
                        TextView textView = new TextView(getContext());
                        textView.setTextAppearance(getContext(), R.style.MyTextViewStyle);
                        textView.setText("\n\nRide Date: " + ride.date + "\n" + "To: " + ride.goingTo + ", From: " + ride.from);
                        // Create update and delete buttons
                        Button updateButton = new Button(getContext());
                        updateButton.setText("Update");
                        Button deleteButton = new Button(getContext());
                        deleteButton.setText("Delete");

                        // Set OnClickListener for update button
                        updateButton.setOnClickListener(v -> {
                            // Handle update button click
                            // Replace the current fragment with the RiderFragment
                            Fragment riderFragment = new RiderFragment(currentUser, userList, rideList); // Create a new instance of RiderFragment
                            Bundle args = new Bundle();
                            //  args.putString("rideId", ride.key);
                            riderFragment.setArguments(args);

                            // Replace the current fragment with the RiderFragment
                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, riderFragment)
                                    .addToBackStack(null)  // Optional: Add fragment transaction to back stack
                                    .commit();
                        });

                        // Set OnClickListener for delete button
                        deleteButton.setOnClickListener(v -> {
                            // Remove the ride from the database
                            snapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Ride deleted successfully, remove the ride views from the layout
                                        requestedRidesLayout.removeView(textView);
                                        requestedRidesLayout.removeView(updateButton);
                                        requestedRidesLayout.removeView(deleteButton);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failed deletion
                                        Log.e("HomeScreenFragment", "Failed to delete ride: " + e.getMessage());
                                    });
                        });

                        // Add the TextView, update button, and delete button to the layout
                        requestedRidesLayout.addView(textView);
                        requestedRidesLayout.addView(updateButton);
                        requestedRidesLayout.addView(deleteButton);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                Log.e("HomeScreenFragment", "Database query cancelled: " + databaseError.getMessage());
            }
        });
    }

    private void retrieveOfferedRide(View view) {

        // Query the database for requested rides by the current user
        Query offeredRidesQuery = mDatabase.child("rides").orderByChild("rider").equalTo(null);
        offeredRidesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing layout
                LinearLayout offeredRidesLayout = view.findViewById(R.id.o_rides_layout);
                offeredRidesLayout.removeAllViews();

                // Iterate through the retrieved rides and display them
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    if (ride != null && getContext() != null) {
                        // Create a TextView to display ride information
                        TextView textView = new TextView(getContext());
                        textView.setTextAppearance(getContext(), R.style.MyTextViewStyle);
                        textView.setText("\n\nRide Date: " + ride.date + "\n" + "To: " + ride.goingTo + ", From: " + ride.from);

                        // Create update and delete buttons
                        Button updateButton = new Button(getContext());
                        updateButton.setText("Update");
                        Button deleteButton = new Button(getContext());
                        deleteButton.setText("Delete");

                        // Set OnClickListener for update button
                        updateButton.setOnClickListener(v -> {
                            // Handle update button click
                            // You can implement the update logic here
                            // For example, show a dialog to update ride details
                        });

                        // Set OnClickListener for delete button
                        deleteButton.setOnClickListener(v -> {
                            // Remove the ride from the database
                            snapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Ride deleted successfully, remove the ride views from the layout
                                        offeredRidesLayout.removeView(textView);
                                        offeredRidesLayout.removeView(updateButton);
                                        offeredRidesLayout.removeView(deleteButton);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failed deletion
                                        Log.e("HomeScreenFragment", "Failed to delete ride: " + e.getMessage());
                                    });
                        });

                        // Add the TextView, update button, and delete button to the layout
                        offeredRidesLayout.addView(textView);
                        offeredRidesLayout.addView(updateButton);
                        offeredRidesLayout.addView(deleteButton);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                Log.e("HomeScreenFragment", "Database query cancelled: " + databaseError.getMessage());
            }
        });
    }

    private void retrieveRides() {
        mDatabase.child("rides").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rideList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ride ride = snapshot.getValue(Ride.class);
                    rideList.add(ride);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private List<Ride> filterAcceptedRides(List<Ride> rideList) {
        List<Ride> filteredList = new ArrayList<>();
        retrieveRides();
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

    private List<Ride> filterRequestedRides(List<Ride> rideList) {
        List<Ride> filteredList = new ArrayList<>();
        retrieveRides();
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

    private List<Ride> filterOfferedRides(List<Ride> rideList) {
        List<Ride> filteredList = new ArrayList<>();
        retrieveRides();
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

}
