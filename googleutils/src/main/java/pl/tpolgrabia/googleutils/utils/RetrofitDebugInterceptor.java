package pl.tpolgrabia.googleutils.utils;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by tpolgrabia on 09.10.16.
 */
class RetrofitDebugInterceptor implements Interceptor {

    private static final Logger lg = LoggerFactory.getLogger(RetrofitDebugInterceptor.class);

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        final Request req = chain.request();
        okhttp3.Response response = chain.proceed(req);
        boolean successFull = response.isSuccessful();
        int code = response.code();
        String message = response.message();
        String msg = response.body().string();
        Log.d("XXX", String.format("Got response. Is successfull: %d, code: %d, message: %s, msg: %s",
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
