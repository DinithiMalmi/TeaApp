package com.example.teaapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity {

    protected final int camera=1;
    protected final int chatbot=2;
    protected final int notifications=3;

    MeowBottomNavigation bottomNavigation;
    RelativeLayout home_layout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        home_layout = findViewById(R.id.home_layout);



        bottomNavigation=findViewById(R.id.bottomNavigation);
        replace(new CameraFragment());
        bottomNavigation.show(camera,true);
        bottomNavigation.add(new MeowBottomNavigation.Model(chatbot,R.drawable.chatbot));
        bottomNavigation.add(new MeowBottomNavigation.Model(camera,R.drawable.home));
        bottomNavigation.add(new MeowBottomNavigation.Model(notifications,R.drawable.notifications));

        meownavigation();
        home_layout.setBackgroundColor(Color.parseColor("white"));


    }

    private void meownavigation(){
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()){

                        case chatbot:

                            replace(new ChatBotFragment());
                            home_layout.setBackgroundColor(Color.parseColor("white"));
                            break;
                        case camera:
                            replace(new CameraFragment());
                            home_layout.setBackgroundColor(Color.parseColor("white"));


                            break;
                        case notifications:
                            replace(new NotificationsFragment());
                            home_layout.setBackgroundColor(Color.parseColor("white"));

                            break;


                }


                return null;
            }
        });
    }

    private void replace(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.commit();
    }
}