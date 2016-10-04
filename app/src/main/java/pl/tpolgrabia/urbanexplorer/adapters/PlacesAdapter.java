package pl.tpolgrabia.urbanexplorer.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import pl.tpolgrabia.googleutils.dto.GooglePlacePhoto;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;

import java.util.List;

/**
 * Created by tpolgrabia on 29.09.16.
 */
public class PlacesAdapter extends ArrayAdapter<GooglePlaceResult> {


    public PlacesAdapter(Context context, List<GooglePlaceResult> objects) {
        super(context, R.layout.google_place_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView;
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            resultView = inflater.inflate(R.layout.google_place_item, parent, false);
        } else {
            resultView = convertView;
        }

        GooglePlaceResult item = getItem(position);
        if (item.getId().equals(resultView.getTag())) {
            return resultView;
        }

        final List<GooglePlacePhoto> photos = item.getPhotos();
        String photoRef = photos != null && !photos.isEmpty() ? photos.get(0).getPhotoReference() : null;
        String photoUrl = photoRef == null ? null : "https://maps.googleapis.com/maps/api/place/photo?photoreference="
             + photoRef + "&maxwidth=64&key=" + AppConstants.GOOGLE_API_KEY;

        TextView placeDescriptionWidget = (TextView) resultView.findViewById(R.id.place_description);
        placeDescriptionWidget.setText(item.getName());

        TextView placeAddressWidget = (TextView) resultView.findViewById(R.id.place_address);
        placeAddressWidget.setText(item.getVicinity());

        TextView placeRateWidget = (TextView) resultView.findViewById(R.id.place_rate);
        if (item.getRating() != null && !item.getRating().equals(Double.NaN)) {
            placeRateWidget.setText("" + item.getRating());
        } else {
            placeRateWidget.setText("N/A");
        }

        ImageView placePreviewWidget = (ImageView)resultView.findViewById(R.id.place_img_preview);
        placePreviewWidget.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage));

        if (photoUrl != null ) {
            ImageLoader.getInstance().displayImage(
                photoUrl,
                placePreviewWidget,
                MainActivity.options);
        }

        resultView.setTag(item.getId());

        return resultView;
    }
}
