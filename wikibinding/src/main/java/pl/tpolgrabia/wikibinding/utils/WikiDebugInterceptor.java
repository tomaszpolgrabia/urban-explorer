package pl.tpolgrabia.wikibinding.utils;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * Created by tpolgrabia on 01.11.16.
 */
public class WikiDebugInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request req = chain.request();
        Log.d("XXX", "Url: " + req.url());
        okhttp3.Response response = chain.proceed(req);
        boolean successFull = response.isSuccessful();
        int code = response.code();
        String message = response.message();
        String msg = response.body().string();
        Log.d("XXX", String.format("Got wiki response. Is successfull: %d, code: %d, message: %s, msg: %s",
            successFull ? 1 : 0,
            code,
            message,
            msg));
        // now we repeat once again (because we have used the stream)
        return response.newBuilder()
            .body(ResponseBody.create(response.body().contentType(), msg))
            .build();
    }
}
