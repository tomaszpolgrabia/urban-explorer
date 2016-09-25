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
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;

import java.util.List;

/**
 * Created by tpolgrabia on 01.09.16.
 */
public class WikiLocationsAdapter extends ArrayAdapter<WikiAppObject> {
    public WikiLocationsAdapter(Context ctx, List<WikiAppObject> locations) {
        super(ctx, R.layout.wiki_locations_item, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView;
        // reusing old view
        itemView = convertView != null ? convertView : inflater.inflate(R.layout.wiki_locations_item, parent, false);

        WikiAppObject wikiPage = getItem(position);
        if (wikiPage.getPageId() != null && wikiPage.getPageId().equals(itemView.getTag())) {
            // all data were previously loaded
            return itemView;
        }


        itemView.setTag(wikiPage.getPageId());
        // wiki page image preview
        ImageView imgPreview = (ImageView) itemView.findViewById(R.id.wiki_locs_item_img_preview);
        String url = wikiPage.getThumbnail() != null ? wikiPage.getThumbnail() : null;

        TextView locDistanceInfo = (TextView) itemView.findViewById(R.id.wiki_locs_item_distance);
        locDistanceInfo.setText(String.format("%.2f km", wikiPage.getDistance() / 1000.0));
        imgPreview.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage));

        if (url != null) {
            ImageLoader.getInstance().displayImage(
                url,
                imgPreview,
                MainActivity.options);
        }

        // wiki page title
        TextView pageTitle = (TextView) itemView.findViewById(R.id.wiki_locs_item_title);
        pageTitle.setText(wikiPage.getTitle());


        return itemView;
    }
}
