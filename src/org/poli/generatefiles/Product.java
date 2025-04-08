package org.poli.generatefiles;

import java.io.Serializable;

/**
 * Representa un producto con identificador, nombre y precio. Esta clase
 * implementa {@link Serializable} para permitir la serialización de objetos.
 *
 */
public class Product implements Serializable {

    /**
     * UID de versión para consistencia en serialización/deserialización.
     */

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private int price;

    /**
     * Construye un nuevo Producto con detalles específicos.
     *
     * @param id identificador único del producto (no puede ser nulo o vacío)
     * @param name nombre del producto (no puede ser nulo o vacío)
     * @param price precio del producto en unidades enteras (debe ser un entero
     * positivo)
     */
    public Product(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Obtiene el identificador único del producto.
     *
     * @return el ID del producto como String
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el nombre del producto.
     *
     * @return el nombre del producto como String
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene el precio del producto.
     *
     * @return el precio del producto como entero
     */
    public int getPrice() {
        return price;
    }

    /**
     * Proporciona una representación en cadena del producto en el formato: "ID
     * - Nombre: $Precio"
     *
     * @return una cadena formateada con los detalles del producto
     */
    @Override
    public String toString() {
        return id + " - " + name + ": $" + price;
    }
}
