package pl.tpolgrabia.urbanexplorer.handlers;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.dto.GooglePlacePhoto;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.urbanexplorer.fragments.PlacesFragment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tpolgrabia on 08.10.16.
 */
public class GooglePlacesLongClickItemHandler implements AdapterView.OnItemLongClickListener {

    private Logger lg = LoggerFactory.getLogger(GooglePlacesLongClickItemHandler.class);
    private final Pattern GOOGLE_PLACES_URL_PATTERN = Pattern.compile(".*href=\"(.*)\".*");

    private PlacesFragment placesFragment;
    private final ListView placesWidget;

    public GooglePlacesLongClickItemHandler(PlacesFragment placesFragment, ListView placesWidget) {
        this.placesFragment = placesFragment;
        this.placesWidget = placesWidget;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        GooglePlaceResult item = (GooglePlaceResult) placesWidget.getAdapter().getItem(position);
        if (item.getPhotos() != null && !item.getPhotos().isEmpty()) {
            GooglePlacePhoto alink = item.getPhotos().get(0);
            lg.debug("Photo link: {}", alink);
            final List<String> htmlAttributions = alink.getHtmlAttributions();
            lg.debug("Html attributions: {}", htmlAttributions);
            if (htmlAttributions != null && !htmlAttributions.isEmpty()) {
                String attribute = htmlAttributions.get(0);
                lg.debug("Attribute {}", attribute);
                Matcher matcher = GOOGLE_PLACES_URL_PATTERN.matcher(attribute);
                boolean found = matcher.find();
                if (found) {
                    String link = matcher.group(1);
                    lg.debug("Link: {}", link);
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    placesFragment.getActivity().startActivity(intent);
                } else {
                    lg.warn("Not expected link url html attribute expression {}", attribute);
                }
            }
        }
        return true;
    }
}
