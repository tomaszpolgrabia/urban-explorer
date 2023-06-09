package pl.tpolgrabia.urbanexplorerutils.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import com.androidquery.AQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorerutils.constants.UtilConstants;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Tomasz Półgrabia <tomasz.polgrabia@unicredit.eu> (c310702) on 15.09.2016.
 */
public class NetUtils {
    private static final Logger lg = LoggerFactory.getLogger(NetUtils.class);

    public static AQuery createProxyAQueryInstance(Context ctx) {
        final AQuery aq = new AQuery(ctx);
        try {
            lg.trace("Creating aquery proxy instance");
            SharedPreferences sharedPrefs = getDefaultSharedPreferences(ctx);
            boolean enabled = isProxyEnabled(ctx);
            lg.trace("Proxy is enabled: {}", enabled);
            if (!enabled) {
                return aq;
            }

            String httpProxyHost = sharedPrefs.getString(UtilConstants.PREF_HTTP_PROXY_HOST_KEY,
                    UtilConstants.DEF_HTTP_PROXY_HOST);

            String httpProxyPort = sharedPrefs.getString(UtilConstants.PREF_HTTP_PROXY_PORT_KEY,
                    UtilConstants.DEF_HTTP_PROXY_PORT);

            lg.trace("Proxy is enabled, host: {}, port: {}", httpProxyHost, httpProxyPort);


            if (httpProxyHost == null) {
                return aq;
            }

            lg.trace("Proxy host: {}, proxy port: {}",
                httpProxyHost, httpProxyPort);
            aq.proxy(httpProxyHost, Integer.parseInt(httpProxyPort));
        } catch (NumberFormatException e) {
            lg.warn("Invalid proxy auth number format", e);
        }

        return aq;
    }

    public static void setGlobalProxyAuth(Context ctx) {
        lg.trace("Setting proxy auth");
        if (isProxyEnabled(ctx)) {
            lg.trace("Setting custom proxy auth");
            SharedPreferences sharedPrefs = getDefaultSharedPreferences(ctx);
            final String httpProxyUser = sharedPrefs.getString(UtilConstants.PREF_HTTP_PROXY_USER_KEY,
                    UtilConstants.DEF_HTTP_PROXY_USER);

            final String httpProxyPass = sharedPrefs.getString(UtilConstants.PREF_HTTP_PROXY_PASSWORD_KEY,
                    UtilConstants.DEF_HTTP_PROXY_PASSWORD);

            setGlobalProxyAuth(httpProxyUser, httpProxyPass);
        } else {
            Authenticator.setDefault(null);
            lg.trace("Setting empty proxy auth");
        }
    }

    private static SharedPreferences getDefaultSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private static void setGlobalProxyAuth(final String httpProxyUser, final String httpProxyPass) {
        lg.trace("Proxy user: {}, proxy pass {}", httpProxyUser);
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                lg.trace("Proxy auth try");
                return new PasswordAuthentication(httpProxyUser,httpProxyPass.toCharArray());
            }
        });
    }

    public static boolean isProxyEnabled(Context ctx) {
        SharedPreferences sharedPrefs =  getDefaultSharedPreferences(ctx);
        return sharedPrefs.getBoolean(
                        UtilConstants.PREF_HTTP_PROXY_ENABLED_KEY,
                        UtilConstants.DEF_HTTP_PROXY_ENABLED);
    }

    static LocationManager getSystemService(Context ctx) {
        return (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }
}
