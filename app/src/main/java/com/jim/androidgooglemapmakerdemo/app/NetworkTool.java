package com.jim.androidgooglemapmakerdemo.app;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by easyapp_jim on 15/3/19.
 */
public class NetworkTool {
    public static final String SUCCESS_CODE = "status";

    private Context mContext;
    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=24.144,120.675&language=zh-TW&radius=200&sensor=false&key=AIzaSyC8qLjFTmMo0etL1tVO3OdLBrEKx41Qko0
    private String baseUrl = "";

    private int limit = 25;
    private int timeout = 1000 * 30;
    private static AsyncHttpClient asyncHttpClient;
    private RequestHandle handle;

    static {
        // setup asynchronous client
        asyncHttpClient = new AsyncHttpClient();
    }


    public NetworkTool(Context context) {
        mContext = context;

    }

    public void CancelRequest() {
        asyncHttpClient.cancelRequests(mContext, false);
        if (handle != null) {
            handle.cancel(true);
        }
    }

    public void GetNear(String latlng, ResponseHandler responseHandler) {
        String route = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latlng + "&language=zh-TW&radius=200&sensor=false&key=AIzaSyC8qLjFTmMo0etL1tVO3OdLBrEKx41Qko0";
        get(route, responseHandler);
    }


    public void GetRandomChinese(int limit, int n, ResponseHandler responseHandler) {
        String route = "http://more.handlino.com/sentences.json?limit=" + limit + "&n=" + n;
        get(route, responseHandler);
    }

    public void GetTestProd(ResponseHandler responseHandler) {
        String route = "https://dl.dropboxusercontent.com/u/173576968/easyapptestapi/fiifapi.html";
        get(route, responseHandler);
    }

    private void get(String route, RequestParams params, ResponseHandler responseHandler) {
        if (!route.startsWith("http"))
            route = baseUrl + route;

        Log.d("route", route);
        handle = asyncHttpClient.get(mContext, route, params, DefaultjsonHttpResponseHandler(responseHandler));
    }

    private void get(String route, ResponseHandler responseHandler) {
        if (!route.startsWith("http"))
            route = baseUrl + route;

        Log.d("route", route);
        handle = asyncHttpClient.get(mContext, route, DefaultjsonHttpResponseHandler(responseHandler));
    }

    private void post(String route, RequestParams params, ResponseHandler responseHandler) {
        post(route, params, false, responseHandler);
    }

    private void post(String route, RequestParams params, boolean isLogin, ResponseHandler responseHandler) {
        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger.d(params.toString());
        handle = asyncHttpClient.post(mContext, route, params, DefaultjsonHttpResponseHandler(responseHandler));
    }


    private JsonHttpResponseHandler DefaultjsonHttpResponseHandler(final ResponseHandler responseHandler) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Logger.d(response.toString());
                responseHandler.Success(statusCode, response);
            }

            @Override
            protected Object parseResponse(byte[] responseBody) throws JSONException {
//                Logger.d(responseBody.toString());
                return super.parseResponse(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mContext, "更新失敗請檢查網路", Toast.LENGTH_SHORT).show();
                if (null != errorResponse) {
                    responseHandler.Fail(statusCode, errorResponse.toString());
                } else {
                    responseHandler.Fail(statusCode, "");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(mContext, "更新失敗請檢查網路", Toast.LENGTH_SHORT).show();
                responseHandler.Fail(statusCode, "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(mContext, "更新失敗請檢查網路", Toast.LENGTH_SHORT).show();
                responseHandler.Fail(statusCode, responseString);
            }
        };
        return jsonHttpResponseHandler;
    }

    public ResponseHandler DefaultResponseHandler() {
        return new ResponseHandler() {
            @Override
            public void Success(int StatusCode, JSONObject response) {
                Logger.d(response.toString());
            }

            @Override
            public void Fail(int status, String reason) {

            }
        };
    }

    public interface ResponseHandler {
        void Success(int StatusCode, JSONObject response);

        void Fail(int status, String reason);
    }

}
