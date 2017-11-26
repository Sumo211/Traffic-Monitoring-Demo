package com.leon.processor.util;

public class GeoDistanceCalculator {

    private GeoDistanceCalculator() {
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; //Earth radius in km

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lngDistance = Math.toRadians(lng2 - lng1);
        Double delta = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        return r * (2 * Math.atan2(Math.sqrt(delta), Math.sqrt(1 - delta)));
    }

    public static boolean isInPOIRadius(double currentLat, double currentLng, double poiLat, double poiLng, double radius) {
        return getDistance(currentLat, currentLng, poiLat, poiLng) <= radius;
    }

}
