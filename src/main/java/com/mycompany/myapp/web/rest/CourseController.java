package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.dto.CourseDto;
import com.mycompany.myapp.domain.dto.CourseWithTNDto;
import com.mycompany.myapp.service.CourseService;
import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.UserDTO;
import com.netflix.discovery.converters.Auto;
import io.swagger.annotations.Api;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
@Api(value="Course Service Controller", description = "Controller for find couses information")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/api/course/findAllCourses", produces = "application/json")
    public HttpEntity<List<CourseDto>> findAllCourses(){
        
        List<CourseDto> allCourses = courseService.findAllCourses();

        return new ResponseEntity<>(allCourses, HttpStatus.OK);
    }

    @GetMapping(path = "/api/course/findAllCoursesDto", produces = "application/json")
    public HttpEntity<List<CourseDto>> findAllCoursesDto(){
        List<CourseDto> allCourses = courseService.findAllCoursesDtoFromDB();

        return new ResponseEntity<>(allCourses, HttpStatus.OK);
    }

    @GetMapping(path = "/api/course/findAllCoursesWithTNDto", produces = "application/json")
    public HttpEntity<List<CourseWithTNDto>> findAllCoursesWithTNDto(){
        List<CourseWithTNDto> allCourses = courseService.findAllCoursesDtoWithTeacherNameFromDB();

        return new ResponseEntity<>(allCourses, HttpStatus.OK);
    }

    @PostMapping(path = "/api/course/registerCourse/{courseName}", produces = "application/json")
    public HttpStatus registerCourse(@PathVariable String courseName) {
        try {
            courseService.registerCourse(courseName);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
    }

    @PostMapping(path = "/api/course/addCourse", produces = "application/json")
    public HttpStatus addCourse(@RequestBody @NotNull CourseWithTNDto course) {
        try {
            Optional<User> user = userService.getUserWithAuthoritiesByLogin(course.getTeacherName());
            long userId = 0;
            if (!user.isPresent()) {
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(UUID.randomUUID() + "@test.com");
                userDTO.setLogin(course.getTeacherName());
                User user1 = userService.createUser(userDTO);
                userId = user1.getId();
            } else {
                userId = user.get().getId();
            }
            courseService.addCourse(CourseDto.builder()
                .courseContent(course.getCourseContent())
                .courseLocation(course.getCourseLocation())
                .courseName(course.getCourseName())
                .teacherId(userId)
                .build());
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @PutMapping(path = "/api/course/updateCourse", produces = "application/json")
    public HttpStatus updateCourse(@RequestBody @NotNull CourseDto course) {
        try {
            courseService.updateCourse(course);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    @DeleteMapping(path = "/api/course/deleteCourse/{courseName}", produces = "application/json")
    public HttpStatus deleteCourse(@NotNull @PathVariable("courseName") String courseName) {
        try {
            courseService.deleteCourse(courseName);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.BAD_REQUEST;
        }
    }
}
