package com.github.rodrigo_sp17.mscheduler.friend;

import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequest;
import com.github.rodrigo_sp17.mscheduler.friend.data.FriendRequestDTO;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import com.github.rodrigo_sp17.mscheduler.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AppUser>> getFriends(Authentication auth) {
        var friends = friendService.getFriendsByUser(auth.getName());
        Link self = linkTo(methodOn(FriendController.class).getFriends(null)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(friends).add(self));
    }

    @GetMapping("/request")
    public ResponseEntity<CollectionModel<FriendRequestDTO>> getFriendRequests(Authentication auth) {
        var requests = friendService.getFriendRequestsForUser(auth.getName());
        var dtos = requests.stream().map(this::dtoFromRequest)
                .collect(Collectors.toList());
        Link self = linkTo(methodOn(FriendController.class).getFriendRequests(null))
                .withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(dtos).add(self));
    }

    @GetMapping("/request/{id}")
    public ResponseEntity<FriendRequestDTO> getFriendRequestById(@PathVariable Long id,
                                                              Authentication auth) {
    FriendRequest request = friendService.getRequestById(id, auth.getName());
    var dto = dtoFromRequest(request);
    dto.add(linkTo(methodOn(FriendController.class)
            .getFriendRequestById(id, null)).withSelfRel());
    dto.add(linkTo(methodOn(FriendController.class).getFriendRequests(null))
            .withRel("allRequests"));
    return ResponseEntity.ok(dto);
    }

    @PostMapping("/request")
    public ResponseEntity<FriendRequestDTO> requestFriendship(@RequestParam String username,
                                                           Authentication auth) {
        // ensures user is not requesting to be friends with self
        if (username.equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user cannot befriend himself!");
        }

        FriendRequest request = null;
        try {
            request = friendService.requestFriendship(username, auth.getName());
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        var dto = dtoFromRequest(request);
        Link toCreated = linkTo(methodOn(FriendController.class)
                .getFriendRequestById(request.getId(), null)).withSelfRel();
        dto.add(linkTo(methodOn(FriendController.class).getFriendRequests(null))
                .withRel("allRequests"));
        return ResponseEntity.created(toCreated.toUri()).body(dto);
    }

    @PostMapping("/accept")
    public ResponseEntity<AppUser> acceptFriendship(@RequestParam String username,
                                                    Authentication auth) {
        var acceptedUser = friendService.acceptFriendship(username, auth.getName());
        acceptedUser.add(linkTo(methodOn(FriendController.class).getFriends(null))
                .withRel("allFriends"));
        acceptedUser.add(linkTo(methodOn(FriendController.class).getFriendRequests(null))
                .withRel("allRequests"));
        return ResponseEntity.ok(acceptedUser);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<AppUser> removeFriendByUsername(@RequestParam String username,
                                                 Authentication auth) {
        friendService.removeFriendByUsername(username, auth.getName());
        Link allFriends = linkTo(methodOn(FriendController.class).getFriends(null))
                .withRel("allFriends");
        return ResponseEntity.noContent().header("Link", allFriends.getHref()).build();
    }

    private FriendRequestDTO dtoFromRequest(FriendRequest req) {
        var dto = new FriendRequestDTO();
        dto.setId(req.getId());
        dto.setTimestamp(req.getTimestamp());
        dto.setSourceUsername(req.getSource().getUserInfo().getUsername());
        dto.setSourceName(req.getSource().getUserInfo().getName());
        dto.setTargetUsername(req.getTarget().getUserInfo().getUsername());
        dto.setTargetName(req.getTarget().getUserInfo().getName());

        return dto;
    }
}
