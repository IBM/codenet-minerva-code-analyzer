package com.acme.modres.mbean.reservation;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.acme.modres.Constants;

public class DateChecker implements Runnable {
    ReservationCheckerData data;

    public DateChecker(ReservationCheckerData data) {
        this.data = data;
    }

    public void run() {
        data.setAvailablility(true);
		for(Reservation resveration: data.getReservationList().getReservations()) {
			try {
                Date selectedDate = data.getSelectedDate();
				Date fromDate = new SimpleDateFormat(Constants.DATA_FORMAT).parse(resveration.getFromDate());
				Date toDate = new SimpleDateFormat(Constants.DATA_FORMAT).parse(resveration.getToDate());

				if (selectedDate.after(fromDate) && selectedDate.before(toDate)) {
                    data.setAvailablility(false);
                    break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
