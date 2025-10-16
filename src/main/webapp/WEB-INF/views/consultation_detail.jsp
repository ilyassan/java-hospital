<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.User" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.Consultation" %>
<%@ page import="com.ilyassan.medicalteleexpertise.model.TechnicalAct" %>
<%@ page import="com.ilyassan.medicalteleexpertise.enums.Status" %>
<%@ page import="java.time.LocalDateTime" %>
<%
    User user = (User) request.getAttribute("user");
    Consultation consultation = (Consultation) request.getAttribute("consultation");

    boolean canCancel = consultation.getStatus() == Status.PENDING_SPECIALIST_OPINION
                        && consultation.getSpecialist() != null
                        && consultation.getDate() != null
                        && LocalDateTime.now().isBefore(consultation.getDate());

    boolean needsCompletion = consultation.getStatus() == Status.PENDING_SPECIALIST_OPINION
                              && consultation.getSpecialist() == null;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Consultation Details - Medical Tele-Expertise</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 1200px;
            margin: 50px auto;
            padding: 20px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .alert-warning {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeaa7;
        }
        .card {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
        }
        .card h3 {
            margin-top: 0;
            color: #4CAF50;
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }
        .info-row {
            margin: 10px 0;
            display: flex;
        }
        .info-label {
            font-weight: bold;
            min-width: 200px;
            color: #555;
        }
        .info-value {
            color: #333;
        }
        .status-badge {
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 14px;
            font-weight: bold;
            display: inline-block;
        }
        .status-pending-specialist {
            background-color: #d1ecf1;
            color: #0c5460;
        }
        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }
        .priority-badge {
            padding: 6px 12px;
            border-radius: 4px;
            font-size: 14px;
            font-weight: bold;
            display: inline-block;
        }
        .priority-urgent {
            background-color: #f8d7da;
            color: #721c24;
        }
        .priority-normal {
            background-color: #fff3cd;
            color: #856404;
        }
        .priority-non-urgent {
            background-color: #d1ecf1;
            color: #0c5460;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: Arial, sans-serif;
            font-size: 14px;
            resize: vertical;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
            display: inline-block;
            margin-right: 10px;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-danger {
            background-color: #f44336;
            color: white;
        }
        .btn-danger:hover {
            background-color: #da190b;
        }
        .btn-secondary {
            background-color: #757575;
            color: white;
        }
        .btn-secondary:hover {
            background-color: #616161;
        }
        .actions {
            margin-top: 20px;
            padding-top: 20px;
            border-top: 2px solid #ddd;
        }
        .back-link {
            margin-top: 20px;
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>Consultation Details</h1>
    <a href="<%= request.getContextPath() %>/logout" class="btn btn-danger">Logout</a>
</div>

<%
    String successMessage = (String) session.getAttribute("success");
    String errorMessage = (String) session.getAttribute("error");
    if (successMessage != null) {
        session.removeAttribute("success");
%>
<div class="alert alert-success"><%= successMessage %></div>
<%
    }
    if (errorMessage != null) {
        session.removeAttribute("error");
%>
<div class="alert alert-error"><%= errorMessage %></div>
<%
    }

    if (needsCompletion) {
%>
<div class="alert alert-warning">
    <strong>Action Required:</strong> This consultation was cancelled and needs to be completed. Please provide your opinion and recommendations below.
</div>
<%
    }
%>

<div class="card">
    <h3>Consultation Information</h3>
    <div class="info-row">
        <span class="info-label">Consultation ID:</span>
        <span class="info-value">#<%= consultation.getId() %></span>
    </div>
    <div class="info-row">
        <span class="info-label">Status:</span>
        <span class="info-value">
            <%
                String statusClass = "";
                String statusText = "";
                switch (consultation.getStatus()) {
                    case COMPLETED:
                        statusClass = "status-completed";
                        statusText = "Completed";
                        break;
                    case PENDING_SPECIALIST_OPINION:
                        statusClass = "status-pending-specialist";
                        statusText = "Pending Specialist";
                        break;
                }
            %>
            <span class="status-badge <%= statusClass %>"><%= statusText %></span>
        </span>
    </div>
    <div class="info-row">
        <span class="info-label">Priority:</span>
        <span class="info-value">
            <%
                String priorityClass = "";
                switch (consultation.getPriority()) {
                    case URGENT:
                        priorityClass = "priority-urgent";
                        break;
                    case NORMAL:
                        priorityClass = "priority-normal";
                        break;
                    case NON_URGENT:
                        priorityClass = "priority-non-urgent";
                        break;
                }
            %>
            <span class="priority-badge <%= priorityClass %>"><%= consultation.getPriority() %></span>
        </span>
    </div>
    <div class="info-row">
        <span class="info-label">Cost:</span>
        <span class="info-value"><%= String.format("%.2f DH", consultation.getCost()) %></span>
    </div>
    <div class="info-row">
        <span class="info-label">Created At:</span>
        <span class="info-value"><%= consultation.getCreatedAt() %></span>
    </div>
    <% if (consultation.getDate() != null) { %>
    <div class="info-row">
        <span class="info-label">Appointment Date:</span>
        <span class="info-value"><%= consultation.getDate() %></span>
    </div>
    <% } %>
</div>

<div class="card">
    <h3>Patient Information</h3>
    <div class="info-row">
        <span class="info-label">Name:</span>
        <span class="info-value"><%= consultation.getPatient().getFirstName() %> <%= consultation.getPatient().getLastName() %></span>
    </div>
    <div class="info-row">
        <span class="info-label">CIN:</span>
        <span class="info-value"><%= consultation.getPatient().getCin() %></span>
    </div>
</div>

<% if (consultation.getSpecialist() != null) { %>
<div class="card">
    <h3>Specialist Information</h3>
    <div class="info-row">
        <span class="info-label">Name:</span>
        <span class="info-value"><%= consultation.getSpecialist().getFirstName() %> <%= consultation.getSpecialist().getLastName() %></span>
    </div>
    <div class="info-row">
        <span class="info-label">Email:</span>
        <span class="info-value"><%= consultation.getSpecialist().getEmail() %></span>
    </div>
    <div class="info-row">
        <span class="info-label">Phone:</span>
        <span class="info-value"><%= consultation.getSpecialist().getPhone() != null ? consultation.getSpecialist().getPhone() : "N/A" %></span>
    </div>
    <% if (consultation.getMeetLink() != null) { %>
    <div class="info-row">
        <span class="info-label">Meet Link:</span>
        <span class="info-value"><a href="<%= consultation.getMeetLink() %>" target="_blank">Join Meeting</a></span>
    </div>
    <% } %>
</div>
<% } %>

<div class="card">
    <h3>Technical Acts</h3>
    <% if (consultation.getTechnicalActs() != null && !consultation.getTechnicalActs().isEmpty()) { %>
        <ul>
            <% for (TechnicalAct act : consultation.getTechnicalActs()) { %>
            <li><%= act.getName() %> - <%= String.format("%.2f DH", act.getPrice()) %></li>
            <% } %>
        </ul>
    <% } else { %>
        <p>No technical acts performed.</p>
    <% } %>
</div>

<div class="card">
    <h3>Observations (from Generalist)</h3>
    <p><%= consultation.getObservations() %></p>
</div>

<% if (consultation.getOpinion() != null && !consultation.getOpinion().isEmpty()) { %>
<div class="card">
    <h3>Opinion (Diagnosis)</h3>
    <p><%= consultation.getOpinion() %></p>
</div>
<% } %>

<% if (consultation.getRecommendations() != null && !consultation.getRecommendations().isEmpty()) { %>
<div class="card">
    <h3>Recommendations (Treatment Plan)</h3>
    <p><%= consultation.getRecommendations() %></p>
</div>
<% } %>

<% if (needsCompletion) { %>
<div class="card">
    <h3>Complete Consultation</h3>
    <form action="<%= request.getContextPath() %>/consultation?action=complete" method="post" onsubmit="return confirmCompletion()">
        <input type="hidden" name="csrf_token" value="<%= session.getAttribute("CSRF_TOKEN") %>">
        <input type="hidden" name="consultationId" value="<%= consultation.getId() %>">

        <div class="form-group">
            <label for="opinion">Opinion (Diagnosis) *</label>
            <textarea id="opinion" name="opinion" rows="5" required placeholder="Enter your diagnosis and analysis..."></textarea>
        </div>

        <div class="form-group">
            <label for="recommendations">Recommendations (Treatment Plan) *</label>
            <textarea id="recommendations" name="recommendations" rows="5" required placeholder="Enter your treatment recommendations..."></textarea>
        </div>

        <div class="actions">
            <button type="submit" class="btn btn-primary">Submit & Complete Consultation</button>
            <a href="<%= request.getContextPath() %>/consultation" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
<% } %>

<% if (canCancel) { %>
<div class="card">
    <h3>Complete Consultation</h3>
    <form action="<%= request.getContextPath() %>/consultation?action=cancelAndComplete" method="post" onsubmit="return confirmSubmission()">
        <input type="hidden" name="csrf_token" value="<%= session.getAttribute("CSRF_TOKEN") %>">
        <input type="hidden" name="consultationId" value="<%= consultation.getId() %>">

        <div class="form-group">
            <label style="display: flex; align-items: center; cursor: pointer;">
                <input type="checkbox" id="cancelSpecialistCheckbox" name="cancelSpecialist" value="true" onchange="toggleCompletionFields()" style="margin-right: 10px; width: auto;">
                <span style="color: #f44336; font-weight: bold;">Cancel Specialist Review</span>
            </label>
            <small style="color: #666; margin-left: 30px; display: block; margin-top: 5px;">
                Check this if you want to cancel the specialist review and complete the consultation yourself
            </small>
        </div>

        <div id="completionFields" style="display: none;">
            <div class="alert alert-warning" style="margin-top: 15px;">
                <strong>Note:</strong> By canceling the specialist review, the specialist will be unassigned, the appointment will be removed, and the cost will be recalculated. You must provide your opinion and recommendations to complete the consultation.
            </div>

            <div class="form-group">
                <label for="opinion">Opinion (Diagnosis) *</label>
                <textarea id="opinion" name="opinion" rows="5" placeholder="Enter your diagnosis and analysis..."></textarea>
            </div>

            <div class="form-group">
                <label for="recommendations">Recommendations (Treatment Plan) *</label>
                <textarea id="recommendations" name="recommendations" rows="5" placeholder="Enter your treatment recommendations..."></textarea>
            </div>

            <div class="actions">
                <button type="submit" class="btn btn-primary">Submit</button>
                <a href="<%= request.getContextPath() %>/consultation" class="btn btn-secondary">Back</a>
            </div>
        </div>
    </form>
</div>
<% } %>

<div class="back-link">
    <a href="<%= request.getContextPath() %>/consultation" class="btn btn-secondary">Back to Consultations List</a>
</div>

<script>
function toggleCompletionFields() {
    const checkbox = document.getElementById('cancelSpecialistCheckbox');
    const completionFields = document.getElementById('completionFields');
    const opinionField = document.querySelector('#completionFields textarea[name="opinion"]');
    const recommendationsField = document.querySelector('#completionFields textarea[name="recommendations"]');

    if (checkbox.checked) {
        completionFields.style.display = 'block';
        opinionField.required = true;
        recommendationsField.required = true;
    } else {
        completionFields.style.display = 'none';
        opinionField.required = false;
        recommendationsField.required = false;
    }
}

function confirmSubmission() {
    const checkbox = document.getElementById('cancelSpecialistCheckbox');

    if (checkbox.checked) {
        return confirm('Are you sure you want to cancel the specialist review and complete this consultation?\n\nThis will:\n- Remove the specialist assignment\n- Remove the appointment date\n- Recalculate the cost without specialist tariff\n- Mark the consultation as COMPLETED');
    }

    return true;
}

function confirmCompletion() {
    return confirm('Are you sure you want to submit and complete this consultation?\n\nOnce submitted, the consultation will be marked as COMPLETED.');
}
</script>
</body>
</html>
