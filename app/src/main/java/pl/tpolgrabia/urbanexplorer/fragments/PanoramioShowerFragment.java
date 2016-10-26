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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.events.PhotoInfoUpdateEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class PanoramioShowerFragment extends Fragment {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioShowerFragment.class);


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        lg.debug("Registering...");
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

        if (imageInfo == null) {
            return inflatedView;
        }

        setContent(inflatedView, imageInfo);

        return inflatedView;
    }

    private void setContent(View view, final PanoramioImageInfo imageInfo) {
        if (imageInfo != null) {
            ImageLoader.getInstance().displayImage(
                imageInfo.getPhotoFileUrl(),
                (ImageView) view.findViewById(R.id.photo_container),
                MainActivity.rectOptions);

            photoTitle = (TextView)view.findViewById(R.id.photo_title);
            photoTitle.setText(imageInfo.getPhotoTitle());

            photoUploadDate = (TextView)view.findViewById(R.id.photo_upload);
            photoUploadDate.setText(imageInfo.getUploadDate());

            photoAuthor = (TextView)view.findViewById(R.id.photo_author);
            photoAuthor.setText(imageInfo.getOwnerName());

            photoUrl = (TextView)view.findViewById(R.id.photo_url);
            photoUrl.setText(imageInfo.getPhotoUrl());
            photoUrl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(imageInfo.getPhotoUrl()));
                    startActivity(intent);
                    return true;
                }
            });

            photoLocation = (TextView)view.findViewById(R.id.photo_location);
            photoLocation.setText(imageInfo.getLatitude() + "," + imageInfo.getLongitude());
            photoLocation.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + imageInfo.getLatitude() + "," +
                                    imageInfo.getLongitude()));
                    startActivity(intent);
                    return true;
                }
            });

        }
    }

    @Subscribe
    public void handlePhotoInfoUpdate(PhotoInfoUpdateEvent event) {
        lg.debug("Photo event: {}", event);
        if (getView() == null) {
            lg.debug("View is not available");
            return;
        }

        final PanoramioImageInfo photoInfo = event.getPhotoInfo();
        if (photoInfo == null) {
            lg.debug("Photo info is not available");
            return;
        }

        setContent(getView(), photoInfo);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1);
        getView().setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        lg.debug("Unregistering...");

        FragmentActivity acc = getActivity();
        if (acc != null) {
            MainActivity mainActivity = (MainActivity)acc;
            mainActivity.resetPhotoInfo();
        }
    }
    
}
