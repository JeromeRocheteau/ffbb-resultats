package com.ffbb.resultats.api;

import java.net.URI;
import java.util.LinkedList;

public class Appartenances extends LinkedList<Appartenance> implements Extractable {

	private static final long serialVersionUID = 202011231500001L;

	private Organisation organisation;
	
	public Appartenances(Organisation organisation) {
		super();
		this.organisation = organisation;
	}
	
	public URI getURI() {
		return URI.create("https://resultats.ffbb.com/organisation/appartenance/" + organisation.getCode() + ".html");
	}

}
