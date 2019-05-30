import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { LoginModalService, Principal, Account } from 'app/core';
import { CourseService } from 'app/shared/service/CourseService';
import { CourseDto } from 'app/shared/model/course-dto.model';
import { CourseWithTNDto } from 'app/shared/model/courseWithTN-dto.model';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.css']
})
export class HomeComponent implements OnInit {
    course: CourseWithTNDto = {
        courseName: '',
        courseContent: '',
        courseLocation: '',
        teacherName: ''
    };

    courseUpdate: CourseDto = {
        courseName: '',
        courseContent: '',
        courseLocation: '',
        courseTeacher: ''
    };

    account: Account;
    modalRef: NgbModalRef;
    classeNameNeedToReg: string;

    constructor(
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private courseService: CourseService
    ) {}

    courses: CourseDto[] = [];

    coursesWithTN: CourseWithTNDto[] = [];

    ngOnInit() {
        this.principal.identity().then(account => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    getAllCourses() {
        this.courseService.getCourseInfo().subscribe(curDto => {
            if (!curDto) {
                this.courses = [];
            } else {
                this.courses = curDto;
            }
        });
    }

    getAllCoursesWithTN() {
        this.courseService.getCourseInfoWithTN().subscribe(curDto => {
            if (!curDto) {
                this.coursesWithTN = [];
            } else {
                this.coursesWithTN = curDto;
            }
        });
    }

    registerCourse() {
        this.courseService.add(this.course).subscribe(response => {
            if (response.toString().localeCompare('OK') == 0) {
                alert('Added Successfully');
            } else {
                alert('Failure to Add');
            }
        });
    }

    clearAllCourses() {
        this.courses = [];
    }

    clearAllCourseTN() {
        this.coursesWithTN = [];
    }
    update() {
        this.courseService.update(this.courseUpdate).subscribe(response => {
            if (response.toString().localeCompare('OK') == 0) {
                alert('Updated Successfully');
            } else {
                alert('Failure to Update');
            }
        });
    }

    deleteCourse(course) {
        let index = this.courses.indexOf(course, 0);
        if (index > -1) {
            this.courses.splice(index, 1);
        }
        this.courseService.delete(course.courseName).subscribe(response => {
            if (response.toString().localeCompare('OK') == 0) {
                alert('Deleted Successfully');
            } else {
                alert('Failure to Delete');
            }
        });
    }
}
