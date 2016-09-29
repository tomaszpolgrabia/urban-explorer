package pl.tpolgrabia.urbanexplorer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;

import java.util.List;

/**
 * Created by tpolgrabia on 29.09.16.
 */
public class PlacesAdapter extends ArrayAdapter<GooglePlaceResult> {

    public PlacesAdapter(Context context, @NonNull List<GooglePlaceResult> objects) {
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
        TextView placeDescriptionWidget = (TextView) resultView.findViewById(R.id.place_description);
        placeDescriptionWidget.setText(item.getName());

        ImageView placePreviewWidget = (ImageView)resultView.findViewById(R.id.place_img_preview);

        ImageLoader.getInstance().displayImage(
            item.getIcon(),
            placePreviewWidget,
            MainActivity.options);

        return resultView;
    }
}
