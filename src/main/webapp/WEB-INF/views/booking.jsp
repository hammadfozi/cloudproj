<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Booking</title>

    <jsp:include page="includes/head.jsp"/>

    <script>
        function verifyPeople() {
            var check = document.getElementById('people').value;
            if (Number(check) < 0) {
                document.getElementById("people").style.borderColor = '#FF0013';
                document.getElementById("people").value = null;
            } else if (Number(check) > 6) {
                document.getElementById("people").value = 6;
            } else {
                document.getElementById("people").style.borderColor = '#A7FF00';
            }
        }
    </script>
</head>
<body class="background">
<jsp:include page="includes/header.jsp"/>

<section id="booking" style="margin-top: 5%">

    <sec:authorize access="hasAnyRole('ADMIN', 'USER')">
        <form:form action="/booking" method="POST" modelAttribute="booking"
                   class="form-horizontal">
            <form:input type="hidden" path="id" id="id"/>
            <form:hidden path="status"/>
            <div>
                <c:choose>
                    <c:when test="${fn:length(rooms) > 0}">
                        <h1 class="heading h1-responsive material-red center-div white-text card-header">BOOK A
                            ROOM</h1>

                        <div class="form-group card card-block">

                            <div class="grid">

                                <div>
                                    <label for="people">Number of people</label>
                                    <form:input id="people" path="people" onchange="verifyPeople()" type="number"
                                                min="0"
                                                max="6"
                                                required="required"/>
                                </div>
                                <div class="has-error">
                                    <form:errors path="people" class="help-inline material-red-text "/>
                                </div>
                                </br>
                                <label for="room">Select Room Type</label>
                                <div>
                                    <form:select id="room" required="required" path="room"
                                                 cssClass="input-block-level rgba-white-slight waves-input-wrapper"
                                                 items="${rooms}"
                                                 itemValue="id"
                                                 itemLabel="name">
                                    </form:select>
                                    <div class="has-error">
                                        <form:errors path="room" class="help-inline material-red-text "/>
                                    </div>
                                </div>
                                </br>
                                <div>
                                    <label for="arrivalTime">Arrival Time</label>
                                    <form:input path="arrivalTime" id="arrivalTime" type="date" required="required"/>
                                </div>
                                <div class="has-error">
                                    <form:errors path="arrivalTime" class="help-inline material-red-text "/>
                                </div>
                                </br>
                                <div>
                                    <label for="departureTime">Departure Time</label>
                                    <form:input path="departureTime" id="departureTime" type="date"
                                                required="required"/>
                                </div>
                                <div class="has-error">
                                    <form:errors path="departureTime" class="help-inline material-red-text "/>
                                </div>
                            </div>
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                            <div class="grid center-div">
                                <button type="submit" value="Submit"
                                        class="btn-primary material-red waves-button waves-light"
                                        style="width: 240px;">BOOK
                                </button>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <h1 class="heading h1-responsive material-red center-div white-text card-header">WE ARE
                            FULL</h1>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </sec:authorize>
</section>
</body>
</html>
