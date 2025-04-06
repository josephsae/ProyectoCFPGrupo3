package org.poli.generatefiles;

import java.io.Serializable;

/**
 * Representa un producto con ID, nombre y precio.
 */
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private int price;

	public Product(String id, String name, int price) {
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return id + " - " + name + ": $" + price;
	}
}
