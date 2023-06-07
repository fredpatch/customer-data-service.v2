package net.fred.customerdataservice.web;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import net.fred.customerdataservice.entities.Customer;
import net.fred.customerdataservice.mappers.CustomerMapper;
import net.fred.customerdataservice.repository.CustomerRepository;
import net.fred.customerdataservice.stub.CustomerServiceGrpc;
import net.fred.customerdataservice.stub.CustomerServiceOuterClass;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Override
    public void getAllCustomers(CustomerServiceOuterClass.GetAllCustomersRequest request, StreamObserver<CustomerServiceOuterClass.GetCustomersResponse> responseObserver) {

        //Customer entities
        List <Customer> customerList =customerRepository.findAll();

        List<CustomerServiceOuterClass.Customer> grpcCustomers =
                customerList.stream()
                        .map(customerMapper::fromCustomer).collect(Collectors.toList());
        //customer grpc
        List <CustomerServiceOuterClass.Customer> customers = new ArrayList<>();

        CustomerServiceOuterClass.GetCustomersResponse customersResponse =
                CustomerServiceOuterClass.GetCustomersResponse.newBuilder()
                        .addAllCustomers(grpcCustomers)
                        .build();

        responseObserver.onNext(customersResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getCustomerById(CustomerServiceOuterClass.GetCustomersByIdRequest request, StreamObserver<CustomerServiceOuterClass.GetCustomersByIdResponse> responseObserver) {
        Customer customerEntity = customerRepository.findById(request.getCustomerId()).get();

        CustomerServiceOuterClass.GetCustomersByIdResponse response =
                CustomerServiceOuterClass.GetCustomersByIdResponse.newBuilder()
                        .setCustomer(customerMapper.fromCustomer(customerEntity))
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void saveCustomer(CustomerServiceOuterClass.SaveCustomerRequest request, StreamObserver<CustomerServiceOuterClass.SaveCustomerResponse> responseObserver) {

        CustomerServiceOuterClass.CustomerRequest customerRequest = request.getCustomer();
        Customer customer = customerMapper.fromGrpcCustomerRequest(customerRequest);

        Customer savedCustomer = customerRepository.save(customer);

        CustomerServiceOuterClass.SaveCustomerResponse response =
                CustomerServiceOuterClass.SaveCustomerResponse.newBuilder()
                        .setCustomer(customerMapper.fromCustomer(savedCustomer))
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
