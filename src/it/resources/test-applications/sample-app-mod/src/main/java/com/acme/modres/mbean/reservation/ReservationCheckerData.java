package com.acme.modres.mbean.reservation;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.acme.modres.Constants;

public class ReservationCheckerData {
    private ReservationList reservations;
	private Date selectedDate;
	private Boolean available;

    public ReservationCheckerData(ReservationList reservations) {
        this.reservations = reservations;
        this.available = true;
    }

    public ReservationList getReservationList() {
        return reservations;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public boolean setSelectedDate(String dateStr) {
        try {
			selectedDate = new SimpleDateFormat(Constants.DATA_FORMAT).parse(dateStr);
		} catch (Exception e) {
			return false;
		}
        return true;
    }

    public boolean isAvailible() {
        return available;
    }

    public void setAvailablility(Boolean available) {
        this.available = available;
    }
}
