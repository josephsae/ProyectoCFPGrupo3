package org.poli.generatefiles;

import java.io.Serializable;

public class Salesman implements Serializable {
	private static final long serialVersionUID = 1L;
	private String documentType;
	private long id;
	private String fullName;

	public Salesman(String documentType, long id, String fullName) {
		this.documentType = documentType;
		this.id = id;
		this.fullName = fullName;
	}

	public String getDocumentType() { return documentType; }
	public long getId() { return id; }
	public String getFullName() { return fullName; }

	@Override
	public String toString() {
		return documentType + " - " + id + " - " + fullName;
	}
}
