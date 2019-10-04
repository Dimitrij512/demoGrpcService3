package com.grpc.dao.student.service.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.lognet.springboot.grpc.GRpcService;

import com.google.protobuf.Empty;
import com.grpc.dao.student.service.Student;
import com.grpc.dao.student.service.StudentAgeRequest;
import com.grpc.dao.student.service.StudentDaoServiceGrpc;
import com.grpc.dao.student.service.StudentIdRequest;
import com.grpc.dao.student.service.StudentResponse;
import com.grpc.dao.student.service.StudentStatusRequest;
import com.grpc.dao.student.service.StudentsResponse;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import static java.util.stream.Collectors.toList;

@GRpcService
public class StudentDaoService extends StudentDaoServiceGrpc.StudentDaoServiceImplBase {

    @Override
    public void getById(final StudentIdRequest request, StreamObserver<StudentResponse> responseObserver) {
        System.out.println("Started working getById service");

        String id = request.getId();

        responseObserver.onNext(StudentResponse.newBuilder().setStudent(
            getStudentList().stream()
                .filter(student -> student.getId().equals(id))
                .findFirst().get())
            .build());

        responseObserver.onCompleted();

        System.out.println("finished work getById service");
    }

    @Override
    public void findByAge(final StudentAgeRequest request, StreamObserver<StudentsResponse> responseObserver) {
        System.out.println("Started working findByAge service");

        int age = request.getAge();
        responseObserver.onNext(StudentsResponse.newBuilder().addAllStudents(
            getStudentList().stream().filter(student -> student.getAge() == age).collect(toList()))
            .build());
        responseObserver.onCompleted();

        System.out.println("Finished work findByAge service");
    }

    @Override
    public void findAll(final Empty request, StreamObserver<StudentsResponse> responseObserver) {
        try {
            System.out.println("Start time out: "+ new Date().getTime());
            Thread.sleep(3000);
            System.out.println("End time out: " + new Date().getTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Started working findAll service");
        responseObserver.onNext(StudentsResponse.newBuilder()
            .addAllStudents(getStudentList())
            .build());
        responseObserver.onCompleted();
        System.out.println("Finished work findAll service");
    }

    @Override
    public StreamObserver<StudentStatusRequest> findStudentByStatusStream(StreamObserver<Student> responseObserver) {
        System.out.println("findStudentByStatusStream server");

        ServerCallStreamObserver<Student> streamObserver = (ServerCallStreamObserver<Student>) responseObserver;
        streamObserver.disableAutoInboundFlowControl();

        streamObserver.setOnReadyHandler(() -> {
                if (streamObserver.isReady()) {
                    streamObserver.request(1);
                }
        });

        return new StreamObserver<StudentStatusRequest>() {

            @Override public void onNext(StudentStatusRequest studentStatusRequest) {

                for (Student student : getStudentList()) {
                    if (studentStatusRequest.getStatus() == student.getActive()) {
                        responseObserver.onNext(student);
                    }
                }

                responseObserver.onCompleted();
            }

            @Override public void onError(Throwable throwable) {}
            @Override public void onCompleted() {}
        };
    }


    //test data
    List<Student> getStudentList() {
        List<Student> studentList = new ArrayList<>();

        studentList.add(Student.newBuilder().setId("1").setName("Dima").setSurname("Andrusiv").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("2").setName("Polina").setSurname("Andrusiv").setAge(32).setActive(false).build());
        studentList.add(Student.newBuilder().setId("3").setName("Zlata").setSurname("Andrusiv").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("4").setName("Ivan").setSurname("Andrusiv").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("5").setName("Yura").setSurname("Andrusiv").setAge(30).setActive(false).build());
        studentList.add(Student.newBuilder().setId("6").setName("Ruslan").setSurname("Andrusiv").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("7").setName("Alex").setSurname("Test").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("8").setName("Liybomyr").setSurname("Ivanenko").setAge(30).setActive(false).build());
        studentList.add(Student.newBuilder().setId("9").setName("Vasyl").setSurname("Andrusiv").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("10").setName("Nata").setSurname("Andrusiv").setAge(32).setActive(true).build());
        studentList.add(Student.newBuilder().setId("11").setName("Test").setSurname("Andrusiv").setAge(30).setActive(true).build());

        return studentList;
    }

}
