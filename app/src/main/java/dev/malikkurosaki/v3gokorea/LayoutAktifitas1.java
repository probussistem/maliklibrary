package dev.malikkurosaki.v3gokorea;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Sampler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapzen.speakerbox.Speakerbox;
import com.tooltip.OnDismissListener;
import com.tooltip.Tooltip;
import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;
import com.xd.commander.translatetool.GoogleTrans;
import com.xd.commander.translatetool.OnTransSuccess;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

public class LayoutAktifitas1 extends Fragment{

    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private View v;
    private DroidSpeech droidSpeech;
    private ImageView micBicara;
    private TextView result;

    private String TAG = "-->";
    private boolean sedangBicara = false;
    private Speakerbox speakerbox;
    private TextView papanText;
    private TextView blink;
    private String hasilFinal;
    private String hasilTerjemah;
    private TextView nilaiScore;
    private TextView waktu;
    private TextView namaDisplay;
    private String nama;

    private int nil = 60;
    private boolean mulaiHitung = false;
    private DatabaseReference db;


    LayoutAktifitas1 newInstance(){
        return new LayoutAktifitas1();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_aktifitas1,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.v = view;
        this.context = v.getContext();
        this.activity = (Activity)context;

        db = FirebaseDatabase.getInstance().getReference();

        Paper.init(context);
        droidSpeech = new DroidSpeech(context, null);
        speakerbox = new Speakerbox(activity.getApplication());
        micBicara = v.findViewById(R.id.micBicara);
        result = v.findViewById(R.id.result);
        papanText = v.findViewById(R.id.papanText);
        blink = v.findViewById(R.id.blink);
        nilaiScore = v.findViewById(R.id.nilaiScore);
        waktu = v.findViewById(R.id.waktu);
        namaDisplay = v.findViewById(R.id.namaDisplay);

        result.setVisibility(View.GONE);
        //droidSpeech.setPreferredLanguage("en");
        blink.setVisibility(View.GONE);


        Paper.init(context);
        nama = Paper.book().read("nama");
        if (nama != null){
            namaDisplay.setText(nama);
        }

        new Tooltip.Builder(micBicara)
        .setGravity(Gravity.TOP)
        .setText("mulai")
        .setPadding(16)
        .setTextSize(24f)
        .setTextColor(getResources().getColor(R.color.colorPutih))
        .setCancelable(true)
        .setBackgroundColor(getResources().getColor(R.color.colorOrange2))
        .show();


        micBicara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sedangBicara){
                    micBicara.setImageResource(R.drawable.icon_mic_on_50);
                    result.setVisibility(View.VISIBLE);
                    droidSpeech.startDroidSpeechRecognition();
                    droidSpeech.setContinuousSpeechRecognition(true);

                    sedangBicara = true;
                }else {
                    micBicara.setImageResource(R.drawable.icon_mic_50);
                    result.setVisibility(View.GONE);
                    droidSpeech.closeDroidSpeechOperations();
                    sedangBicara = false;
                    result.setText("");
                }
                if (!mulaiHitung){
                    mulaiHitung = true;
                    penghitungan();
                }
            }
        });


        droidSpeech.setOnDroidSpeechListener(new OnDSListener() {
            @Override
            public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {
                droidSpeech.setPreferredLanguage("ko-KR");
                Log.i(TAG, "onDroidSpeechSupportedLanguages: "+ TextUtils.join(",",supportedSpeechLanguages));

            }

            @Override
            public void onDroidSpeechRmsChanged(float rmsChangedValue) {
                int angka = (int) rmsChangedValue;

                if (angka > 6){
                    YoYo.with(Techniques.FadeIn).duration(500).playOn(micBicara);
                }

                if (!sedangBicara){
                    droidSpeech.closeDroidSpeechOperations();
                }
            }

            @Override
            public void onDroidSpeechLiveResult(String liveSpeechResult) {

            }

            @Override
            public void onDroidSpeechFinalResult(String finalSpeechResult) {
                hasilFinal = finalSpeechResult;
                result.setText(finalSpeechResult);

                GoogleTrans.build().with(finalSpeechResult).setFrom("ko").setTo("id").into(new OnTransSuccess() {
                    @Override
                    public void out(String s) {
                        hasilTerjemah = s;
                        blink.setVisibility(View.VISIBLE);
                        blink.setText(s);
                        YoYo.with(Techniques.ZoomIn).duration(500).withListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                blink.setVisibility(View.GONE);
                                papanText.append(hasilTerjemah+"\n");
                                int scr = papanText.getText().toString().trim().length() * 12;
                                nilaiScore.setText(String.valueOf(scr));
                                MediaPlayer player = MediaPlayer.create(getContext(),R.raw.coin);
                                player.start();
                            }
                        }).playOn(blink);


                    }
                });
            }

            @Override
            public void onDroidSpeechClosedByUser() {

            }

            @Override
            public void onDroidSpeechError(String errorMsg) {

            }
        });
    }


    void penghitungan(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (nil > 0){
                    nil--;
                    handler.postDelayed(this,500);
                    waktu.setText("00:" + nil);
                }else {
                    handler.removeCallbacks(this);
                    Toast.makeText(getContext(),"selesai",Toast.LENGTH_LONG).show();
                    micBicara.setImageResource(R.drawable.icon_mic_50);
                    result.setVisibility(View.GONE);
                    droidSpeech.closeDroidSpeechOperations();
                    sedangBicara = false;
                    result.setText("");

                    String fScore = nilaiScore.getText().toString().trim();
                    if (TextUtils.isEmpty(fScore)){
                        Toast.makeText(getContext(),"PAYAH",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(),MainActivity.class));
                        droidSpeech.closeDroidSpeechOperations();
                    }else {
                        Map<String,Object> kirimScore = new HashMap<>();
                        kirimScore.put("nama",nama);
                        kirimScore.put("score",nilaiScore.getText().toString().trim());

                        db.child("score").child(nama).setValue(kirimScore).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    droidSpeech.closeDroidSpeechOperations();
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();
                                    transaction.replace(R.id.aktifitasContainer,new LayoutActifitas2());
                                    transaction.commitAllowingStateLoss();
                                }
                            }
                        });
                    }



                }

            }
        },500);
    }

}
