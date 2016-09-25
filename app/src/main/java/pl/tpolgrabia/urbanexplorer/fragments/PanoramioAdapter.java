package pl.tpolgrabia.urbanexplorer.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
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
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorerutils.utils.NetUtils;

import java.util.List;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioAdapter extends ArrayAdapter<PanoramioImageInfo> {
    private final AQuery aq;

    public PanoramioAdapter(FragmentActivity activity, int location_item, List<PanoramioImageInfo> photosDescriptions) {
        super(activity, location_item, photosDescriptions);
        aq = NetUtils.createProxyAQueryInstance(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = convertView != null ? convertView : inflater.inflate(R.layout.location_item, parent, false);
        final PanoramioImageInfo item = getItem(position);

        if (item.getPhotoId() != null && item.getPhotoId().equals(itemView.getTag())) {
            // if it is the the same object f.e. add new objects to the collection (without the slide)
            // the refresh makes blinking without this
            return itemView;
        }

        TextView locDesc = (TextView) itemView.findViewById(R.id.location_description);
        itemView.setTag(item.getPhotoId());
        locDesc.setText(item.getPhotoTitle());
        final String photoUrl = item.getPhotoFileUrl();
        ImageView photoImg = (ImageView) itemView.findViewById(R.id.photo_img);
        photoImg.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage));
        ImageLoader.getInstance().displayImage(photoUrl, photoImg, MainActivity.options);
        return itemView;
    }
}