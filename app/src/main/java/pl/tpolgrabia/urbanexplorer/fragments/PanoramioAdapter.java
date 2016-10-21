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
    public static final int MAX_OWNERNAME_LENGTH = 10;
    public static final int MAX_PANORAMIO_DESCRIPTION_LENGTH = 96;
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
        final String description = item.getPhotoTitle();
        final String trimmedDescription =
            description != null && description.length() > MAX_PANORAMIO_DESCRIPTION_LENGTH
                ? description.substring(0, MAX_PANORAMIO_DESCRIPTION_LENGTH) + "..."
                : description;
        locDesc.setText(trimmedDescription);
        final String photoUrl = item.getPhotoFileUrl();
        ImageView photoImg = (ImageView) itemView.findViewById(R.id.photo_img);
        photoImg.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage));
        ImageLoader.getInstance().displayImage(photoUrl, photoImg, MainActivity.options);


        TextView authorWidget = (TextView) itemView.findViewById(R.id.location_author);
        final String ownerName = item.getOwnerName();
        final String trimmedOwnerName =
            ownerName != null && ownerName.length() > MAX_OWNERNAME_LENGTH
                ? ownerName.substring(0, MAX_OWNERNAME_LENGTH) + "..."
                : ownerName;
        authorWidget.setText(trimmedOwnerName);

        TextView uploadDateWidget = (TextView) itemView.findViewById(R.id.location_upload_date);
        uploadDateWidget.setText(item.getUploadDate());

        return itemView;
    }
}