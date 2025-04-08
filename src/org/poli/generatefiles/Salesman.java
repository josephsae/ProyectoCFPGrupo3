package org.poli.generatefiles;

import java.io.Serializable;

/**
 * Representa un vendedor con tipo de documento de identificación, número de
 * identificación y nombre completo. Esta clase implementa {@link Serializable}
 * para permitir la serialización de objetos.
 */
public class Salesman implements Serializable {

    /**
     * UID de versión para garantizar consistencia durante la
     * serialización/deserialización
     */
    private static final long serialVersionUID = 1L;

    private String documentType;
    private long id;
    private String name;

    /**
     * Construye un nuevo vendedor con sus datos básicos
     *
     * @param documentType Tipo de documento de identificación (ej: "CC", "NIT",
     * "Pasaporte")
     * @param id Número único de identificación (debe ser un valor positivo)
     * @param name Nombre completo del vendedor (no debe ser nulo o vacío)
     */
    public Salesman(String documentType, long id, String name) {
        this.documentType = documentType;
        this.id = id;
        this.name = name;
    }

    /**
     * Obtiene el tipo de documento de identificación del vendedor
     *
     * @return Cadena con el tipo de documento (ej: "CC", "TI")
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Obtiene el número de identificación único del vendedor
     *
     * @return Número de identificación como valor largo (long)
     */
    public long getId() {
        return id;
    }

    /**
     * Obtiene el nombre completo del vendedor
     *
     * @return Nombre completo en formato de cadena
     */
    public String getName() {
        return name;
    }

    /**
     * Proporciona una representación en cadena del vendedor en el formato:
     * "[Tipo Documento] [ID] - [Nombre]"
     *
     * @return Cadena formateada con los datos básicos del vendedor
     */
    @Override
    public String toString() {
        return documentType + " " + id + " - " + name;
    }
}
