package edu.uga.cs.rideshare;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.ui.email.RegisterEmailFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    FirebaseAuth mAuth;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Replace the SplashFragment with the QuizFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeScreenFragment())
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        // Find the start button
        Button loginButton = view.findViewById(R.id.loginButton);
        EditText editTextEmail = view.findViewById(R.id.loginEmail);
        EditText editTextPassword = view.findViewById(R.id.loginPassword);
        mAuth = FirebaseAuth.getInstance();
        // Set OnClickListener to start the quiz when the button is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                mAuth.signInWithEmailAndPassword( email, password )
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d( TAG, "signInWithEmail:success" );
                                    // Replace the SplashFragment with the QuizFragment
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new HomeScreenFragment())
                                            .commit();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.d( TAG, "signInWithEmail:failure", task.getException() );
                                    Toast.makeText( getActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        return view;
    }
}
