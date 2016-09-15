package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.androidquery.AQuery;
import pl.tpolgrabia.urbanexplorer.AppConstants;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Tomasz Półgrabia <tomasz.polgrabia@unicredit.eu> (c310702) on 15.09.2016.
 */
public class NetUtils {
    private static final String CLASS_TAG = NetUtils.class.getSimpleName();

    public static AQuery createProxyAQueryInstance(Context ctx) {
        final AQuery aq = new AQuery(ctx);
        try {
            Log.v(CLASS_TAG, "Creating aquery proxy instance");
            SharedPreferences sharedPrefs = getDefaultSharedPreferences(ctx);
            boolean enabled = isProxyEnabled(ctx);
            Log.v(CLASS_TAG, "Proxy is enabled: " + enabled);
            if (!enabled) {
                return aq;
            }

            String httpProxyHost = sharedPrefs.getString(AppConstants.PREF_HTTP_PROXY_HOST_KEY,
                    AppConstants.DEF_HTTP_PROXY_HOST);

            String httpProxyPort = sharedPrefs.getString(AppConstants.PREF_HTTP_PROXY_PORT_KEY,
                    AppConstants.DEF_HTTP_PROXY_PORT);

            Log.v(CLASS_TAG, "Proxy is enabled, host: " + httpProxyHost + ", port: " + httpProxyPort);


            if (httpProxyHost == null) {
                return aq;
            }

            Log.v(CLASS_TAG, "Proxy host: " + httpProxyHost + ", proxy port: " + httpProxyPort);
            aq.proxy(httpProxyHost, Integer.parseInt(httpProxyPort));
        } catch (NumberFormatException e) {
            Log.w(CLASS_TAG, "Invalid proxy auth number format", e);
        }

        return aq;
    }

    public static void setGlobalProxyAuth(Context ctx) {
        Log.v(CLASS_TAG, "Setting proxy auth");
        if (isProxyEnabled(ctx)) {
            Log.v(CLASS_TAG, "Setting custom proxy auth");
            SharedPreferences sharedPrefs = getDefaultSharedPreferences(ctx);
            final String httpProxyUser = sharedPrefs.getString(AppConstants.PREF_HTTP_PROXY_USER_KEY,
                    AppConstants.DEF_HTTP_PROXY_USER);

            final String httpProxyPass = sharedPrefs.getString(AppConstants.PREF_HTTP_PROXY_PASSWORD_KEY,
                    AppConstants.DEF_HTTP_PROXY_PASSWORD);

            setGlobalProxyAuth(httpProxyUser, httpProxyPass);
        } else {
            Authenticator.setDefault(null);
            Log.v(CLASS_TAG, "Setting empty proxy auth");
        }
    }

    private static SharedPreferences getDefaultSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private static void setGlobalProxyAuth(final String httpProxyUser, final String httpProxyPass) {
        Log.v(CLASS_TAG, "Proxy user: " + httpProxyUser + ", proxy pass: " + httpProxyPass);
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                Log.v(CLASS_TAG, "Proxy auth try");
                return new PasswordAuthentication(httpProxyUser,httpProxyPass.toCharArray());
            }
        });
    }

    public static boolean isProxyEnabled(Context ctx) {
        SharedPreferences sharedPrefs =  getDefaultSharedPreferences(ctx);
        return sharedPrefs.getBoolean(
                        AppConstants.PREF_HTTP_PROXY_ENABLED_KEY,
                        AppConstants.DEF_HTTP_PROXY_ENABLED);
    }

}
