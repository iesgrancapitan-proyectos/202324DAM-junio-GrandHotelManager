package com.example.hotelapp.Objetos;

/**
 * La clase MenuOption representa una opción de menú en la aplicación.
 * Cada opción de menú tiene un título y un recurso de imagen asociado.
 */
public class MenuOption {
    private String title;
    private int imageResId;

    /**
     * Constructor para la clase MenuOption.
     *
     * @param title El título de la opción de menú.
     * @param imageResId El recurso de imagen asociado con la opción de menú.
     */
    public MenuOption(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    /**
     * Devuelve el título de la opción de menú.
     *
     * @return El título de la opción de menú.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Devuelve el recurso de imagen de la opción de menú.
     *
     * @return El recurso de imagen de la opción de menú.
     */
    public int getImageResId() {
        return imageResId;
    }
}