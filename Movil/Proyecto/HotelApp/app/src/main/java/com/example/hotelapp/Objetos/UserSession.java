package com.example.hotelapp.Objetos;

/**
 * Esta clase representa una sesión de usuario en la aplicación.
 * Contiene campos estáticos para el nombre de usuario, tipo de usuario y token.
 * Los campos estáticos permiten que los datos de la sesión del usuario estén disponibles globalmente en la aplicación.
 */
public class UserSession {
    private static String username; // El nombre de usuario de la sesión
    private static int userType; // El tipo de usuario de la sesión
    private static String token; // El token de la sesión

    /**
     * Método getter para el nombre de usuario de la sesión.
     * @return El nombre de usuario de la sesión.
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Método setter para el nombre de usuario de la sesión.
     * @param username El nuevo nombre de usuario de la sesión.
     */
    public static void setUsername(String username) {
        UserSession.username = username;
    }

    /**
     * Método getter para el tipo de usuario de la sesión.
     * @return El tipo de usuario de la sesión.
     */
    public static int getUserType() {
        return userType;
    }

    /**
     * Método setter para el tipo de usuario de la sesión.
     * @param userType El nuevo tipo de usuario de la sesión.
     */
    public static void setUserType(int userType) {
        UserSession.userType = userType;
    }

    /**
     * Método getter para el token de la sesión.
     * @return El token de la sesión.
     */
    public static String getToken() {
        return token;
    }

    /**
     * Método setter para el token de la sesión.
     * @param token El nuevo token de la sesión.
     */
    public static void setToken(String token) {
        UserSession.token = token;
    }
}