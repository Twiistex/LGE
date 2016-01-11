package com.lumi_dos.lge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ScheduleFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static WebView timetable_webview;
    public SharedPreferences sharedPreferences;
    public static String timetable_url;

    public ScheduleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_schedule, container, false);

        Spinner class_selector = (Spinner) getActivity().findViewById(R.id.class_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.classes, R.layout.spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_item);

        class_selector.setAdapter(adapter);
        class_selector.setVisibility(View.VISIBLE);

        class_selector.setOnItemSelectedListener(this);

        String my_class = sharedPreferences.getString("my_class", getString(R.string.select_class));
        class_selector.setSelection(getIndex(class_selector, my_class));
        timetable_webview = (WebView) layout.findViewById(R.id.webview);

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.class_selector).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position != 0) {

            String position_string = Integer.toString(position);

            while(position_string.length() < 2) {
                position_string = "0" + position_string;
            }

            //TODO: review code (avoid hard coding)
            // String timetable_url = getString(R.string.timetable_address_prefix) + position_string + getString(R.string.timetable_address_suffix);

            timetable_url = "http://lge.lu/horaires/39/c/c000" + position_string + ".htm";

            WebSettings timetable_settings = timetable_webview.getSettings();
            timetable_settings.setSupportZoom(true);
            timetable_settings.setBuiltInZoomControls(true);
            timetable_webview.setInitialScale(150);

            timetable_webview.loadUrl(timetable_url);

        } else {
            Toast.makeText(getContext(), getString(R.string.select_class), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        Toast.makeText(getContext(), getString(R.string.select_class), Toast.LENGTH_SHORT).show();
    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
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
}
