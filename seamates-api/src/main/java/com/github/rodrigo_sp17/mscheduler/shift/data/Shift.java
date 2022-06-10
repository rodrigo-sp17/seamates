package com.github.rodrigo_sp17.mscheduler.shift.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Shift extends RepresentationModel<Shift> {

    /*
        AF:
            Period of time the friend is away at work and therefore unavailable
    *   Rep invariant:
    *       unavailabilityStartDate <= boardingDate <= leavingDate <= unavailabilityEndDate
    *       unavailabilityStartDate is NOT available
    *       unavailabilityEndDate is NOT available
    *   Safety from rep exposure:
    *       All return types are immutable
     */

    @Id
    @GeneratedValue
    private Long shiftId;

    @ManyToOne
    @JsonIgnore
    private AppUser owner;

    private LocalDate unavailabilityStartDate;

    private LocalDate boardingDate;

    private LocalDate leavingDate;

    private LocalDate unavailabilityEndDate;


    public Shift() {
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }


    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    public LocalDate getUnavailabilityStartDate() {
        return unavailabilityStartDate;
    }

    public void setUnavailabilityStartDate(LocalDate unavailabilityStartDate) {
        this.unavailabilityStartDate = unavailabilityStartDate;
    }

    public LocalDate getBoardingDate() {
        return boardingDate;
    }

    public void setBoardingDate(LocalDate boardingDate) {
        this.boardingDate = boardingDate;
    }

    public LocalDate getLeavingDate() {
        return leavingDate;
    }

    public void setLeavingDate(LocalDate leavingDate) {
        this.leavingDate = leavingDate;
    }

    public LocalDate getUnavailabilityEndDate() {
        return unavailabilityEndDate;
    }

    public void setUnavailabilityEndDate(LocalDate unavailabilityEndDate) {
        this.unavailabilityEndDate = unavailabilityEndDate;
    }
}
