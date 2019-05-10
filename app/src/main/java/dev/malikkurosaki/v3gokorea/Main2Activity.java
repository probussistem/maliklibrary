package dev.malikkurosaki.v3gokorea;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.mapzen.speakerbox.Speakerbox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    private Fragment containerTujuan;
    private Speakerbox speakerbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        speakerbox = new Speakerbox(this.getApplication());


        Intent terima = getIntent();
        Map<String,Object> halaman = (HashMap<String, Object>)terima.getSerializableExtra("halaman");
        String halamannya = String.valueOf(halaman.get("halaman"));

        if (halamannya.equals("aktifitas1")){
            containerTujuan = new LayoutAktifitas1();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.aktifitasContainer,containerTujuan);
            transaction.commitAllowingStateLoss();
        }



    }
}
