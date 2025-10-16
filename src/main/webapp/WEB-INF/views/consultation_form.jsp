<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Queue" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.TechnicalAct" %>
<%@ page import="com.ilyassan.medicalteleexpertise.enums.Specialty" %>
<%@ page import="com.ilyassan.medicalteleexpertise.util.CSRFUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    User user = (User) request.getAttribute("user");
    Queue queue = (Queue) request.getAttribute("queue");
    Patient patient = (Patient) request.getAttribute("patient");
    List<TechnicalAct> technicalActs = (List<TechnicalAct>) request.getAttribute("technicalActs");
    List<User> specialists = (List<User>) request.getAttribute("specialists");
    Map<Long, List<String>> unavailableSlotsToday = (Map<Long, List<String>>) request.getAttribute("unavailableSlotsToday");
    Map<Long, List<String>> unavailableSlotsTomorrow = (Map<Long, List<String>>) request.getAttribute("unavailableSlotsTomorrow");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Create Consultation - Medical Tele-Expertise</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1000px;
            margin: 50px auto;
            padding: 20px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        .user-info {
            background: #f4f4f4;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .patient-info {
            background: #e8f5e9;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .logout-btn {
            background-color: #f44336;
            color: white;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], textarea, select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        .checkbox-group {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 10px;
        }
        .checkbox-item {
            display: flex;
            align-items: center;
        }
        .checkbox-item input[type="checkbox"] {
            margin-right: 8px;
        }
        .submit-btn {
            background-color: #4CAF50;
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .submit-btn:hover {
            background-color: #45a049;
        }
        .specialist-btn {
            background-color: #2196F3;
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            margin-left: 10px;
        }
        .specialist-btn:hover {
            background-color: #0b7dda;
        }
        .cancel-btn {
            background-color: #9e9e9e;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 4px;
            display: inline-block;
            margin-left: 10px;
        }
        .error {
            color: #d32f2f;
            background-color: #ffebee;
            padding: 15px;
            border: 2px solid #ef5350;
            border-radius: 4px;
            margin-bottom: 20px;
            font-weight: bold;
        }
        #specialistSection {
            display: none;
            background: #e3f2fd;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        #agendaSection {
            display: none;
            background: #f5f5f5;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .time-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 10px;
            margin-top: 15px;
        }
        .time-slot {
            padding: 12px;
            text-align: center;
            border: 2px solid #4CAF50;
            border-radius: 4px;
            cursor: pointer;
            background-color: white;
            transition: all 0.3s;
        }
        .time-slot:hover:not(.disabled):not(.selected) {
            background-color: #e8f5e9;
            transform: scale(1.05);
        }
        .time-slot.selected {
            background-color: #4CAF50;
            color: white;
            font-weight: bold;
        }
        .time-slot.disabled {
            background-color: #f5f5f5;
            border-color: #ccc;
            color: #999;
            cursor: not-allowed;
        }
        .period-label {
            font-weight: bold;
            margin-top: 20px;
            margin-bottom: 10px;
            color: #333;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>Create Consultation</h1>
    <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
</div>

<div class="user-info">
    <h2>Generalist: <%= user.getFirstName() %> <%= user.getLastName() %></h2>
    <p><strong>Email:</strong> <%= user.getEmail() %></p>
</div>

<div class="patient-info">
    <h3>Patient Information</h3>
    <p><strong>CIN:</strong> <%= patient.getCin() %></p>
    <p><strong>Name:</strong> <%= patient.getFirstName() %> <%= patient.getLastName() %></p>
    <p><strong>Date of Birth:</strong> <%= patient.getDateOfBirth() %></p>
    <% if (patient.getVitalSignsTimestamp() != null) { %>
    <p><strong>Vital Signs (Last recorded: <%= patient.getVitalSignsTimestamp() %>):</strong></p>
    <p>
        BP: <%= patient.getBloodPressure() != null ? patient.getBloodPressure() : "N/A" %>,
        HR: <%= patient.getHeartRate() != null ? patient.getHeartRate() : "N/A" %>,
        Temp: <%= patient.getTemperature() != null ? patient.getTemperature() : "N/A" %>,
        RR: <%= patient.getRespiratoryRate() != null ? patient.getRespiratoryRate() : "N/A" %>,
        Weight: <%= patient.getWeight() != null ? patient.getWeight() : "N/A" %> kg,
        Height: <%= patient.getHeight() != null ? patient.getHeight() : "N/A" %> cm
    </p>
    <% } %>
    <% if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) { %>
    <p><strong>Allergies:</strong> <%= patient.getAllergies() %></p>
    <% } %>
    <% if (patient.getTreatments() != null && !patient.getTreatments().isEmpty()) { %>
    <p><strong>Current Treatments:</strong> <%= patient.getTreatments() %></p>
    <% } %>
</div>

<div class="content">
    <h3>Consultation Details</h3>
    <% if (error != null) { %>
    <p class="error"><%= error %></p>
    <% } %>

    <form action="<%= request.getContextPath() %>/consultation?action=store" method="post" id="consultationForm">
        <input type="hidden" name="csrf_token" value="<%= CSRFUtil.getToken(request) %>">
        <input type="hidden" name="queueId" value="<%= queue.getId() %>">

        <div class="form-group">
            <label>Need Specialist Opinion? *</label>
            <div style="margin-top: 10px;">
                <label style="font-weight: normal; margin-right: 20px;">
                    <input type="radio" name="needSpecialist" value="no" checked onclick="toggleSpecialistFields(false)">
                    No - I can complete the consultation
                </label>
                <label style="font-weight: normal;">
                    <input type="radio" name="needSpecialist" value="yes" onclick="toggleSpecialistFields(true)">
                    Yes - I need a specialist opinion
                </label>
            </div>
        </div>

        <div id="specialistSection">
            <h4>Select Specialist</h4>
            <div class="form-group">
                <label for="specialistId">Specialist *</label>
                <select id="specialistId" name="specialistId" onchange="showAgenda()">
                    <option value="">-- Select a Specialist --</option>
                    <% if (specialists != null && !specialists.isEmpty()) { %>
                        <% for (User specialist : specialists) { %>
                        <option value="<%= specialist.getId() %>"
                                data-specialty="<%= specialist.getSpecialty() %>"
                                data-tariff="<%= specialist.getTariff() %>">
                            <%= specialist.getFirstName() %> <%= specialist.getLastName() %> -
                            <%= specialist.getSpecialty() %>
                            (<%= specialist.getTariff() %> DH)
                        </option>
                        <% } %>
                    <% } else { %>
                        <option value="" disabled>No specialists available</option>
                    <% } %>
                </select>
            </div>

            <div id="agendaSection">
                <h4>Select Time Slot</h4>
                <p style="font-size: 14px; color: #666;">Available slots are 30 minutes each. Disabled slots are already booked or passed.</p>
                <p id="dateIndicator" style="font-size: 14px; font-weight: bold; color: #4CAF50; margin-bottom: 15px;"></p>

                <input type="hidden" id="selectedDateTime" name="selectedDateTime">

                <div class="period-label">Morning (8:00 AM - 12:00 PM)</div>
                <div class="time-grid" id="morningSlots"></div>

                <div class="period-label">Afternoon (2:00 PM - 6:00 PM)</div>
                <div class="time-grid" id="afternoonSlots"></div>
            </div>
        </div>

        <div class="form-group">
            <label for="observations">Observations (Clinical Examination, Symptoms) *</label>
            <textarea id="observations" name="observations" required></textarea>
        </div>

        <div class="form-group" id="opinionGroup">
            <label for="opinion">Opinion (Diagnosis) *</label>
            <textarea id="opinion" name="opinion" required></textarea>
        </div>

        <div class="form-group" id="recommendationsGroup">
            <label for="recommendations">Recommendations (Prescription, Treatment Plan) *</label>
            <textarea id="recommendations" name="recommendations" required></textarea>
        </div>

        <div class="form-group">
            <label for="priority">Priority *</label>
            <select id="priority" name="priority" required>
                <option value="">-- Select Priority --</option>
                <option value="URGENT">Urgent</option>
                <option value="NORMAL">Normal</option>
                <option value="NON_URGENT">Non Urgent</option>
            </select>
        </div>

        <div class="form-group">
            <label>Technical Acts</label>
            <div class="checkbox-group">
                <% if (technicalActs != null && !technicalActs.isEmpty()) { %>
                    <% for (TechnicalAct act : technicalActs) { %>
                    <div class="checkbox-item">
                        <input type="checkbox" id="act_<%= act.getId() %>" name="technicalActIds" value="<%= act.getId() %>">
                        <label for="act_<%= act.getId() %>" style="font-weight: normal;">
                            <%= act.getName() %> (<%= act.getPrice() %> DH)
                        </label>
                    </div>
                    <% } %>
                <% } else { %>
                    <p>No technical acts available</p>
                <% } %>
            </div>
        </div>

        <div class="form-group">
            <button type="submit" class="submit-btn">Submit Consultation</button>
            <a href="<%= request.getContextPath() %>/queue" class="cancel-btn">Cancel</a>
        </div>
    </form>

    <script>
        // Unavailable slots data from server
        const unavailableSlotsToday = <%= unavailableSlotsToday != null ?
            new com.google.gson.Gson().toJson(unavailableSlotsToday) : "{}" %>;
        const unavailableSlotsTomorrow = <%= unavailableSlotsTomorrow != null ?
            new com.google.gson.Gson().toJson(unavailableSlotsTomorrow) : "{}" %>;

        function toggleSpecialistFields(needSpecialist) {
            const opinionGroup = document.getElementById('opinionGroup');
            const recommendationsGroup = document.getElementById('recommendationsGroup');
            const opinion = document.getElementById('opinion');
            const recommendations = document.getElementById('recommendations');
            const specialistSection = document.getElementById('specialistSection');
            const specialistId = document.getElementById('specialistId');

            if (needSpecialist) {
                // Hide and disable opinion and recommendations (specialist will fill these)
                opinionGroup.style.display = 'none';
                recommendationsGroup.style.display = 'none';
                opinion.required = false;
                recommendations.required = false;
                opinion.value = '';
                recommendations.value = '';

                // Show specialist section
                specialistSection.style.display = 'block';
                specialistId.required = true;
            } else {
                // Show and enable opinion and recommendations (generalist fills all)
                opinionGroup.style.display = 'block';
                recommendationsGroup.style.display = 'block';
                opinion.required = true;
                recommendations.required = true;

                // Hide specialist section
                specialistSection.style.display = 'none';
                specialistId.required = false;
                document.getElementById('agendaSection').style.display = 'none';
            }
        }

        function showAgenda() {
            const specialistId = document.getElementById('specialistId').value;
            const agendaSection = document.getElementById('agendaSection');

            if (!specialistId) {
                agendaSection.style.display = 'none';
                return;
            }

            agendaSection.style.display = 'block';
            generateTimeSlots(specialistId);
        }

        function generateTimeSlots(specialistId) {
            const morningSlots = document.getElementById('morningSlots');
            const afternoonSlots = document.getElementById('afternoonSlots');
            const dateIndicator = document.getElementById('dateIndicator');

            morningSlots.innerHTML = '';
            afternoonSlots.innerHTML = '';

            const now = new Date();
            const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

            // Check if all today's slots are unavailable or past
            const unavailableToday = unavailableSlotsToday[specialistId] || [];
            let allTodaySlotsUnavailable = true;

            // Check all today's time slots
            for (let hour = 8; hour < 12; hour++) {
                for (let minute = 0; minute < 60; minute += 30) {
                    const slotTime = new Date(today.getFullYear(), today.getMonth(), today.getDate(), hour, minute);
                    const hourStr = hour < 10 ? '0' + hour : '' + hour;
                    const minuteStr = minute < 10 ? '0' + minute : '' + minute;
                    const timeStr = hourStr + ':' + minuteStr;

                    const isPast = slotTime <= now;
                    const isBooked = unavailableToday.includes(timeStr);

                    if (!isPast && !isBooked) {
                        allTodaySlotsUnavailable = false;
                        break;
                    }
                }
                if (!allTodaySlotsUnavailable) break;
            }

            if (allTodaySlotsUnavailable) {
                for (let hour = 14; hour < 18; hour++) {
                    for (let minute = 0; minute < 60; minute += 30) {
                        const slotTime = new Date(today.getFullYear(), today.getMonth(), today.getDate(), hour, minute);
                        const hourStr = hour < 10 ? '0' + hour : '' + hour;
                        const minuteStr = minute < 10 ? '0' + minute : '' + minute;
                        const timeStr = hourStr + ':' + minuteStr;

                        const isPast = slotTime <= now;
                        const isBooked = unavailableToday.includes(timeStr);

                        if (!isPast && !isBooked) {
                            allTodaySlotsUnavailable = false;
                            break;
                        }
                    }
                    if (!allTodaySlotsUnavailable) break;
                }
            }

            // Use tomorrow's date if all today's slots are unavailable
            let targetDate, unavailable, dateLabel;
            if (allTodaySlotsUnavailable) {
                const tomorrow = new Date(today);
                tomorrow.setDate(tomorrow.getDate() + 1);
                targetDate = tomorrow;
                unavailable = unavailableSlotsTomorrow[specialistId] || [];
                dateLabel = 'Tomorrow - ' + tomorrow.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
                console.log('All today slots unavailable, showing tomorrow');
            } else {
                targetDate = today;
                unavailable = unavailableToday;
                dateLabel = 'Today - ' + today.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
                console.log('Showing today slots');
            }

            dateIndicator.textContent = dateLabel;

            // Morning slots: 8:00 AM - 12:00 PM (8 slots of 30 minutes)
            for (let hour = 8; hour < 12; hour++) {
                for (let minute = 0; minute < 60; minute += 30) {
                    createTimeSlot(morningSlots, hour, minute, targetDate, unavailable, allTodaySlotsUnavailable);
                }
            }

            // Afternoon slots: 2:00 PM - 6:00 PM (8 slots of 30 minutes)
            for (let hour = 14; hour < 18; hour++) {
                for (let minute = 0; minute < 60; minute += 30) {
                    createTimeSlot(afternoonSlots, hour, minute, targetDate, unavailable, allTodaySlotsUnavailable);
                }
            }
        }

        function createTimeSlot(container, hour, minute, targetDate, unavailable, isShowingTomorrow) {
            const slotDiv = document.createElement('div');
            slotDiv.className = 'time-slot';

            // Format time string
            const hourStr = hour < 10 ? '0' + hour : '' + hour;
            const minuteStr = minute < 10 ? '0' + minute : '' + minute;
            const timeStr = hourStr + ':' + minuteStr;
            slotDiv.textContent = timeStr;

            // Create datetime string in format: YYYY-MM-DD HH:mm:00
            const year = targetDate.getFullYear();
            const month = targetDate.getMonth() + 1;
            const day = targetDate.getDate();

            const monthStr = month < 10 ? '0' + month : '' + month;
            const dayStr = day < 10 ? '0' + day : '' + day;

            const dateTimeStr = year + '-' + monthStr + '-' + dayStr + ' ' + timeStr + ':00';

            // Check if this slot is in the past (only for today's slots)
            const now = new Date();
            const slotTime = new Date(year, month - 1, day, hour, minute);
            const isPast = !isShowingTomorrow && (slotTime <= now);

            console.log('Creating slot - hour:', hour, 'minute:', minute, 'dateTimeStr:', dateTimeStr, 'isPast:', isPast, 'isShowingTomorrow:', isShowingTomorrow);

            // Check if this slot is unavailable or in the past
            if (unavailable.includes(timeStr)) {
                slotDiv.classList.add('disabled');
                slotDiv.title = 'This time slot is already booked';
            } else if (isPast) {
                slotDiv.classList.add('disabled');
                slotDiv.title = 'This time slot has already passed';
            } else {
                slotDiv.onclick = function() {
                    selectTimeSlot(this, dateTimeStr);
                };
            }

            container.appendChild(slotDiv);
        }

        function selectTimeSlot(element, dateTimeStr) {
            // Remove selection from all slots
            document.querySelectorAll('.time-slot').forEach(slot => {
                slot.classList.remove('selected');
            });

            // Select clicked slot
            element.classList.add('selected');

            // Set hidden input value
            document.getElementById('selectedDateTime').value = dateTimeStr;
            console.log('Selected time slot:', dateTimeStr);
        }

        // Form validation
        document.getElementById('consultationForm').addEventListener('submit', function(e) {
            const needSpecialist = document.querySelector('input[name="needSpecialist"]:checked').value;

            if (needSpecialist === 'yes') {
                const specialistId = document.getElementById('specialistId').value;
                const selectedDateTime = document.getElementById('selectedDateTime').value;


                console.log('Form validation - specialistId:', specialistId);
                console.log('Form validation - selectedDateTime:', selectedDateTime);

                if (!specialistId) {
                    e.preventDefault();
                    alert('Please select a specialist');
                    return false;
                }

                if (!selectedDateTime || selectedDateTime.trim() === '') {
                    e.preventDefault();
                    alert('Please select a time slot by clicking on one of the available time blocks');
                    return false;
                }
            }
        });
    </script>
</div>

<p><a href="<%= request.getContextPath() %>/queue">Back to Queue</a></p>
<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>
