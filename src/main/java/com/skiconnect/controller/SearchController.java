// Controller for search functionality (e.g., GET /search)

package com.skiconnect.controller;

import com.skiconnect.model.LessonAvailability;
import com.skiconnect.model.School;
import com.skiconnect.model.Teacher;
import com.skiconnect.service.LessonAvailabilityService;
import com.skiconnect.service.SchoolService;
import com.skiconnect.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Search", description = "Search APIs")
public class SearchController {

    private final SchoolService schoolService;
    private final TeacherService teacherService;
    private final LessonAvailabilityService lessonAvailabilityService;

    @Autowired
    public SearchController(SchoolService schoolService, 
                           TeacherService teacherService,
                           LessonAvailabilityService lessonAvailabilityService) {
        this.schoolService = schoolService;
        this.teacherService = teacherService;
        this.lessonAvailabilityService = lessonAvailabilityService;
    }

    @GetMapping
    @Operation(summary = "Search schools and teachers by place", description = "Search schools and teachers by place")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results with CVs",
                    content = @Content(schema = @Schema(implementation = SearchResponse.class)))
    })
    public ResponseEntity<SearchResponse> searchByPlace(
            @Parameter(description = "Place ID") @RequestParam Long placeId,
            @Parameter(description = "Type (schools, teachers)") @RequestParam(required = false) String type) {
        
        SearchResponse response = new SearchResponse();
        
        if (type == null || "schools".equals(type)) {
            List<School> schools = schoolService.findByPlaceId(placeId);
            List<SchoolResponse> schoolResponses = schools.stream()
                    .map(this::mapToSchoolResponse)
                    .collect(Collectors.toList());
            response.setSchools(schoolResponses);
            response.setTotalSchools(schoolResponses.size());
        }
        
        if (type == null || "teachers".equals(type)) {
            List<Teacher> teachers = teacherService.findByPlaceId(placeId);
            List<TeacherResponse> teacherResponses = teachers.stream()
                    .map(this::mapToTeacherResponse)
                    .collect(Collectors.toList());
            response.setTeachers(teacherResponses);
            response.setTotalTeachers(teacherResponses.size());
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private SchoolResponse mapToSchoolResponse(School school) {
        SchoolResponse response = new SchoolResponse();
        response.setId(school.getId());
        response.setName(school.getName());
        response.setEmail(school.getUser().getEmail());
        
        // Count available lessons
        List<LessonAvailability> availabilities = lessonAvailabilityService.findBySchoolId(school.getId());
        long availableCount = availabilities.stream()
                .filter(LessonAvailability::isAvailable)
                .count();
        response.setAvailableLessons((int) availableCount);
        
        return response;
    }

    private TeacherResponse mapToTeacherResponse(Teacher teacher) {
        TeacherResponse response = new TeacherResponse();
        response.setId(teacher.getId());
        response.setName(teacher.getName());
        response.setEmail(teacher.getUser().getEmail());
        
        if (teacher.getSchool() != null) {
            response.setSchoolId(teacher.getSchool().getId());
        }
        
        // Count available lessons
        List<LessonAvailability> availabilities = lessonAvailabilityService.findByTeacherId(teacher.getId());
        long availableCount = availabilities.stream()
                .filter(LessonAvailability::isAvailable)
                .count();
        response.setAvailableLessons((int) availableCount);
        
        return response;
    }

    // Response classes
    public static class SearchResponse {
        private List<SchoolResponse> schools;
        private List<TeacherResponse> teachers;
        private Integer totalSchools;
        private Integer totalTeachers;

        public List<SchoolResponse> getSchools() {
            return schools;
        }

        public void setSchools(List<SchoolResponse> schools) {
            this.schools = schools;
        }

        public List<TeacherResponse> getTeachers() {
            return teachers;
        }

        public void setTeachers(List<TeacherResponse> teachers) {
            this.teachers = teachers;
        }

        public Integer getTotalSchools() {
            return totalSchools;
        }

        public void setTotalSchools(Integer totalSchools) {
            this.totalSchools = totalSchools;
        }

        public Integer getTotalTeachers() {
            return totalTeachers;
        }

        public void setTotalTeachers(Integer totalTeachers) {
            this.totalTeachers = totalTeachers;
        }
    }

    public static class SchoolResponse {
        private Long id;
        private String name;
        private String email;
        private Integer availableLessons;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAvailableLessons() {
            return availableLessons;
        }

        public void setAvailableLessons(Integer availableLessons) {
            this.availableLessons = availableLessons;
        }
    }

    public static class TeacherResponse {
        private Long id;
        private String name;
        private String email;
        private Long schoolId;
        private Integer availableLessons;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Long getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(Long schoolId) {
            this.schoolId = schoolId;
        }

        public Integer getAvailableLessons() {
            return availableLessons;
        }

        public void setAvailableLessons(Integer availableLessons) {
            this.availableLessons = availableLessons;
        }
    }
}
