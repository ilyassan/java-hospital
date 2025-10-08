<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Consultation" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Patient" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.TechnicalAct" %>
<%@ page import="com.ilyassan.medicalteleexpertise.enums.Status" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User user = (User) request.getAttribute("user");
    Consultation consultation = (Consultation) request.getAttribute("consultation");
    Patient patient = consultation.getPatient();
    User generalist = consultation.getGeneralist();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    boolean isCompleted = consultation.getStatus() == Status.COMPLETED;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Consultation Details - Medical Tele-Expertise</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { padding: 20px; }
        .priority-urgent { background-color: #ffebee; color: #c62828; border: 1px solid #f44336; }
        .priority-normal { background-color: #e3f2fd; color: #1565c0; border: 1px solid #2196F3; }
        .priority-non-urgent { background-color: #f1f8e9; color: #558b2f; border: 1px solid #8bc34a; }
    </style>
</head>
<body>
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Consultation Details</h1>
        <a href="<%= request.getContextPath() %>/logout" class="btn btn-danger">Logout</a>
    </div>

    <div class="card mb-3">
        <div class="card-header bg-secondary text-white">
            <h5 class="mb-0">Specialist Information</h5>
        </div>
        <div class="card-body">
            <p><strong>Name:</strong> Dr. <%= user.getFirstName() %> <%= user.getLastName() %></p>
            <p class="mb-0"><strong>Specialty:</strong> <%= user.getSpecialty() %></p>
        </div>
    </div>

    <div class="card mb-3 border-success">
        <div class="card-header bg-success text-white">
            <h5 class="mb-0">Patient Information</h5>
        </div>
        <div class="card-body">
            <p><strong>CIN:</strong> <%= patient.getCin() %></p>
            <p><strong>Name:</strong> <%= patient.getFirstName() %> <%= patient.getLastName() %></p>
            <p><strong>Date of Birth:</strong> <%= patient.getDateOfBirth().format(dateFormatter) %></p>

            <% if (patient.getVitalSignsTimestamp() != null) { %>
            <h6 class="mt-3 mb-2">Vital Signs (Last recorded: <%= patient.getVitalSignsTimestamp() %>)</h6>
            <div class="row">
                <div class="col-md-6">
                    <p><strong>Blood Pressure:</strong> <%= patient.getBloodPressure() != null ? patient.getBloodPressure() : "N/A" %></p>
                    <p><strong>Heart Rate:</strong> <%= patient.getHeartRate() != null ? patient.getHeartRate() : "N/A" %></p>
                    <p><strong>Temperature:</strong> <%= patient.getTemperature() != null ? patient.getTemperature() + "°C" : "N/A" %></p>
                </div>
                <div class="col-md-6">
                    <p><strong>Respiratory Rate:</strong> <%= patient.getRespiratoryRate() != null ? patient.getRespiratoryRate() : "N/A" %></p>
                    <p><strong>Weight:</strong> <%= patient.getWeight() != null ? patient.getWeight() + " kg" : "N/A" %></p>
                    <p><strong>Height:</strong> <%= patient.getHeight() != null ? patient.getHeight() + " cm" : "N/A" %></p>
                </div>
            </div>
            <% } %>

            <% if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) { %>
            <div class="alert alert-danger" role="alert">
                <strong>Allergies:</strong> <%= patient.getAllergies() %>
            </div>
            <% } %>

            <% if (patient.getTreatments() != null && !patient.getTreatments().isEmpty()) { %>
            <p><strong>Current Treatments:</strong> <%= patient.getTreatments() %></p>
            <% } %>
        </div>
    </div>

    <% if (generalist != null) { %>
    <div class="card mb-3 border-warning">
        <div class="card-header bg-warning">
            <h5 class="mb-0">Generalist Information</h5>
        </div>
        <div class="card-body">
            <p><strong>Name:</strong> Dr. <%= generalist.getFirstName() %> <%= generalist.getLastName() %></p>
            <p><strong>Email:</strong> <%= generalist.getEmail() %></p>
            <% if (generalist.getPhone() != null && !generalist.getPhone().isEmpty()) { %>
            <p class="mb-0"><strong>Phone:</strong> <span class="text-primary fs-5 fw-bold"><%= generalist.getPhone() %></span></p>
            <% } %>
        </div>
    </div>
    <% } %>

    <div class="card mb-3 border-primary">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0">Consultation Details</h5>
        </div>
        <div class="card-body">
            <% if (consultation.getDate() != null) { %>
            <p><strong>Consultation Date/Time:</strong> <%= consultation.getDate().format(dateTimeFormatter) %></p>
            <% } %>
            <p>
                <strong>Status:</strong>
                <span class="badge <%= isCompleted ? "bg-success" : "bg-warning text-dark" %>">
                    <%= isCompleted ? "Completed" : "Pending Opinion" %>
                </span>
            </p>
            <p>
                <strong>Priority:</strong>
                <%
                    String priorityClass = "priority-normal";
                    if (consultation.getPriority().toString().equals("URGENT")) {
                        priorityClass = "priority-urgent";
                    } else if (consultation.getPriority().toString().equals("NON_URGENT")) {
                        priorityClass = "priority-non-urgent";
                    }
                %>
                <span class="badge <%= priorityClass %>"><%= consultation.getPriority() %></span>
            </p>
            <p><strong>Cost:</strong> <%= String.format("%.2f", consultation.getCost()) %> DH</p>

            <h6 class="mt-3">Observations from Generalist</h6>
            <div class="border rounded p-3 bg-light"><%= consultation.getObservations() %></div>

            <% if (consultation.getTechnicalActs() != null && !consultation.getTechnicalActs().isEmpty()) { %>
            <h6 class="mt-3">Technical Acts Performed</h6>
            <ul class="list-group">
                <% for (TechnicalAct act : consultation.getTechnicalActs()) { %>
                <li class="list-group-item"><%= act.getName() %> - <%= String.format("%.2f", act.getPrice()) %> DH</li>
                <% } %>
            </ul>
            <% } %>
        </div>
    </div>

    <% if (isCompleted) { %>
    <div class="card mb-3 border-success">
        <div class="card-header bg-success text-white">
            <h5 class="mb-0">Your Opinion and Recommendations (Completed)</h5>
        </div>
        <div class="card-body">
            <h6>Opinion (Diagnosis/Analysis)</h6>
            <div class="border rounded p-3 bg-light mb-3"><%= consultation.getOpinion() %></div>

            <h6>Recommendations (Treatment Plan)</h6>
            <div class="border rounded p-3 bg-light"><%= consultation.getRecommendations() %></div>
        </div>
    </div>
    <% } else { %>
    <div class="card mb-3">
        <div class="card-header bg-info text-white">
            <h5 class="mb-0">Provide Your Opinion and Recommendations</h5>
        </div>
        <div class="card-body">
            <form action="<%= request.getContextPath() %>/specialist/consultations?action=submit" method="post" id="consultationForm">
                <input type="hidden" name="consultationId" value="<%= consultation.getId() %>">

                <div class="mb-3">
                    <label for="opinion" class="form-label">Opinion (Diagnosis/Analysis) *</label>
                    <textarea class="form-control" id="opinion" name="opinion" rows="5" required placeholder="Provide your medical opinion, diagnosis, and analysis..."></textarea>
                </div>

                <div class="mb-3">
                    <label for="recommendations" class="form-label">Recommendations (Treatment Plan) *</label>
                    <textarea class="form-control" id="recommendations" name="recommendations" rows="5" required placeholder="Provide your recommendations and treatment plan..."></textarea>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-success">Submit Opinion & Complete Consultation</button>
                    <a href="<%= request.getContextPath() %>/specialist/consultations" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
    <% } %>

    <div class="mt-3">
        <a href="<%= request.getContextPath() %>/specialist/consultations" class="btn btn-link">← Back to My Agenda</a>
        <a href="<%= request.getContextPath() %>/dashboard" class="btn btn-link">← Back to Dashboard</a>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('consultationForm')?.addEventListener('submit', function(e) {
        const opinion = document.getElementById('opinion').value.trim();
        const recommendations = document.getElementById('recommendations').value.trim();

        if (!opinion || !recommendations) {
            e.preventDefault();
            alert('Both Opinion and Recommendations are required.');
            return false;
        }

        if (!confirm('Are you sure you want to submit your opinion? This will mark the consultation as completed.')) {
            e.preventDefault();
            return false;
        }
    });
</script>
</body>
</html>
