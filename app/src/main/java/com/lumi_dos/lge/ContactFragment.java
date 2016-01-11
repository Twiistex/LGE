package com.lumi_dos.lge;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactFragment extends Fragment {

    public ContactFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_contact, container, false);

        ListView contactView = (ListView) layout.findViewById(R.id.contactList);

        String[][] listData =
                {{getString(R.string.phone_number_text), getString(R.string.phone_number)},
                        {getString(R.string.email_address), getString(R.string.email_secretary)},
                        {getString(R.string.address), getString(R.string.street_number)+", "+getString(R.string.zip_code_location)},
                        {getString(R.string.developer_label), getString(R.string.app_autor)},
                        {getString(R.string.feedback_address_text), getString(R.string.feedback_email)},
                        {getString(R.string.development_assistance), getString(R.string.development_assistance_name)},
                        {getString(R.string.french_translation), getString(R.string.french_translation_name)},
                        {getString(R.string.design_assistance), getString(R.string.design_assistance_name)}
                };

        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        HashMap<String,String> item;
        for (String[] aListData : listData) {
            item = new HashMap<String, String>();
            item.put("line1", aListData[0]);
            item.put("line2", aListData[1]);
            list.add(item);
        }

        ListAdapter myListAdapter = new SimpleAdapter(getActivity(), list,
                android.R.layout.simple_list_item_2,
                new String[] { "line1","line2" },
                new int[] {android.R.id.text1, android.R.id.text2});
        contactView.setAdapter(myListAdapter);

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
