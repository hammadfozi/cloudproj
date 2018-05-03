package com.rukus.controller;

import com.rukus.Constant;
import com.rukus.model.User;
import com.rukus.model.UserProfile;
import com.rukus.service.BookingService;
import com.rukus.service.UserProfileService;
import com.rukus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes("roles")
public class App {

    private final UserService userService;
    private final UserProfileService userProfileService;
    private final BookingService bookingService;
    private final AuthenticationTrustResolver authenticationTrustResolver;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public App(UserService userService, UserProfileService userProfileService, BookingService bookingService, AuthenticationTrustResolver authenticationTrustResolver, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.bookingService = bookingService;
        this.authenticationTrustResolver = authenticationTrustResolver;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @link HOME PAGE
     */
    @RequestMapping(value = {"/", "/home"})
    public String home() {
        return "index";
    }

    /**
     * @param email - optional
     * @link /register/available?username=username&email=email
     */
    @RequestMapping(value = "/user/availability")
    public
    @ResponseBody
    String checkUserAvailability(@RequestParam(value = "email", defaultValue = "") String email) {
        User user = null;
        if (email != null && !email.equals("")) user = userService.findByEmail(email);
        if (user == null) return "Available";
        else return "Not Available";
    }


    // ERRORS

    /**
     * This method handles Access-Denied redirect.
     * For unauthorized
     *
     * @see com.rukus.security.Security for configuring access to links
     */
    @RequestMapping(value = "/errors/Access_Denied", method = RequestMethod.GET)
    public String accessDeniedPage(ModelMap model) {
        model.addAttribute("loggedinuser", getPrincipal());
        return "accessDenied";
    }

    @RequestMapping(value = {"/errors/404"})
    public String pageNotFound() {
        return "404";
    }

    /**
     * This method handles login GET requests.
     * If users is already logged-in and tries to goto login page again, will be redirected to list page.
     *
     * @see com.rukus.security.Security for detailed login properties
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        if (isAnonymous()) {
            return "login";
        } else {
            return "redirect:/";
        }
    }

    /**
     * Register Page
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String newUser(ModelMap model) {
        if (isAnonymous()) {
            User user = new User();
            model.addAttribute("user", user);
            model.addAttribute("edit", false);
            model.addAttribute("loggedinuser", getPrincipal());
            return "register";
        } else {
            return "redirect:/";
        }
    }

    /**
     * Validates & Register New Customer
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveUser(@ModelAttribute User user, BindingResult result,
                           HttpServletRequest request, ModelMap model) {

        if (result.hasErrors()) {
            return "register";
        }

        if (!userService.isUserEmailUnique(user.getId(), user.getEmail())) {
            FieldError emailError = new FieldError("user", "email", "Email already exists");
            result.addError(emailError);
            return "register";
        }

        try {
            com.rukus.model.UserProfile role = userProfileService.findById(Constant.USER_ROLE.VERIFIED);
            Set<com.rukus.model.UserProfile> userProfile = new HashSet<>();
            userProfile.add(role);
            user.setUserProfiles(userProfile);
            userService.saveUser(user);

            model.addAttribute("success", "User " + user.getFirstName() + " " + user.getLastName() + " registered successfully");
            model.addAttribute("loggedinuser", getPrincipal());

            //Requesting LOGIN
            request.login(user.getEmail(), user.getPassword());
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return "success";
    }

    @RequestMapping(value = "/user/profile", method = RequestMethod.GET)
    public String profile(ModelMap model) {
        if (isAnonymous()) {
            return "login";
        } else {
            User user = getCurrentUser();
            model.addAttribute("username", getPrincipal());
            model.addAttribute("user", user);
            model.addAttribute("totalbookings", user.getBookings().size());
            return "profile";
        }
    }

    /**
     * Update profile settings request handler
     */
    @RequestMapping(value = "/user/profile", method = RequestMethod.POST)
    public String updateProfile(@ModelAttribute User user, BindingResult result,
                                ModelMap model) {
        if (isAnonymous()) {

            // take to login if not authenticated yet
            return "login";
        } else {
            if (!userService.isUserEmailUnique(user.getId(), user.getEmail())) {
                FieldError emailError = new FieldError("user", "email", "Email already exists");
                result.addError(emailError);
                return "profile";
            }

            User n = getCurrentUser();
            n.setFirstName(user.getFirstName());
            n.setLastName(user.getLastName());
            n.setEmail(user.getEmail());
            userService.updateUser(n);
            updateCurrentUser(n);

            model.addAttribute("success", "Your profile was updated successfully");
            return "profile";
        }
    }

    @RequestMapping(value = "/user/profile-loggedin+edit", method = RequestMethod.POST)
    public String updatePassword(@RequestParam("newpassword") String password, RedirectAttributes redirectAttributes) {
        User n = getCurrentUser();
        n.setPassword(passwordEncoder.encode(password));
        userService.updateUser(n);
        redirectAttributes.addFlashAttribute("success", "Your password was changed successfully");
        return "redirect:/user/profile";
    }

    /**
     * Customer cancelling his booking profile
     */
    @RequestMapping(value = "/user/profile/cancel-{id}+confirmed")
    public String deleteBooking(@PathVariable int id, RedirectAttributes redirectAttributes) {

        if (bookingService.findById(id).getStatus().equals(Constant.BOOKING_STATUS.COMPLETED)) {
            redirectAttributes.addFlashAttribute("success", "Booking No " + id + " has been completed already, it can not be cancelled.");
            return "redirect:/user/profile";
        }

        Integer booking = id;
        bookingService.deleteBookingById(id);
        redirectAttributes.addFlashAttribute("success", "Your Booking (" + booking + ") was cancelled successfully");
        return "redirect:/user/profile";
    }

    @RequestMapping(value = "/user/profile/delete")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        new SecurityContextLogoutHandler().logout(request, response, auth);
        SecurityContextHolder.getContext().setAuthentication(null);

        userService.deleteByEmail(getCurrentUser().getEmail());
        return "redirect:/";
    }

    /**
     * This method handles logout requests.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {

            new SecurityContextLogoutHandler().logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return "redirect:/";
    }

    /**
     * This method returns the principal[user-name] of logged-in user.
     */
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

    /**
     * Updates the current user to new values
     *
     * @param user updated settings
     */
    private void updateCurrentUser(User user) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, getGrantedAuthorities(user));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (UserProfile userProfile : user.getUserProfiles())
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getType()));
        return authorities;
    }


    /**
     * Generic Mapping
     */
    @RequestMapping(value = "/contact")
    public String contact() {
        return "contact";
    }

    @RequestMapping(value = "/about")
    public String about() {
        return "about";
    }

    static String getCurrentUserName(Object principal) {
        String userName;
        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else if (principal instanceof User) {
            userName = ((User) principal).getEmail();
        } else {
            userName = principal.toString();
        }
        return userName;
    }
}