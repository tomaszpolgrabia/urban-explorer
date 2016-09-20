package pl.tpolgrabia.urbanexplorer.callbacks;

/**
 * Created by Tomasz Półgrabia <tomasz.polgrabia@unicredit.eu> (c310702) on 20.09.2016.
 */
public interface LocationGeoCoderCallback {
    void callback(int code, String message, String googleStatus, String geocodedLocation);
}
