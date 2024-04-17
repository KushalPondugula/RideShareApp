package edu.uga.cs.rideshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.List;

public class DriverFragment extends Fragment {
    private User currentUser;
    private List<User> userList;
    public DriverFragment(User currentUser, List<User> userList) {
        // Required empty public constructor
        this.currentUser = currentUser;
        this.userList = userList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);


        // Find the start button
        Button homeButton = view.findViewById(R.id.goBackHome);

        // Set OnClickListener to go to DriverFragment when driverButton is clicked
        homeButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the DriverFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeScreenFragment(currentUser, userList))
                    .commit();
        });



        return view;
    }

}


