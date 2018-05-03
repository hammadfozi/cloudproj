<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Admin Panel</title>

    <jsp:include page="includes/head.jsp"/>

    <script type="text/javascript" src="<c:url value='/static/js/room-validations.js' />"></script>

    <script>
        window.onload = onStart();

        function onStart() {
            if (window.location.hash) {
                window.location.href = "/admin";
                //load(window.location.hash.substring(2));
            }
        }

    </script>

    <script type="text/javascript">
        $(document).ready(function () {
            $('#success').delay(3000).fadeOut();
        });

        $(document).ready(function () {
            $('#sampleForm').submit(
                function (event) {
                    var firstname = $('#firstname').val();
                    var lastname = $('#lastname').val();
                    var data = 'firstname='
                        + encodeURIComponent(firstname)
                        + '&lastname='
                        + encodeURIComponent(lastname);
                    $.ajax({
                        url: $("#sampleForm").attr("action"),
                        data: data,
                        type: "GET",

                        success: function (response) {
                            alert(response);
                        },
                        error: function (xhr, status, error) {
                            alert(xhr.responseText);
                        }
                    });
                    return false;
                });
        });
    </script>
</head>
<body>
<jsp:include page="includes/staff_header.jsp"/>
<div class="container-fluid" id="main" style="max-height: 100%; overflow-y: hidden; overflow-x: hidden">
    <div class="row" style="overflow-x: hidden; overflow-y: hidden;">

        <div class="col-md-3 col-lg-2 white side-nav" id="admin_sidebar" role="navigation"
             style="min-height: 100%; position: fixed; margin-left: 0;">
            <ul class="nav md-pills pills-primary nav-pills nav-stacked" role="tablist">
                <li class="nav-item card-block">
                    <a class="nav-link grey-text active" data-toggle="tab" href="#dashboard" role="tab">Overview</a>
                </li>
                <li class="nav-item card-block">
                    <a class="nav-link grey-text" data-toggle="tab" href="#rooms" role="tab">Rooms</a>
                </li>
                <li class="nav-item card-block">
                    <a class="nav-link grey-text" data-toggle="tab" href="#pending_bookings" role="tab"> Pending
                        Bookings</a>
                </li>
                <li class="nav-item card-block">
                    <a class="nav-link grey-text" data-toggle="tab" href="#confirmed_bookings" role="tab">
                        Confirmed Bookings</a>
                </li>
                <li class="nav-item card-block">
                    <a class="nav-link grey-text" data-toggle="tab" href="#completed_bookings" role="tab">
                        Completed
                        Bookings</a>
                </li>
                <li class="nav-item card-block">
                    <a class="nav-link grey-text" data-toggle="tab" href="#admins" role="tab"> Admins</a>
                </li>
                <li class="nav-item card-block">
                    <a class="nav-link grey-text" data-toggle="tab" href="#customers" role="tab"> Customers</a>
                </li>
            </ul>
        </div>

        <div id="right_side_body" class="col-md-10 col-lg-10 main">
            <div class="tab-content vertical">

                <!--Overview-->
                <div class="tab-pane fade in active" id="dashboard" role="tabpanel">

                    <div id="success">
                        <c:if test="${success != null}">
                            <div class="alert alert-success lead">
                                    ${success}
                            </div>
                        </c:if>
                    </div>

                    <div class="row">
                        <div class="col-lg-6 col-md-6">
                            <div class="card card-inverse card-success">
                                <div class="card-block bg-success">
                                    <h4 class="text-uppercase admin-cards">PENDING BOOKINGS
                                        <span class="admin-cards-text">${totalpendingbookings}</span>
                                    </h4>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6">
                            <div class="card card-inverse card-danger">
                                <div class="card-block bg-danger">
                                    <h4 class="text-uppercase admin-cards">CONFIRMED BOOKINGS
                                        <span class="admin-cards-text">${totalconfirmedbookings}</span>
                                    </h4>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6">
                            <div class="card card-inverse card-danger">
                                <div class="card-block bg-danger">
                                    <h4 class="text-uppercase admin-cards">TOTAL BOOKINGS
                                        <span class="admin-cards-text">${totalbookings}</span>
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-6 col-md-6">
                            <div class="card card-inverse card-success">
                                <div class="card-block bg-warning">
                                    <h4 class="text-uppercase admin-cards">FREE ROOMS
                                        <span class="admin-cards-text">${totalfreerooms}</span>
                                    </h4>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6">
                            <div class="card card-inverse card-warning">
                                <div class="card-block bg-warning">
                                    <h4 class="text-uppercase admin-cards">TOTAL ROOMS
                                        <span class="admin-cards-text">${totalrooms}</span>
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-lg-6 col-md-6">
                            <div class="card card-inverse card-info">
                                <div class="card-block bg-info">
                                    <h4 class="text-uppercase admin-cards">TOTAL CUSTOMERS
                                        <span class="admin-cards-text">${totalcustomers}</span>
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-lg-12 col-md-6" style="text-align: center">
                            <a href="<c:url value="/admin/new/user" />">
                                <button id="create_user" type="button"
                                        class="btn white-text waves-button waves-light light-blue"
                                        style="font-weight: 300; width: 150px;">CREATE USER
                                </button>
                            </a>
                            <a style="margin-left: 20px" href="<c:url value="/admin/new/room" />">
                                <button id="add_room" type="button"
                                        class="btn white-text waves-button waves-light light-blue"
                                        style="font-weight: 300; width: 150px;">ADD ROOM
                                </button>
                            </a>
                        </div>
                    </div>

                </div>
                <!--/.Overview-->

                <!--Available Rooms List-->
                <div class="tab-pane fade" id="rooms" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(rooms) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>No</th>
                                        <th>Name</th>
                                        <th>Price</th>
                                        <th>Capacity</th>
                                        <th>Beds</th>
                                        <th>Bath</th>
                                        <th>Internet</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${rooms}" var="room">
                                        <tr>
                                            <td>${room.id}</td>
                                            <td>${room.name}</td>
                                            <td>$${room.price}</td>
                                            <td>${room.capacity}</td>
                                            <td>${room.bed}</td>
                                            <td>${room.bath}</td>
                                            <td>${room.internet}</td>
                                            <td class="table-actions">
                                                <a href="<c:url value='/admin/room/edit-${room.id}' />"
                                                   class="btn light-blue btn-sm table-actions-buttons">Edit</a>

                                                <a href="<c:url value='/admin/room/delete-${room.id}' />"
                                                   onclick="return confirm('Are you sure you want to remove this room?')"
                                                   class="btn material-red btn-sm table-actions-buttons">Del</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> There are currently no
                                    rooms added to system yet.</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--/.Available Rooms List-->

                <!--Pending Bookings List-->
                <div class="tab-pane fade" id="pending_bookings" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(pendingbookings) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>No</th>
                                        <th>Room</th>
                                        <th>People</th>
                                        <th>Arrival Time</th>
                                        <th>Departure Time</th>
                                        <th>Booked By</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${pendingbookings}" var="booking">
                                        <tr>
                                            <td>${booking.id}</td>
                                            <td>${booking.room.name}</td>
                                            <td>${booking.people}</td>
                                            <td>${booking.arrivalTime}</td>
                                            <td>${booking.departureTime}</td>
                                            <td>${booking.user.email}</td>
                                            <td class="table-actions-lg">
                                                <a href="<c:url value='/admin/bookings/confirm-${booking.id}' />"
                                                   onclick="return confirm('Are you sure you want to confirm this booking?')"
                                                   class="btn light-blue btn-sm table-actions-buttons">Confirm</a>

                                                <a href="<c:url value='/admin/bookings/edit-${booking.id}' />"
                                                   class="btn light-blue btn-sm table-actions-buttons">Edit</a>

                                                <a href="<c:url value='/admin/bookings/delete-${booking.id}' />"
                                                   onclick="return confirm('Are you sure you want to cancel this booking?')"
                                                   class="btn material-red btn-sm table-actions-buttons">Cancel</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> No new bookings
                                    yet.</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--/.Pending Bookings List-->

                <!-- Confirmed Bookings List-->
                <div class="tab-pane fade" id="confirmed_bookings" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(confirmedbookings) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>No</th>
                                        <th>Room</th>
                                        <th>People</th>
                                        <th>Comment</th>
                                        <th>Arrival Time</th>
                                        <th>Departure Time</th>
                                        <th>Booked By</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${confirmedbookings}" var="booking">
                                        <tr>
                                            <td>${booking.id}</td>
                                            <td>${booking.room.name}</td>
                                            <td>${booking.people}</td>
                                            <td>
                                                <button type="button"
                                                        class="btn grey btn-sm table-actions-buttons"
                                                        data-toggle="modal" data-target="#${booking.id}c">View
                                                </button>
                                            </td>
                                            <td>${booking.arrivalTime}</td>
                                            <td>${booking.departureTime}</td>
                                            <td>${booking.user.email}</td>
                                            <td class="table-action-mg">
                                                <a href="<c:url value='/admin/bookings/complete-${booking.id}' />"
                                                   onclick="return confirm('Are you sure you want to complete this booking?')"
                                                   class="btn light-blue btn-sm table-actions-buttons">Complete</a>

                                                <a href="<c:url value='/admin/bookings/edit-${booking.id}' />"
                                                   class="btn light-blue btn-sm table-actions-buttons">Edit</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <div class="modal hide fade" id="${booking.id}f" tabindex="-1" role="dialog"
                                         aria-labelledby="company-about-label"
                                         aria-hidden="true" style="display: none;">
                                        <div class="modal-dialog modal-sm">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-hidden="true"
                                                            style="font-size: 12px;">CLOSE
                                                    </button>
                                                    <h4 class="modal-title" id="confirmedroooms"
                                                        style="float: left">${booking.room.name}
                                                    </h4>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Description: ${booking.comment}</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> No bookings have been
                                    confirmed yet.</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--/.Confirmed Bookings List-->

                <!-- Completed Bookings List-->
                <div class="tab-pane fade" id="completed_bookings" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(completedbookings) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>No</th>
                                        <th>Booked Room</th>
                                        <th>People</th>
                                        <th>Comment</th>
                                        <th>Arrival Time</th>
                                        <th>Departure Time</th>
                                        <th>Booked By</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${completedbookings}" var="booking">
                                        <tr>
                                            <td>${booking.id}</td>
                                            <td>${booking.roomBooked}</td>
                                            <td>${booking.people}</td>
                                            <td>
                                                <button type="button"
                                                        class="btn grey btn-sm table-actions-buttons"
                                                        data-toggle="modal" data-target="#${booking.id}c">View
                                                </button>
                                            </td>
                                            <td>${booking.arrivalTime}</td>
                                            <td>${booking.departureTime}</td>
                                            <td>${booking.user.email}</td>
                                        </tr>
                                    </c:forEach>
                                    <div class="modal hide fade" id="${booking.id}c" tabindex="-1" role="dialog"
                                         aria-labelledby="company-about-label"
                                         aria-hidden="true" style="display: none;">
                                        <div class="modal-dialog modal-sm">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal"
                                                            aria-hidden="true"
                                                            style="font-size: 12px;">CLOSE
                                                    </button>
                                                    <h4 class="modal-title" id="completedroooms"
                                                        style="float: left">${booking.room.name}
                                                    </h4>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Description: ${booking.comment}</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> No bookings have been
                                    completed
                                    yet</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--/.Completed Bookings List-->

                <!-- All Bookings List-->
                <div class="tab-pane fade" id="bookings" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(bookings) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>No</th>
                                        <th>Room</th>
                                        <th>People</th>
                                        <th>Comment</th>
                                        <th>Arrival Time</th>
                                        <th>Departure Time</th>
                                        <th>Booked By</th>
                                        <th>Status</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${bookings}" var="booking">
                                        <tr>
                                            <td>${booking.id}</td>
                                            <c:choose>
                                                <c:when test="${booking.room != null}">
                                                    <td>${booking.room.name}</td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td>${booking.roomBooked}</td>
                                                </c:otherwise>
                                            </c:choose>

                                            <td>${booking.people}</td>
                                            <td>
                                                <button type="button"
                                                        class="btn grey btn-sm table-actions-buttons"
                                                        data-toggle="modal" data-target="#${booking.id}">View
                                                </button>
                                            </td>
                                            <td>${booking.arrivalTime}</td>
                                            <td>${booking.departureTime}</td>
                                            <td>${booking.user.email}</td>
                                            <td>${booking.status}</td>
                                        </tr>
                                        <div class="modal hide fade" id="${booking.id}" tabindex="-1" role="dialog"
                                             aria-labelledby="company-about-label"
                                             aria-hidden="true" style="display: none;">
                                            <div class="modal-dialog modal-sm">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <button type="button" class="close" data-dismiss="modal"
                                                                aria-hidden="true"
                                                                style="font-size: 12px;">CLOSE
                                                        </button>
                                                        <h4 class="modal-title" id="bookingroom"
                                                            style="float: left">${booking.room.name}
                                                        </h4>
                                                    </div>
                                                    <div class="modal-body">
                                                        <p>Description: ${booking.comment}</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> No bookings created
                                    yet.</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!--Admins List-->
                <div class="tab-pane fade" id="admins" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(admins) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>ID</th>
                                        <th>First Name</th>
                                        <th>Last Name</th>
                                        <th>Email</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${admins}" var="admins">
                                        <tr>
                                            <td>${admins.id}</td>
                                            <td>${admins.firstName}</td>
                                            <td>${admins.lastName}</td>
                                            <td>${admins.email}</td>

                                            <td class="table-actions">
                                                <a href="<c:url value='admin/user/edit-${admins.email}' />"
                                                   class="btn light-blue btn-sm table-actions-buttons">Edit</a>

                                                <c:if test="${username != admins.email}">
                                                    <a href="<c:url value='admin/user/delete-${admins.email}' />"
                                                       onclick="return confirm('Are you sure you want to delete this admin?')"
                                                       class="btn material-red btn-sm table-actions-buttons">Delete</a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> No admins assigned
                                    yet.</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--/.Admins List-->

                <!--Customers List-->
                <div class="tab-pane fade" id="customers" role="tabpanel">
                    <div class="table-responsive">
                        <c:choose>
                            <c:when test="${fn:length(customers) > 0}">
                                <table class="table table-striped">
                                    <thead class="thead-inverse">
                                    <tr>
                                        <th>ID</th>
                                        <th>First Name</th>
                                        <th>Last Name</th>
                                        <th>Email</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${customers}" var="customers">
                                        <tr>
                                            <td>${customers.id}</td>
                                            <td>${customers.firstName}</td>
                                            <td>${customers.lastName}</td>
                                            <td>${customers.email}</td>

                                            <td class="table-actions">
                                                <a href="<c:url value='admin/user/edit-${customers.email}' />"
                                                   class="btn light-blue btn-sm table-actions-buttons">Edit</a>

                                                <a href="<c:url value='admin/user/delete-${customers.email}' />"
                                                   onclick="return confirm('Are you sure you want to delete this customer?')"
                                                   class="btn material-red btn-sm table-actions-buttons">Delete</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>

                            </c:when>
                            <c:otherwise>
                                <h4 class="h4-responsive text-muted" style="margin-top: 30px"> No customers
                                    registered
                                    yet.</h4>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <!--/.Customers List-->
            </div>
        </div>
    </div>
</body>
</html>