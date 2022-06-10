package com.github.rodrigo_sp17.mscheduler.calendar;

import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/available")
    public ResponseEntity<CollectionModel<AppUser>> getAvailableFriends(@RequestParam
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                        Authentication auth) {
        var availableFriends = calendarService.getAvailableFriends(date,
                auth.getName());
        Link self = linkTo(methodOn(CalendarController.class).getAvailableFriends(date, null))
                .withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(availableFriends).add(self));
    }
}
