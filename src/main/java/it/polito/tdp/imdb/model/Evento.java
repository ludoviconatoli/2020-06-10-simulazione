package it.polito.tdp.imdb.model;

import java.time.LocalDate;
import java.util.Date;

public class Evento {
	
	enum EventType {
		INTERVISTA
	}
	
	private LocalDate giorno;
	private EventType type;
	private Actor intervistato;
	
	public Evento(LocalDate giorno, EventType type, Actor intervistato) {
		super();
		this.giorno = giorno;
		this.type = type;
		this.intervistato = intervistato;
	}

	public LocalDate getGiorno() {
		return giorno;
	}

	public void setGiorno(LocalDate giorno) {
		this.giorno = giorno;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Actor getIntervistato() {
		return intervistato;
	}

	public void setIntervistato(Actor intervistato) {
		this.intervistato = intervistato;
	}
	

}
