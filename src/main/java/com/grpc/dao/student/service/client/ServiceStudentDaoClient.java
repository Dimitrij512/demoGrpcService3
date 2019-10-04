package com.grpc.dao.student.service.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;
import com.grpc.dao.student.service.Student;
import com.grpc.dao.student.service.StudentAgeRequest;
import com.grpc.dao.student.service.StudentDaoServiceGrpc;
import com.grpc.dao.student.service.StudentIdRequest;
import com.grpc.dao.student.service.StudentStatusRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@Component
public class ServiceStudentDaoClient {

    private StudentDaoServiceGrpc.StudentDaoServiceBlockingStub blockingStub;
    private StudentDaoServiceGrpc.StudentDaoServiceStub stub;

    public ServiceStudentDaoClient() {
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 6362).usePlaintext().build();
        blockingStub = StudentDaoServiceGrpc.newBlockingStub(channel);
        stub = StudentDaoServiceGrpc.newStub(channel);
    }

    public Student getById(String id) {
        return blockingStub.getById(StudentIdRequest.newBuilder().setId(id).build()).getStudent();
    }

    public List<Student> findByAge(int age) {
        return blockingStub.findByAge(StudentAgeRequest.newBuilder().setAge(age).build()).getStudentsList();
    }

    public List<Student> findAll() { return blockingStub.findAll(Empty.getDefaultInstance()).getStudentsList(); }

    public StreamObserver<StudentStatusRequest> findAllByStatus(StreamObserver<Student> streamObserver) {
       return  stub.findStudentByStatusStream(streamObserver);
    }

}
