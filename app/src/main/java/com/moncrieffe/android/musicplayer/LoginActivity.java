package com.moncrieffe.android.musicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.moncrieffe.android.musicplayer.Credentials.Credentials;
import com.moncrieffe.android.musicplayer.Credentials.CredentialsManager;

import java.util.UUID;

/**
 * Created by Chaz-Rae on 9/7/2016.
 */
public class LoginActivity extends AppCompatActivity{
    private EditText mIpAddress;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mPortNumber;
    private EditText mWebAddress;
    private Button mGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mIpAddress = (EditText)findViewById(R.id.ip_address);
        mUsername = (EditText)findViewById(R.id.username);
        mPassword = (EditText)findViewById(R.id.password);
        mPortNumber = (EditText)findViewById(R.id.port);
        mWebAddress = (EditText)findViewById(R.id.web_address);

        try{
            Credentials credentials = CredentialsManager
                    .get(LoginActivity.this)
                    .getCredentials(UUID.fromString("e8eabaf8-de77-4d16-acae-7c7269cc5d5e"));
            mIpAddress.setText(credentials.getIpaddress());
            mUsername.setText(credentials.getUsername());
            mPassword.setText(credentials.getPassword());
            mPortNumber.setText(Integer.toString(credentials.getPort()));
            mWebAddress.setText(credentials.getWebaddress());
        }
        catch (Exception e){

        }

        mGo = (Button)findViewById(R.id.go_button);
        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mIpAddress.getText().toString();
                String user = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                int port = Integer.valueOf(mPortNumber.getText().toString());
                String web = mWebAddress.getText().toString();

                Credentials credentials = new Credentials(ip, user, password, port, web);
                CredentialsManager.get(LoginActivity.this).addCredentials(credentials);

                Intent i = DirectoryMenuActivity.newIntent(LoginActivity.this, credentials.getID());
                startActivity(i);
            }
        });
    }
}
