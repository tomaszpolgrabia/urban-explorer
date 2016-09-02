package pl.tpolgrabia.urbanexplorer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.tpolgrabia.urbanexplorer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {


    public WikiLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_wiki_locations, container, false);
        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("FAKE BUG 2");
            }
        });
        return inflatedView;
    }

}
