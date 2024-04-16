package edu.uga.cs.rideshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        // Find the start button
        Button signUpButton = view.findViewById(R.id.loginButton);


        // Set OnClickListener to start the quiz when the button is clicked
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the SplashFragment with the QuizFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeScreenFragment())
                        .commit();
            }
        });



        return view;
    }
}
