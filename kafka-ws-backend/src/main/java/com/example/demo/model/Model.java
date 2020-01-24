package com.example.demo.model;

public class Model {

	private String message;

	public Model() {

	}

	public Model(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("Model [message=%s]", message);
	}

}
