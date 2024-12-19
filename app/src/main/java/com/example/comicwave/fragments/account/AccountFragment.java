package com.example.comicwave.fragments.account;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.comicwave.ChangePasswordActivity;
import com.example.comicwave.LoginActivity;
import com.example.comicwave.R;
import com.example.comicwave.models.User;
import com.example.comicwave.repositories.UserRepository;

public class AccountFragment extends Fragment {

    private Button accountLogoutButton;
    private TextView accountNameText, accountEmailText;
    private CardView accountChangePassword;
    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    private User user;
    private void initComponents(View view) {
        accountLogoutButton = view.findViewById(R.id.accountLogoutButton);
        accountNameText = view.findViewById(R.id.accountNameText);
        accountEmailText = view.findViewById(R.id.accountEmailText);

        UserRepository.getCurrentUser(result -> {
            if (!isAdded() || isRemoving()) return;
            user = result;
            accountNameText.setText(user.getName());
            accountEmailText.setText(user.getEmail());
        });

        accountLogoutButton.setOnClickListener(e -> {
            UserRepository.logout();

            Intent intent = new Intent(getContext(), LoginActivity.class);
            getContext().startActivity(intent);
            ((Activity) getContext()).finish();
        });

        accountChangePassword = view.findViewById(R.id.accountChangePassword);
        accountChangePassword.setOnClickListener(e -> {
            Intent i = new Intent(getContext(), ChangePasswordActivity.class);
            startActivity(i);
        });
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initComponents(view);
        return view;
    }

}