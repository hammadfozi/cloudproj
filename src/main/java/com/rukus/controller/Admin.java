package com.rukus.controller;

import com.rukus.model.Booking;
import com.rukus.model.Room;
import com.rukus.model.User;
import com.rukus.model.UserProfile;
import com.rukus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

import static com.rukus.service.CloudinaryService.deleteRoomImages;
import static com.rukus.service.CloudinaryService.saveImage;

@Controller
@SessionAttributes("roles")
public class Admin {

    private final UserService userService;
    private final RoomService roomService;
    private final RoomImageService roomImagesService;
    private final BookingService bookingService;
    private final UserProfileService userProfileService;

    @Autowired
    public Admin(@Lazy UserService userService, RoomService roomService, RoomImageService roomImagesService, @Lazy BookingService bookingService, UserProfileService userProfileService) {
        this.userService = userService;
        this.roomService = roomService;
        this.roomImagesService = roomImagesService;
        this.bookingService = bookingService;
        this.userProfileService = userProfileService;
    }

    @RequestMapping(value = "/admin")
    public String adminHome(ModelMap model) {
        List<User> customers = userService.findAllCustomers();
        List<User> admins = userService.findAllAdmins();
        List<Room> rooms = roomService.findAllRooms();
        List<Booking> bookings = bookingService.findAllBookings();
        List<Booking> pendingbookings = bookingService.findPendingBookings();
        List<Booking> confirmedbookings = bookingService.findConfirmedBookings();
        List<Room> freerooms = roomService.findFreeRooms();

        // Users
        model.addAttribute("username", getPrincipal());
        model.addAttribute("customers", customers);
        model.addAttribute("totalcustomers", customers.size());
        model.addAttribute("admins", admins);
        model.addAttribute("totaladmins", admins.size());

        // Rooms
        model.addAttribute("rooms", rooms);
        model.addAttribute("freerooms", freerooms);
        model.addAttribute("totalfreerooms", freerooms.size());
        model.addAttribute("totalrooms", rooms.size());

        // Bookings
        model.addAttribute("bookings", bookings);
        model.addAttribute("totalbookings", bookings.size());
        model.addAttribute("pendingbookings", pendingbookings);
        model.addAttribute("confirmedbookings", confirmedbookings);
        model.addAttribute("totalpendingbookings", pendingbookings.size());
        model.addAttribute("totalconfirmedbookings", confirmedbookings.size());
        model.addAttribute("completedbookings", bookingService.findCompletedBookings());

        return "admin";
    }


    /**
     * This method will provide the medium to add a new user.
     */
    @RequestMapping(value = "/admin/new/user", method = RequestMethod.GET)
    public String addUser(ModelMap model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("edit", false);
        model.addAttribute("loggedinuser", getPrincipal());
        return "adminCreateUser";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * saving user in database. It also validates the user input
     */
    @RequestMapping(value = "/admin/new/user", method = RequestMethod.POST)
    public String addUserProcess(@ModelAttribute User user, BindingResult result,
                                 RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            return "adminCreateUser";
        }

        if (!userService.isUserEmailUnique(user.getId(), user.getEmail())) {
            FieldError emailError = new FieldError("user", "email", "Email is not unique");
            result.addError(emailError);
            return "adminCreateUser";
        }

        userService.saveUser(user);

        redirectAttrs.addFlashAttribute("success", "User " + user.getFirstName() + " " + user.getLastName() + " was created successfully");
        return "redirect:/admin";
    }

    /**
     * This method will provide the medium to update an existing user.
     */
    @RequestMapping(value = {"admin/user/edit-{email}"}, method = RequestMethod.GET)
    public String editUser(@PathVariable String email, ModelMap model, RedirectAttributes redirectAttrs) {

        User user = userService.findByEmail(email);
        if (user == null) {
            redirectAttrs.addFlashAttribute("success", "Request user: " + email + " does not exist in database.");
            return "redirect:/manage";
        }
        user.setPassword("");

        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        model.addAttribute("loggedinuser", getPrincipal());
        return "editUser";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * updating user in database. It also validates the user input
     *
     * @link /admin/user/edit/delete-myname
     */
    @RequestMapping(value = {"admin/user/edit-{email}"}, method = RequestMethod.POST)
    public String updateUser(@ModelAttribute User user, BindingResult result,
                             @PathVariable String email, RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            return "editUser";
        }

        if (!userService.isUserEmailUnique(user.getId(), user.getEmail())) {
            FieldError emailError = new FieldError("user", "email", "Email is not unique");
            result.addError(emailError);
            return "editUser";
        }
        User u = userService.findByEmail(email);
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setUserProfiles(user.getUserProfiles());
        u.setEmail(user.getEmail());
        userService.updateUser(u);
        redirectAttrs.addFlashAttribute("success", "User: " + email + " was updated successfully");
        return "redirect:/admin";
    }

    /**
     * This method will delete user by it's Username
     *
     * @link /admin/user/delete-myname
     */
    @RequestMapping(value = {"admin/user/delete-{email}"}, method = RequestMethod.GET)
    public String deleteUser(@PathVariable String email, RedirectAttributes redirectAttrs) {

        if (userService.findByEmail(email) == null) {
            redirectAttrs.addFlashAttribute("success", "Requested user: " + email + " does not exist in database.");
            return "redirect:/admin";
        }

        userService.deleteByEmail(email);
        redirectAttrs.addFlashAttribute("success", "User " + email + " was deleted successfully");
        return "redirect:/admin";
    }


    /**
     * This method will provide the medium to add a new room.
     */
    @RequestMapping(value = "/admin/new/room", method = RequestMethod.GET)
    public String addRoom(ModelMap model) {
        Room room = new Room();
        model.addAttribute("room", room);
        model.addAttribute("edit", false);
        return "adminAddRoom";
    }


    /**
     * This method will be called on form submission, handling POST request for
     * saving room in database.
     */
    @RequestMapping(value = "/admin/new/room", method = RequestMethod.POST)
    public String addRoomProcess(@ModelAttribute Room room, BindingResult result, RedirectAttributes redirectAttrs,
                                 MultipartHttpServletRequest request) throws IOException {

        if (result.hasErrors()) {
            return "adminAddRoom";
        }

        if (!roomService.isRoomNameUnique(room.getId(), room.getName())) {
            FieldError nameError = new FieldError("room", "name", "Room name already exists");
            result.addError(nameError);
            return "adminAddRoom";
        }

        roomService.saveRoom(room);
        saveImage(room, request.getFiles("pictures"), roomImagesService);

        redirectAttrs.addFlashAttribute("success", "Room " + room.getName() + " was added successfully");
        return "redirect:/admin";
    }

    /**
     * @param name - name of room
     * @link /admin/new/room/check?name=roomName
     */
    @RequestMapping(value = "/admin/room/check")
    public
    @ResponseBody
    String isRoomAvailable(@RequestParam("name") String name) {
        if (roomService.isRoomNameUnique(null, name)) return "Available";
        return "Not Available";
    }

    @RequestMapping(value = "/admin/room/edit-{id}", method = RequestMethod.GET)
    public String editRoom(@PathVariable Integer id, ModelMap model) {

        if (roomService.findById(id) == null)
            return "redirect:/admin";

        Room room = roomService.findById(id);
        model.addAttribute("room", room);
        model.addAttribute("edit", true);
        return "editRoom";
    }

    /**
     * Request handler for saving the updated
     *
     * @link /admin/room/edit-xxxx
     */
    @RequestMapping(value = "/admin/room/edit-{id}", method = RequestMethod.POST)
    public String updateRoom(@ModelAttribute Room room, BindingResult result,
                             @PathVariable Integer id,
                             MultipartHttpServletRequest request, RedirectAttributes redirectAttrs) throws IOException {

        if (result.hasErrors()) {
            return "editRoom";
        }

        if (!roomService.isRoomNameUnique(room.getId(), room.getName())) {
            FieldError nameError = new FieldError("room", "name", "Room name ");
            result.addError(nameError);
            return "editRoom";
        }

        Room r = roomService.findById(id);
        r.setName(room.getName());
        r.setBooking(room.getBooking());
        r.setCapacity(room.getCapacity());
        r.setBath(room.getBath());
        r.setBed(room.getBed());
        r.setPrice(room.getPrice());

        // Image attachments
        List<MultipartFile> images = request.getFiles("pictures");
        if (images.size() != 0 && !(images.size() == 1 && images.get(0).getSize() == 0)) {
            if (r.getImages().size() != 0) deleteRoomImages(room, roomImagesService);
            saveImage(room, images, roomImagesService);
            r.setImages(room.getImages());
        }
        roomService.updateRoom(r);

        redirectAttrs.addFlashAttribute("success", "Room " + room.getName() + " was updated successfully");
        return "redirect:/admin";
    }

    /**
     * Deletes the room and redirects url with appropriate message
     *
     * @return redirect url
     * @link /admin/room/delete-xxxx
     */
    @RequestMapping(value = "/admin/room/delete-{id}", method = RequestMethod.GET)
    public String deleteRoom(@PathVariable Integer id, RedirectAttributes redirectAttrs) {

        if (roomService.findById(id) == null) {
            redirectAttrs.addFlashAttribute("success", "Room with Id " + id + "does not exit.");
            return "redirect:/admin";
        }

        roomImagesService.deleteByRoomId(id);
        roomService.deleteRoomById(id);
        redirectAttrs.addFlashAttribute("success", "Room No " + id + " was removed successfully.");
        return "redirect:/admin";
    }

    /**
     * This method returns the principal[user-name] of logged-in user.
     */
    private String getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return App.getCurrentUserName(principal);
    }

    /**
     * This method will provide user roles to all views
     */
    @ModelAttribute("roles")
    public List<UserProfile> initializeProfiles() {
        return userProfileService.findAll();
    }
}