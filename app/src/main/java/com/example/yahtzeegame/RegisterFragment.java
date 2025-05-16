package com.example.yahtzeegame;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yahtzeegame.databinding.FragmentRegisterBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentRegisterBinding binding;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

binding.registerButton.setOnClickListener(v -> {
        String firstName = binding.firstNameInput.getText().toString();
        String lastName = binding.lastNameInput.getText().toString();
        String username = binding.usernameInput.getText().toString();
        String email = binding.emailInput.getText().toString();
        String password = binding.passwordInput.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() ||
                username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all the credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.registerButton.setEnabled(false);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                whatshappening -> {
                    if (whatshappening.isSuccessful()) {
                        FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();

                        DatabaseReference playerRef = FirebaseDatabase.getInstance("https://yahtzeegame-2b1d7-default-rtdb.europe-west1.firebasedatabase.app").getReference("players").child(player.getUid());

                        Map<String, Object> playerData = new HashMap<>();
                        playerData.put("username", username);
                        playerData.put("firstName", firstName);
                        playerData.put("lastName", lastName);
                        playerData.put("email", email);
                        playerData.put("createdAt", ServerValue.TIMESTAMP);
                        
                        String uid = player.getUid();

                        playerRef.setValue(playerData).addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Register succeeded! Lets play, " + username + "!", Toast.LENGTH_SHORT).show();
                            goToGame(username, uid);
                            getActivity().finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Registration error, please try again with different credentials", Toast.LENGTH_SHORT).show();

                            binding.registerButton.setEnabled(true);
                        });

                    } else {
                        Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        binding.registerButton.setEnabled(true);
                    }
                }
        );
    });
    }

    public void goToGame(String username, String uid){
        Intent intent = new Intent(getActivity(), MainActivity2.class);
        intent.putExtra("username", username);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }
}