package org.poli.generatefiles;

import java.io.Serializable;

/**
 * Representa un vendedor con tipo de documento, ID y nombre completo.
 */
public class Salesman implements Serializable {

	private static final long serialVersionUID = 1L;

	private String documentType;
	private long id;
	private String name;

	public Salesman(String documentType, long id, String name) {
		this.documentType = documentType;
		this.id = id;
		this.name = name;
	}

	public String getDocumentType() {
		return documentType;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return documentType + " " + id + " - " + name;
	}
}
