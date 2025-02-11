package org.schoolproject.backend.controllers;//package org.schoolproject.backend.controllers;
//
//import org.schoolproject.backend.entities.User;
//import org.schoolproject.backend.services.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/users")
//
//public class UserController {
//    private final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        User createdUser = userService.createUser(user);
//        return ResponseEntity.ok(createdUser);
//    }
//
//
//    @GetMapping("/{id}")
//    public ResponseEntity<User> findUserById(@PathVariable UUID id) {
//        return userService.findUserById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//
//    @GetMapping("/email/{email}")
//    public ResponseEntity<User> findUserByEmail(@PathVariable String email) {
//        return userService.findUserByEmail(email)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping
//    public ResponseEntity<List<User>> findAllUsers() {
//        List<User> users = userService.findAllUsers();
//        return ResponseEntity.ok(users);
//    }
//
//
//    @GetMapping("/exists/{email}")
//    public ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email) {
//        return ResponseEntity.ok(userService.existsUserByEmail(email));
//    }
//
//
//    @PutMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User updatedUser) {
//        User user = userService.updateUser(id, updatedUser);
//        return ResponseEntity.ok(user);
//    }
//
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//
//    @PostMapping("/{id}/verify-password")
//    public ResponseEntity<Boolean> verifyPassword(@PathVariable UUID id, @RequestBody String password) {
//        return ResponseEntity.ok(userService.verifyPassword(id, password));
//    }
//
//
//    @PutMapping("/{id}/change-password")
//    public ResponseEntity<Void> changePassword(@PathVariable UUID id, @RequestBody String newPassword) {
//        userService.changePassword(id, newPassword);
//        return ResponseEntity.noContent().build();
//    }
//}
