package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanoramioShowerFragment extends Fragment {


    public static final String PANORAMIO_PHOTO_ARG_KEY = "PANORAMIO_PHOTO_ARG_KEY";
    public static final String TAG = "PANORAMIO_TAG";
    private TextView photoTitle;
    private TextView photoUploadDate;
    private TextView photoAuthor;
    private TextView photoUrl;
    private TextView photoLocation;

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

        final PanoramioImageInfo imageInfo = (PanoramioImageInfo) arguments.getSerializable(PANORAMIO_PHOTO_ARG_KEY);

        if (imageInfo != null) {
            ImageLoader.getInstance().displayImage(
                imageInfo.getPhotoFileUrl(),
                (ImageView) inflatedView.findViewById(R.id.photo_container),
                MainActivity.rectOptions);

            photoTitle = (TextView)inflatedView.findViewById(R.id.photo_title);
            photoTitle.setText(imageInfo.getPhotoTitle());

            photoUploadDate = (TextView)inflatedView.findViewById(R.id.photo_upload);
            photoUploadDate.setText(imageInfo.getUploadDate());

            photoAuthor = (TextView)inflatedView.findViewById(R.id.photo_author);
            photoAuthor.setText(imageInfo.getOwnerName());

            photoUrl = (TextView)inflatedView.findViewById(R.id.photo_url);
            photoUrl.setText(imageInfo.getPhotoUrl());

            photoLocation = (TextView)inflatedView.findViewById(R.id.photo_location);
            photoLocation.setText(imageInfo.getLatitude() + "," + imageInfo.getLongitude());
            photoLocation.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + imageInfo.getLatitude() + "," +
                                    imageInfo.getLongitude()));
                    startActivity(intent);
                    return true;
                }
            });

        }

        return inflatedView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FragmentActivity acc = getActivity();
        if (acc != null) {
            MainActivity mainActivity = (MainActivity)acc;
            mainActivity.resetPhotoInfo();
        }
    }
    
}
