package com.example.hotelapp.Objetos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Esta clase proporciona una funcionalidad para encriptar contraseñas.
 */
public class PasswordEncryptor {

    /**
     * Este método se utiliza para encriptar una contraseña.
     * Utiliza el algoritmo SHA-256 para la encriptación.
     * @param password La contraseña que se va a encriptar.
     * @return La contraseña encriptada en formato de cadena hexadecimal, o null si ocurre una excepción.
     */
    public static String encrypt(String password) {
        MessageDigest digest;
        try {
            // Intenta obtener una instancia del algoritmo SHA-256
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // Imprime la traza de la pila y devuelve null si el algoritmo no está disponible
            e.printStackTrace();
            return null;
        }
        // Realiza la encriptación y obtiene el hash codificado
        byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            // Añade un cero al principio si la longitud de la cadena hexadecimal es 1
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        // Devuelve la contraseña encriptada en formato de cadena hexadecimal
        return hexString.toString();
    }
}