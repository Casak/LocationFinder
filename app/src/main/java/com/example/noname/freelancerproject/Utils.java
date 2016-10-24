package com.example.noname.freelancerproject;

public class Utils {

    public static boolean isInRadius(double lat1, double long1, double lat2, double long2, int radius) {
        double earthRadius = 6371.0088d;
        double lat1Rad = degreesToRadians(lat1);
        double lat2Rad = degreesToRadians(lat2);
        double long1Rad = degreesToRadians(long1);
        double long2Rad = degreesToRadians(long2);
        double distance = Math.acos(
                Math.sin(lat1Rad) * Math.sin(lat2Rad) +
                        Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(long1Rad - long2Rad)
        ) * earthRadius * 1000;
        return distance <= radius;
    }

    private static double degreesToRadians(double degrees) {
        return (degrees * Math.PI) / 180;
    }
}
