package pl.tpolgrabia.urbanexplorer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanoramioShowerFragment extends Fragment {


    public static final String PANORAMIO_PHOTO_ARG_KEY = "PANORAMIO_PHOTO_ARG_KEY";
    private TextView photoTitle;

    public PanoramioShowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle arguments = getArguments();
        final View inflatedView = inflater.inflate(R.layout.fragment_panoramio_shower, container, false);

        if (arguments == null) {
            return inflatedView;
        }

        PanoramioImageInfo imageInfo = (PanoramioImageInfo) arguments.getSerializable(PANORAMIO_PHOTO_ARG_KEY);

        if (imageInfo != null) {
            ImageLoader.getInstance().displayImage(
                imageInfo.getPhotoFileUrl(),
                (ImageView) inflatedView.findViewById(R.id.photo_container),
                MainActivity.options);

            photoTitle = (TextView)inflatedView.findViewById(R.id.phot_title);
            photoTitle.setText(imageInfo.getPhotoTitle());
        }

        return inflatedView;
    }

}
