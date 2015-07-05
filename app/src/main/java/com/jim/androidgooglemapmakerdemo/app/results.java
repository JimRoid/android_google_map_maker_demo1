package com.jim.androidgooglemapmakerdemo.app;

import android.location.Location;
import org.json.JSONObject;

/**
 * Created by easyapp_jim on 15/7/5.
 */
public class results {
    private JSONObject jsonObject;

    public results(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Location getLocation() {
        JSONObject geometry = jsonObject.optJSONObject("geometry");
        JSONObject location_obj = geometry.optJSONObject("location");

        Location location = new Location("");
        location.setLatitude(location_obj.optDouble("lat", 0.0));
        location.setLongitude(location_obj.optDouble("lng", 0.0));
        return location;
    }

    public String getName() {
        return jsonObject.optString("name");
    }
}
