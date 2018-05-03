package com.rukus.service;

import com.rukus.model.Booking;
import com.rukus.service.rep.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service("bookingService")
@Transactional
public class BookingService {

    private final BookingRepository dao;

    @Autowired
    public BookingService(BookingRepository dao) {
        this.dao = dao;
    }

    public Booking findById(int id) {
        Optional<Booking> tmp = dao.findById(id);
        return tmp.orElse(null);
    }

    public void saveBooking(@NotNull Booking booking) {
        dao.save(booking);
    }

    public void updateBooking(@NotNull Booking booking) {
        Optional<Booking> entity = dao.findById(booking.getId());
        if (entity.isPresent()) {
            booking.setId(entity.get().getId());
            dao.save(booking);
        }
    }

    public void deleteBookingById(final int id) {
        dao.deleteById(id);
    }

    public List<Booking> findAllBookings() {
        return dao.findAll();
    }

    public List<Booking> findPendingBookings() {
        return dao.findByStatus("PENDING");
    }

    public List<Booking> findConfirmedBookings() {
        return dao.findByStatus("CONFIRMED");
    }

    public List<Booking> findCompletedBookings() {
        return dao.findByStatus("COMPLETED");
    }
}
