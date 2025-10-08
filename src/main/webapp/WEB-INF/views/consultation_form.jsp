<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Queue" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.TechnicalAct" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) request.getAttribute("user");
    Queue queue = (Queue) request.getAttribute("queue");
    Patient patient = (Patient) request.getAttribute("patient");
    List<TechnicalAct> technicalActs = (List<TechnicalAct>) request.getAttribute("technicalActs");
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
            color: red;
            margin-bottom: 10px;
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
        function toggleSpecialistFields(needSpecialist) {
            const opinionGroup = document.getElementById('opinionGroup');
            const recommendationsGroup = document.getElementById('recommendationsGroup');
            const opinion = document.getElementById('opinion');
            const recommendations = document.getElementById('recommendations');

            if (needSpecialist) {
                // Hide and disable opinion and recommendations
                opinionGroup.style.display = 'none';
                recommendationsGroup.style.display = 'none';
                opinion.required = false;
                recommendations.required = false;
                opinion.value = '';
                recommendations.value = '';
            } else {
                // Show and enable opinion and recommendations
                opinionGroup.style.display = 'block';
                recommendationsGroup.style.display = 'block';
                opinion.required = true;
                recommendations.required = true;
            }
        }
    </script>
</div>

<p><a href="<%= request.getContextPath() %>/queue">Back to Queue</a></p>
<p><a href="<%= request.getContextPath() %>/dashboard">Back to Dashboard</a></p>
</body>
</html>
