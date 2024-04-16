package edu.uga.cs.rideshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

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


        // Set OnClickListener to logout back to splash screen when clicked
        logoutButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the HomeScreenFragment with the SplashFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SplashFragment())
                    .commit();
        });



        return view;
    }

}

