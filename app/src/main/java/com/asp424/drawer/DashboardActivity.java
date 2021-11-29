package com.asp424.drawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
public class DashboardActivity extends Activity {

    EditText secretCodeBox;
    Button joinBtn, shareBtn;
    ImageView riple;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //      Toolbar toolbar = findViewById(R.id.toolbar);
        riple = findViewById(R.id.btn_arrow);
//        setTitle(toolbar.getTitle());

        riple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        secretCodeBox = findViewById(R.id.codeBox);
        joinBtn = findViewById(R.id.joinBtn);
        shareBtn = findViewById(R.id.shareBtn);

        URL serverURL;

        try {
            serverURL = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions =
                    new JitsiMeetConferenceOptions.Builder()
                            .setServerURL(serverURL)
                            .setWelcomePageEnabled(false)
                            .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JitsiMeetConferenceOptions options = null;
                try {
                    options = new JitsiMeetConferenceOptions.Builder()
                            .setRoom(secretCodeBox.getText().toString())
                            .setServerURL(new URL("https://meet.jit.si"))
                            .setWelcomePageEnabled(false)
                            .build();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                JitsiMeetActivity.launch(DashboardActivity.this, options);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String sharebody = "look all Programmings";
                String subject = "kviliti";
                intent.putExtra(Intent.EXTRA_SUBJECT,sharebody);
                intent.putExtra(Intent.EXTRA_TEXT,subject);
                startActivity(Intent.createChooser(intent,"AVADH TUTOR"));


            }
        });
    }

}
