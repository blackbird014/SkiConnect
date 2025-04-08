// Controller for managing lessons (e.g., POST /lessons, GET /lessons)

package com.skiconnect.controller;

import com.skiconnect.model.LessonAvailability;
import com.skiconnect.service.LessonAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lessons")
@Tag(name = "Lesson", description = "Lesson management APIs")
public class LessonController {

    private final LessonAvailabilityService lessonAvailabilityService;

    @Autowired
    public LessonController(LessonAvailabilityService lessonAvailabilityService) {
        this.lessonAvailabilityService = lessonAvailabilityService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SKI_SCHOOL')")
    @Operation(summary = "Create lesson availability", description = "Create lesson availability (ski_school only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lesson created",
                    content = @Content(schema = @Schema(implementation = LessonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Ski school access required")
    })
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonRequest request) {
        LessonAvailability availability = lessonAvailabilityService.createAvailability(
                request.getTeacherId(),
                request.getSchoolId(),
                request.getPlaceId(),
                request.getDate(),
                request.getDate().plusMinutes(request.getDurationMinutes())
        );
        
        return new ResponseEntity<>(mapToLessonResponse(availability), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List lessons", description = "List lessons (filtered by role in future)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of lessons",
                    content = @Content(schema = @Schema(implementation = LessonListResponse.class)))
    })
    public ResponseEntity<LessonListResponse> listLessons(
            @Parameter(description = "School ID") @RequestParam(required = false) Long schoolId,
            @Parameter(description = "Teacher ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Place ID") @RequestParam(required = false) Long placeId,
            @Parameter(description = "Status (available, booked)") @RequestParam(required = false) String status) {
        
        List<LessonAvailability> availabilities = new ArrayList<>();
        
        if (schoolId != null) {
            availabilities = lessonAvailabilityService.findBySchoolId(schoolId);
        } else if (teacherId != null) {
            availabilities = lessonAvailabilityService.findByTeacherId(teacherId);
        } else if (placeId != null) {
            availabilities = lessonAvailabilityService.findByPlaceId(placeId);
        } else if (status != null) {
            boolean isAvailable = "available".equals(status);
            availabilities = lessonAvailabilityService.findByIsAvailable(isAvailable);
        } else {
            availabilities = lessonAvailabilityService.findAll();
        }
        
        List<LessonResponse> lessonResponses = availabilities.stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
        
        LessonListResponse response = new LessonListResponse();
        response.setLessons(lessonResponses);
        response.setTotal(lessonResponses.size());
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{lessonId}/book")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_STUDENT_GROUP')")
    @Operation(summary = "Book a lesson", description = "Book a lesson (student/student_group)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson booked",
                    content = @Content(schema = @Schema(implementation = LessonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Student access required"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    public ResponseEntity<LessonResponse> bookLesson(
            @Parameter(description = "Lesson ID") @PathVariable Long lessonId,
            @RequestBody BookLessonRequest request) {
        
        LessonAvailability availability = lessonAvailabilityService.bookLesson(lessonId, request.getStudentId());
        
        return new ResponseEntity<>(mapToLessonResponse(availability), HttpStatus.OK);
    }

    private LessonResponse mapToLessonResponse(LessonAvailability availability) {
        LessonResponse response = new LessonResponse();
        response.setId(availability.getId());
        response.setSchoolId(availability.getSchool().getId());
        response.setTeacherId(availability.getTeacher().getId());
        response.setPlaceId(availability.getPlace().getId());
        response.setDate(availability.getStartTime());
        response.setDurationMinutes((int) java.time.Duration.between(
                availability.getStartTime(), availability.getEndTime()).toMinutes());
        response.setStatus(availability.isAvailable() ? "available" : "booked");
        
        if (availability.getStudent() != null) {
            response.setStudentId(availability.getStudent().getId());
        }
        
        return response;
    }

    // Request and Response classes
    public static class LessonRequest {
        private Long schoolId;
        private Long teacherId;
        private Long placeId;
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime date;
        private Integer durationMinutes;

        public Long getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public Long getPlaceId() {
            return placeId;
        }

        public void setPlaceId(Long placeId) {
            this.placeId = placeId;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
        }
    }

    public static class LessonResponse {
        private Long id;
        private Long schoolId;
        private Long teacherId;
        private Long placeId;
        private Long studentId;
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime date;
        private String status;
        private Integer durationMinutes;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        public Long getPlaceId() {
            return placeId;
        }

        public void setPlaceId(Long placeId) {
            this.placeId = placeId;
        }

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
        }
    }

    public static class LessonListResponse {
        private List<LessonResponse> lessons;
        private Integer total;

        public List<LessonResponse> getLessons() {
            return lessons;
        }

        public void setLessons(List<LessonResponse> lessons) {
            this.lessons = lessons;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }

    public static class BookLessonRequest {
        private Long studentId;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }
    }
}
