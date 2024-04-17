package edu.uga.cs.rideshare;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment {
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find the start button
        Button signUpButton = view.findViewById(R.id.signUp_button);
        Button backButton = view.findViewById(R.id.backSignupButton);
        EditText editTextEmail = view.findViewById(R.id.signUpEmail);
        EditText editTextPassword = view.findViewById(R.id.signUpPassword);


        // Set OnClickListener to go to RiderFragment when riderButton is clicked
        backButton.setOnClickListener((View.OnClickListener) v -> {
            // Replace the LoginFragment with the SplashFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SplashFragment())
                    .commit();
        });

        // Set OnClickListener to start the quiz when the button is clicked
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                if (email.isEmpty() || password.isEmpty()) {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity(), "Enter a username or password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");

                                    // Store user data in Firebase Database
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String userId = user.getUid();
                                    User newUser = new User(email, password);
                                    mDatabase.child("users").child(userId).setValue(newUser);

                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new LoginFragment())
                                            .commit();
                                }
                                else if (password.length() < 6){
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "signInWithPassword:failure", task.getException());
                                    Toast.makeText(getActivity(), "Password must be at least 6 characters",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Check proper email format, or duplicate email",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }
}

