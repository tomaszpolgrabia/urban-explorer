package pl.tpolgrabia.urbanexplorer.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.utils.GeocoderUtils;
import pl.tpolgrabia.panoramiobindings.utils.PanoramioUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.callbacks.geocoder.GeocodedLocationCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.panoramio.FetchAdditionalPanoramioPhotosCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.panoramio.PanoramioLocationCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.panoramio.PanoramioProviderCallback;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioRequest;
import pl.tpolgrabia.urbanexplorer.worker.PanoramioWorker;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;
import pl.tpolgrabia.urbanexplorer.handlers.PanoramioItemLongClickHandler;
import pl.tpolgrabia.urbanexplorer.handlers.PanoramioLocationsScrollListener;
import pl.tpolgrabia.urbanexplorer.utils.*;
import pl.tpolgrabia.urbanexplorerutils.utils.DebugUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.SettingsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  {

    private static final Logger lg = LoggerFactory.getLogger(HomeFragment.class);

    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final String PHOTO_LIST = "PHOTO_LIST_KEY";
    private boolean initialized = false;
    private View inflatedView;
    private Semaphore loading;
    private ArrayList<PanoramioImageInfo> photos;
    private boolean noMorePhotos;
    private String currentGeocodedLocation;
    private GeocoderUtils geocoderUtils;
    private PanoramioLocationCallback locationChangedHandler;
    private PanoramioProviderCallback providerChangedHandler;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lg.trace("onCreate {}", System.identityHashCode(this));
        EventBus.getDefault().register(this);
        loading = new Semaphore(1, true);
        noMorePhotos = false;
        updateLocationInfo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        locationChangedHandler = new PanoramioLocationCallback(this);
        providerChangedHandler = new PanoramioProviderCallback(this);

        EventBus.getDefault().register(locationChangedHandler);
        EventBus.getDefault().register(providerChangedHandler);

        geocoderUtils = new GeocoderUtils(getActivity(), AppConstants.GOOGLE_API_KEY);
    }

    public void updateGeocodedLocation() {
        if (getActivity() == null) {
            lg.debug("Activity still not attached");
            return;
        }

        Location currLocation = LocationUtils.getLastKnownLocation(getActivity());
        lg.debug("Current location is {}", currLocation);
        if (currLocation == null) {
            lg.debug("Current location is not available");
            return;
        }

        geocoderUtils.getGeoCodedLocation(
            currLocation.getLatitude(),
            currLocation.getLongitude(),
            new GeocodedLocationCallback(this));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        lg.trace("TAG: {}", getTag());
        DebugUtils.dumpFragments(getFragmentManager().getFragments());

        inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        ListView locations = (ListView)inflatedView.findViewById(R.id.locations);
        final ListView finalLocations = locations;
        locations.setOnItemLongClickListener(new PanoramioItemLongClickHandler(this, finalLocations));

        initialized = true;

        lg.trace("Saved instance state {}", savedInstanceState);
        if (photos == null) {
            photos = PanoramioCacheUtils.loadPhotosFromCache(this, savedInstanceState);
        }

        locations.setAdapter(new PanoramioAdapter(getActivity(), R.layout.location_item, photos));
        lg.trace("Photos initialized {}", photos);

        lg.trace("Photos size: {}", photos.size());
        locations.setOnScrollListener(new PanoramioLocationsScrollListener(this));
        return inflatedView;
    }

    public void fetchAdditionalPhotos() throws InterruptedException {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.trace("Activity shouldn't be null. No headless fragment");
            return;
        }
        MainActivity mainActivity = (MainActivity)getActivity();
        if (noMorePhotos) {
            lg.trace("No more photos - last query was zero result");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }
        if (!initialized) {
            lg.trace("Fetching additional photos blocked till system is initialized");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }
        if (getView() == null) {
            lg.trace("Application still not initialized");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }
        final Location location = LocationUtils.getLastKnownLocation(activity);
        if (location == null) {
            lg.info("Location still not available");
            Toast.makeText(activity, "Location still not available", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }
        lg.trace("Fetching additional photos. Trying loading acquirng lock");
        if (!loading.tryAcquire()) {
            lg.info("Fetching additional photos. Loading in progress");
            return;
        }
        int offset = photos.size();
        lg.debug("Fetching additional photos offset: {}, count: {}", offset, SettingsUtils.getPanoramioBulkDataSize(getActivity()));
        PanoramioUtils.fetchPanoramioImages(
            activity,
            location.getLatitude(),
            location.getLongitude(),
            SettingsUtils.fetchRadiusX(getActivity()),
            SettingsUtils.fetchRadiusY(getActivity()),
            (long)(offset),
            fetchLocationPageSize(),
            new FetchAdditionalPanoramioPhotosCallback(this, activity)
        );
    }

    private Long fetchLocationPageSize() {
        return Long.valueOf(SettingsUtils.getPanoramioBulkDataSize(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Panoramio search");
        lg.trace("onResume");
        if (currentGeocodedLocation != null) {
            updateLocationInfo();
        }
        else {
            updateGeocodedLocation();
        }
    }

    public void updateLocationInfo() {
        lg.trace("Update locations info");
        final View view = getView();
        if (view == null) {
            lg.warn("Fragment has no view");
            return;
        }
        final TextView locationInfo = (TextView) view.findViewById(R.id.locationInfo);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity should'nt be null. No headless fragment");
            return;
        }
        final Location currLocation = LocationUtils.getLastKnownLocation(activity);
        lg.trace("Current location: {}, locationInfo: {}", currLocation, locationInfo);
        locationInfo.setText(currentGeocodedLocation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lg.trace("onDestroy");
        EventBus.getDefault().unregister(this);
        PanoramioCacheUtils.savePhotosToCache(getActivity(), photos);

        // FIXME true reason is that hot swapping doesn't fire the activityCreated event while it was recreated
        if (locationChangedHandler != null) {
            // FIXME only avoiding crash
            EventBus.getDefault().unregister(locationChangedHandler);
        }
        if (providerChangedHandler != null) {
            // FIXME only avoiding crash
            EventBus.getDefault().unregister(providerChangedHandler);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        lg.trace("Saving state");
        super.onSaveInstanceState(outState);
        outState.putSerializable(PHOTO_LIST, photos);
        lg.trace("Saved photos: {}", photos);
    }

    @Subscribe
    public void refresh(RefreshEvent refreshEvent) {
        lg.debug("Refreshing event...");
        lg.trace("Fetch panoramio photos");
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity shouldn't be null. It isn't headless fragment");
            return;
        }

        final Location location = LocationUtils.getLastKnownLocation(activity);
        if (location == null) {
            lg.info("Location is still not available");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            Toast.makeText(getActivity(), "Location is still not available", Toast.LENGTH_SHORT).show();
            return;
        }

        PanoramioRequest req = new PanoramioRequest();
        req.setLatitude(location.getLatitude());
        req.setLongitude(location.getLongitude());
        req.setOffset(0L);
        req.setCount(fetchLocationPageSize());
        req.setRadiusX(SettingsUtils.fetchRadiusX(getActivity()));
        req.setRadiusY(SettingsUtils.fetchRadiusY(getActivity()));
        new PanoramioWorker(this).execute(req);

//        PanoramioUtils.fetchPanoramioImages(
//            activity,
//            location.getLatitude(),
//            location.getLongitude(),
//            SettingsUtils.fetchRadiusX(getActivity()),
//            SettingsUtils.fetchRadiusY(getActivity()),
//            0L,
//            fetchLocationPageSize(),
//            new FetchPanoramioPhotosCallback(this, activity)
//        );
    }

    public Semaphore getLoading() {
        return loading;
    }

    public void setNoMorePhotos(boolean noMorePhotos) {
        this.noMorePhotos = noMorePhotos;
    }

    public int getPhotosCount() {
        return photos.size();
    }

    public void addPhotos(List<PanoramioImageInfo> images) {
        photos.addAll(images);
    }

    public void setCurrentGeocodedLocation(String currentGeocodedLocation) {
        this.currentGeocodedLocation = currentGeocodedLocation;
    }

    public void setPhotos(ArrayList<PanoramioImageInfo> photos) {
        this.photos = photos;
    }

}
