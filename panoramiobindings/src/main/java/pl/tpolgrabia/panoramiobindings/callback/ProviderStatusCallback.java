package pl.tpolgrabia.panoramiobindings.callback;

/**
 * Created by Tomasz Półgrabia <tomasz.polgrabia@unicredit.eu> (c310702) on 19.09.2016.
 */
public interface ProviderStatusCallback {
    void callback(String provider, boolean enabled);
}
