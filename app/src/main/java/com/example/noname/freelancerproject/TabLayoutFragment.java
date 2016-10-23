package com.example.noname.freelancerproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class TabLayoutFragment extends Fragment {


    private ListView listView;
    private Context context;
    ArrayList<String> list;

    public TabLayoutFragment(Context c, ArrayList<String> stringList) {
        context = c;
        list = stringList;
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TabLayoutFragment newInstance(Context c, ArrayList<String> stringList) {
        TabLayoutFragment fragment = new TabLayoutFragment(c, stringList);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(com.example.noname.freelancerproject.R.layout.fragment_tab_layout, container, false);
        listView = (ListView) fragmentView.findViewById(com.example.noname.freelancerproject.R.id.fragmentListView);

        listView.setAdapter(new ListViewAdapter(context,list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(context, "Id "+id, Toast.LENGTH_SHORT).show();
            }
        });
        return fragmentView;
    }

}
