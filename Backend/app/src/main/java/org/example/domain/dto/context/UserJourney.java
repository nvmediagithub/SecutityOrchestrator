package org.example.domain.dto.context;

import java.util.List;

/**
 * DTO representing a user journey through the system
 */
public class UserJourney {
    private String journeyId;
    private String journeyName;
    private String description;
    private List<String> steps;
    private List<String> touchpoints;
    private String userPersona;
    private String startPoint;
    private String endPoint;
    private String status;
    private Double satisfactionScore;
    private List<String> painPoints;
    private List<String> successFactors;

    public UserJourney() {}

    public UserJourney(String journeyId, String journeyName) {
        this.journeyId = journeyId;
        this.journeyName = journeyName;
    }

    // Getters and Setters
    public String getJourneyId() { return journeyId; }
    public void setJourneyId(String journeyId) { this.journeyId = journeyId; }

    public String getJourneyName() { return journeyName; }
    public void setJourneyName(String journeyName) { this.journeyName = journeyName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getTouchpoints() { return touchpoints; }
    public void setTouchpoints(List<String> touchpoints) { this.touchpoints = touchpoints; }

    public String getUserPersona() { return userPersona; }
    public void setUserPersona(String userPersona) { this.userPersona = userPersona; }

    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }

    public String getEndPoint() { return endPoint; }
    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(Double satisfactionScore) { this.satisfactionScore = satisfactionScore; }

    public List<String> getPainPoints() { return painPoints; }
    public void setPainPoints(List<String> painPoints) { this.painPoints = painPoints; }

    public List<String> getSuccessFactors() { return successFactors; }
    public void setSuccessFactors(List<String> successFactors) { this.successFactors = successFactors; }
}