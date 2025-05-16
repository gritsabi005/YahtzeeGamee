package com.example.yahtzeegame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;

import com.example.yahtzeegame.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.loginButton.setOnClickListener(v -> {
            String username = binding.usernameInput.getText().toString();
            String password = binding.passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Please check and fill all the credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.loginButton.setEnabled(false);

            // First find the user's email from the database
            DatabaseReference playersRef = FirebaseDatabase.getInstance("https://yahtzeegame-2b1d7-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("players");

            playersRef.orderByChild("username").equalTo(username).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            DataSnapshot playerSnapshot = snapshot.getChildren().iterator().next();
                            String email = playerSnapshot.child("email").getValue(String.class);
                            String uid = playerSnapshot.getKey();

                            // Now try to sign in with Firebase Auth
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(whatsHappening -> {
                                        if (whatsHappening.isSuccessful()) {
                                            Toast.makeText(getContext(), "Lets play again, " + username + "!", Toast.LENGTH_SHORT).show();
                                            goToGame(username, uid);
                                            getActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), "You sure your credentials matches luv?", Toast.LENGTH_SHORT).show();
                                            binding.loginButton.setEnabled(true);
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Register first luv.", Toast.LENGTH_SHORT).show();
                            binding.loginButton.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "You sure you have internet connection luv?", Toast.LENGTH_SHORT).show();
                        binding.loginButton.setEnabled(true);
                    });
        });

        binding.goToRegister.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.login_to_register);
        });
    }

    public void goToGame(String username, String uid){
        Intent intent = new Intent(getActivity(), MainActivity2.class);
        intent.putExtra("username", username);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}