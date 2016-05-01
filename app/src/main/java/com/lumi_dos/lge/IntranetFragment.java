package com.lumi_dos.lge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class IntranetFragment extends Fragment implements GestureDetector.OnGestureListener{

    public static RelativeLayout layout;
    public static ImageView imageView;
    public static ProgressBar progressBar;
    public static int slideNumber = 1;
    public static ImageButton backButton;
    public static ImageButton nextButton;
    public static WebView webView;
    public GestureDetectorCompat mDetector;
    public SharedPreferences sharedPreferences;
    public static int totalNumberOfSlides = 100;

    public IntranetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_intranet, container, false);
        imageView = (ImageView) layout.findViewById(R.id.imageView);
        webView = (WebView) layout.findViewById(R.id.webView);
        backButton = (ImageButton) layout.findViewById(R.id.intranetBackButton);
        nextButton = (ImageButton) layout.findViewById(R.id.intranetNextButton);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        if(sharedPreferences.getBoolean("getServerBootReport", false)) {
            imageView.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setAppCacheEnabled(false);
            webView.loadUrl("http://colinries.com/lge/intranet_trans.html");
            progressBar.setVisibility(View.GONE);
        } else {
            new GetTotalNumberOfSlidesTask().execute(getString(R.string.slideNumberTxtUrl));
            if (totalNumberOfSlides > slideNumber) new DownloadImageTask().execute(buildSlideUrl());
            backButton.setOnClickListener(clickListener);
            nextButton.setOnClickListener(clickListener);
            adaptButtonOpacity();
        }


        mDetector = new GestureDetectorCompat(getActivity(), this);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });

        if(!sharedPreferences.getBoolean("intranetSwipeHintShown", false)) {
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.hint))
                    .setMessage(getString(R.string.intranetHintMsg))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean("intranetSwipeHintShown", true).apply();
                        }
                    }).show();
        }
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.equals(layout.findViewById(R.id.intranetBackButton))) {
                goToPreviousSlide();
            } else if (v.equals(layout.findViewById(R.id.intranetNextButton))) {
                goToNextSlide();
            }
        }
    };

    public String buildSlideUrl() {
        return getString(R.string.intranet_url_prefix) + slideNumber + getString(R.string.intranet_url_postfix);
    }

    public void goToPreviousSlide() {
        if(slideNumber > 1) {
            slideNumber--;
            new DownloadImageTask().execute(buildSlideUrl());
            adaptButtonOpacity();
        }
    }

    public void goToNextSlide() {
        if(slideNumber < totalNumberOfSlides) {
            slideNumber++;
            new DownloadImageTask().execute((buildSlideUrl()));
            adaptButtonOpacity();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final Tracker tracker = ((LGE) getActivity().getApplication()).getTracker(LGE.TrackerName.APP_TRACKER);
        if(tracker != null) {
            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final Tracker tracker = ((LGE) getActivity().getApplication()).getTracker(LGE.TrackerName.APP_TRACKER);
        if(tracker != null) {
            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX() > e2.getX()) {
            goToNextSlide();
        } else if (e2.getX() > e1.getX()) {
            goToPreviousSlide();
        }
        return true;
    }

    public void adaptButtonOpacity() {
        if (slideNumber <= 1) {
            backButton.setAlpha(0.5f);
        } else {
            backButton.setAlpha(1.0f);
        }
        if (slideNumber >= totalNumberOfSlides) {
            nextButton.setAlpha(0.5f);
        } else {
            nextButton.setAlpha(1.0f);
        }
    }
}
