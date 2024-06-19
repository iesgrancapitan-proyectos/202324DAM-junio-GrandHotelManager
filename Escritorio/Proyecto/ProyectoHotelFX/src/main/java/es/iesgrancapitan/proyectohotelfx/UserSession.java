package es.iesgrancapitan.proyectohotelfx;

/**
 * Clase UserSession
 */
public class UserSession {
    private static String username;
    private static int userType;
    private static String token;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static int getUserType() {
        return userType;
    }

    public static void setUserType(int userType) {
        UserSession.userType = userType;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        UserSession.token = token;
    }
}