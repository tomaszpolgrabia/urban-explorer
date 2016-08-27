package pl.tpolgrabia.urbanexplorer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.nostra13.universalimageloader.core.ImageLoader;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;

import java.util.List;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioAdapter extends ArrayAdapter<PanoramioImageInfo> {
    private final AQuery aq;

    public PanoramioAdapter(FragmentActivity activity, int location_item, List<PanoramioImageInfo> photosDescriptions) {
        super(activity, location_item, photosDescriptions);
        aq = new AQuery(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.location_item, parent, false);
        TextView locDesc = (TextView) itemView.findViewById(R.id.location_description);
        locDesc.setText(getItem(position).getPhotoTitle());
        final String photoUrl = getItem(position).getPhotoFileUrl();
        ImageView photoImg = (ImageView) itemView.findViewById(R.id.photo_img);
        // photoImg.setImageBitmap(bm);
        // photoImg.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher));
        ImageLoader.getInstance().displayImage(photoUrl, photoImg, MainActivity.options);
        return itemView;
    }
}