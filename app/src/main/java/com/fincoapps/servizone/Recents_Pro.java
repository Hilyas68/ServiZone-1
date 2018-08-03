package com.fincoapps.servizone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fincoapps.servizone.adapters.ExpertAdapter;
import com.fincoapps.servizone.models.ExpertModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Type;

public class Recents_Pro extends Fragment {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Type collectionType;
    private ArrayList<ExpertModel> historyList = new ArrayList<ExpertModel>();
    ListView listViewHistory;
    private ExpertAdapter historyAdapter;

    public Recents_Pro() {
        // Required empty public constructor
    }

    public static Recents_Pro newInstance(String param1, String param2) {
        Recents_Pro fragment = new Recents_Pro();
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

        System.out.println("Recent Contacted Professional Page");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View l = inflater.inflate(R.layout.fragment_recents_, container, false);


        //================= PROFESSION LISTVIEW ====================
        Gson gson = new Gson();
        collectionType = new TypeToken<List<ExpertModel>>(){}.getType();
        historyList.addAll((ArrayList)gson.fromJson("[     {         \"id\": 1,         \"name\": \"Efetobor\", \t\"profession\": \"Web Designer\"     }, {         \"id\": 2,         \"name\": \"Cynthia\", \t\"profession\": \"Economist\"     }, {         \"id\": 3,         \"name\": \"Lisa\", \t\"profession\": \"Marketer\"     }, {         \"id\": 4,         \"name\": \"Josh\", \t\"profession\": \"Web Developer\"     }, {         \"id\": 5,         \"name\": \"Debby\", \t\"profession\": \"Graphics Designer\"     }, ]", collectionType));
        listViewHistory = (ListView)l.findViewById(R.id.listViewRecentContacts);
        historyAdapter = new ExpertAdapter(historyList, getActivity(), Recents_Pro.this);
        listViewHistory.setAdapter(historyAdapter);
        return l;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Fragment newInstance() {
        Recents_Pro f = new Recents_Pro();

        Bundle bdl = new Bundle(1);

        f.setArguments(bdl);

        return f;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
