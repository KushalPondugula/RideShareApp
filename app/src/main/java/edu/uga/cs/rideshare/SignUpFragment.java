package edu.uga.cs.rideshare;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SignUpFragment extends Fragment {
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        // Find the start button
        Button submitButton = view.findViewById(R.id.submit_button);


        // Set OnClickListener to start the quiz when the button is clicked
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the SplashFragment with the QuizFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new QuizContainerFragment(countryData))
                        .commit();
            }
        });



        return view;
    }

}



