package com.rukus.controller;

import com.rukus.Constant;
import com.rukus.model.Booking;
import com.rukus.model.Room;
import com.rukus.model.User;
import com.rukus.service.BookingService;
import com.rukus.service.RoomService;
import com.rukus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@SessionAttributes("roles")
public class Hotel {

    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final AuthenticationTrustResolver authenticationTrustResolver;

    @Autowired
    public Hotel(UserService userService, RoomService roomService, @Lazy BookingService bookingService, AuthenticationTrustResolver authenticationTrustResolver) {
        this.userService = userService;
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    /**
     * Default date format to be used in app
     * binding to website data handlers
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
    }

    /**
     * Returns all rooms to be displayed on page
     *
     * @link /rooms
     */
    @RequestMapping(value = "/rooms", method = RequestMethod.GET)
    public String rooms(ModelMap model) {
        model.addAttribute("rooms", roomService.findAllRooms());
        model.addAttribute("search", false);
        return "rooms";
    }

    /**
     * This method handles the Booking
     */
    @RequestMapping(value = "/booking", method = RequestMethod.GET)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    public String book(ModelMap model) {
        if (isAnonymous()) {
            return "redirect:/login";
        } else {
            Booking booking = new Booking();
            model.addAttribute("booking", booking);
            model.addAttribute("user", getCurrentUser());
            model.addAttribute("loggedinuser", getPrincipal());
            model.addAttribute("rooms", roomService.findFreeRooms());
            model.addAttribute("edit", false);
            return "booking";
        }
    }

    /**
     * @param id room id to be booked
     * @Temporal(DATE) important for using defined date databinding
     */
    @RequestMapping(value = "/booking-{id}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    public String bookRoomWithId(@PathVariable Integer id, ModelMap model) {
        if (isAnonymous()) {
            return "redirect:/login";
        } else {
            Room room = roomService.findById(id);

            if (room.getBooking() != null || roomService.findById(id) == null)
                return "redirect:/rooms";

            Booking booking = new Booking();
            booking.setRoom(room);
            model.addAttribute("booking", booking);
            model.addAttribute("user", getCurrentUser());
            model.addAttribute("loggedinuser", getPrincipal());
            model.addAttribute("rooms", roomService.findFreeRooms());
            model.addAttribute("edit", false);
            return "booking";
        }
    }

    @RequestMapping(value = "/booking", method = RequestMethod.POST)
    public String bookRoom(@ModelAttribute Booking booking, BindingResult result,
                           ModelMap model) {

        if (result.hasErrors()) {
            model.addAttribute("user", getCurrentUser());
            model.addAttribute("loggedinuser", getPrincipal());
            model.addAttribute("rooms", roomService.findFreeRooms());
            model.addAttribute("edit", false);
            return "booking";
        }

        if (booking.getUser() == null) booking.setUser(getCurrentUser());

        // awaiting confirmation from manager
        booking.setStatus(Constant.BOOKING_STATUS.PENDING);
        bookingService.saveBooking(booking);

        model.addAttribute("success", "Booking ID " + booking.getId() + " created successfully");
        model.addAttribute("bookingsuccess", booking.getId());
        return "success";
    }

    @RequestMapping(value = "/admin/bookings/edit-{id}", method = RequestMethod.GET)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    public String booking(ModelMap model, @PathVariable Integer id, RedirectAttributes redirectAttrs) {
        if (isAnonymous()) {
            return "redirect:/login";
        } else {

            if (bookingService.findById(id) == null) {
                redirectAttrs.addFlashAttribute("success", "There is no booking with Id " + id + " in system.");
                return "redirect:/admin";
            }

            Booking booking = bookingService.findById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("user", getCurrentUser());
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("rooms", roomService.findAllRooms());
            model.addAttribute("loggedinuser", getPrincipal());
            model.addAttribute("edit", true);
            return "editBooking";
        }
    }

    @RequestMapping(value = "/admin/bookings/edit-{id}", method = RequestMethod.POST)
    public String updateBooking(@ModelAttribute Booking booking, BindingResult result,
                                ModelMap model, @PathVariable Integer id, RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            model.addAttribute("booking", booking);
            model.addAttribute("user", getCurrentUser());
            model.addAttribute("users", userService.findAllUsers());
            model.addAttribute("rooms", roomService.findFreeRooms());
            model.addAttribute("loggedinuser", getPrincipal());
            model.addAttribute("edit", true);
            return "editBooking";
        }

        // Error handling
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        Date currentTime = null;
        try {
            currentTime = dateFormat.parse(dateFormat.format(cal.getTime()));
        } catch (ParseException p) {
            p.printStackTrace();
        }
        if (currentTime != null) {
            if (booking.getArrivalTime().before(currentTime)) {
                FieldError dateError = new FieldError("booking", "arrivalTime",
                        "Arrival Time can not be in the past");
                result.addError(dateError);
                model.addAttribute("booking", booking);
                model.addAttribute("user", getCurrentUser());
                model.addAttribute("users", userService.findAllUsers());
                model.addAttribute("rooms", roomService.findFreeRooms());
                model.addAttribute("loggedinuser", getPrincipal());
                model.addAttribute("edit", true);
                return "editBooking";
            } else if (booking.getDepartureTime().before(currentTime)) {
                FieldError dateError = new FieldError("booking", "departureTime",
                        "Departure Time can not be in the past.");
                result.addError(dateError);
                model.addAttribute("booking", booking);
                model.addAttribute("user", getCurrentUser());
                model.addAttribute("users", userService.findAllUsers());
                model.addAttribute("rooms", roomService.findFreeRooms());
                model.addAttribute("loggedinuser", getPrincipal());
                model.addAttribute("edit", true);
                return "editBooking";
            }
        }

        bookingService.updateBooking(booking);
        redirectAttrs.addFlashAttribute("success", "Booking No " + id + " was updated successfully");
        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/bookings/delete-{id}")
    public String deleteBooking(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        if (bookingService.findById(id) == null) {
            redirectAttrs.addFlashAttribute("success", "Booking No " + id + " does not exist in database.");
            return "redirect:/admin";
        }

        if (bookingService.findById(id).getStatus().equals(Constant.BOOKING_STATUS.COMPLETED)) {
            redirectAttrs.addFlashAttribute("success", "Booking No " + id + " has been completed, it can't be removed from database.");
            return "redirect:/admin";
        }

        bookingService.deleteBookingById(id);
        redirectAttrs.addFlashAttribute("success", "Booking No " + id + " was deleted successfully");
        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/bookings/confirm-{id}")
    public String confirmBooking(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        Booking booking = bookingService.findById(id);

        if (booking == null) {
            redirectAttrs.addFlashAttribute("success", "Booking No " + id + " does not exist in database.");
            return "redirect:/admin";
        }

        if (booking.getStatus().equals(Constant.BOOKING_STATUS.COMPLETED)) {
            redirectAttrs.addFlashAttribute("success", "Booking No " + booking + " has been completed, it can't be set to CONFIRMED.");
            return "redirect:/admin";
        }
        booking.setStatus(Constant.BOOKING_STATUS.CONFIRMED);
        bookingService.updateBooking(booking);
        redirectAttrs.addFlashAttribute("success", "Booking No " + id + " was set to confirmed");
        return "redirect:/admin";
    }

    @RequestMapping(value = "/admin/bookings/complete-{id}")
    public String completeBooking(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        Booking booking = bookingService.findById(id);

        if (booking == null) {
            redirectAttrs.addFlashAttribute("success", "Booking No " + id + " does not exist in database.");
            return "redirect:/admin";
        }

        booking.setRoomBooked(booking.getRoom().getName());
        booking.setStatus(Constant.BOOKING_STATUS.COMPLETED);
        booking.setRoom(null);
        bookingService.updateBooking(booking);
        redirectAttrs.addFlashAttribute("success", "Booking No " + id + " was set to completed");
        return "redirect:/admin";
    }

    private String getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return App.getCurrentUserName(principal);
    }

    private User getCurrentUser() {
        return userService.findByEmail(getPrincipal());
    }

    /**
     * This method returns true if users is already authenticated [logged-in]
     */
    private boolean isAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }
}