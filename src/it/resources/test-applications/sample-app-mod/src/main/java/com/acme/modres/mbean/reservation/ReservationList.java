package com.acme.modres.mbean.reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationList {
    
    private List<Reservation> reservations = new ArrayList<>();

	public ReservationList() {

	}

	public ReservationList(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	
	public void add(Reservation reservation) {
		reservations.add(reservation);
	}
    
	public List<Reservation> getReservations() {
		return reservations;
	}
}
