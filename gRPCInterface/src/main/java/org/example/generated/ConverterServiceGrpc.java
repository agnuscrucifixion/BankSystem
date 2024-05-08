package org.example.generated;/*
package org.example.generated;

import static io.grpc.MethodDescriptor.generateFullMethodName;

*/
/**
 *//*

@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: converter.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class ConverterServiceGrpc {

  private ConverterServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "converter.ConverterService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<Converter.ConvertRequest,
      Converter.ConvertResponse> getConvertMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Convert",
      requestType = Converter.ConvertRequest.class,
      responseType = Converter.ConvertResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<Converter.ConvertRequest,
      Converter.ConvertResponse> getConvertMethod() {
    io.grpc.MethodDescriptor<Converter.ConvertRequest, Converter.ConvertResponse> getConvertMethod;
    if ((getConvertMethod = ConverterServiceGrpc.getConvertMethod) == null) {
      synchronized (ConverterServiceGrpc.class) {
        if ((getConvertMethod = ConverterServiceGrpc.getConvertMethod) == null) {
          ConverterServiceGrpc.getConvertMethod = getConvertMethod =
              io.grpc.MethodDescriptor.<Converter.ConvertRequest, Converter.ConvertResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Convert"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Converter.ConvertRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  Converter.ConvertResponse.getDefaultInstance()))
              .setSchemaDescriptor(new ConverterServiceMethodDescriptorSupplier("Convert"))
              .build();
        }
      }
    }
    return getConvertMethod;
  }

  */
/**
   * Creates a new async stub that supports all call types for the service
   *//*

  public static ConverterServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConverterServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConverterServiceStub>() {
        @java.lang.Override
        public ConverterServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConverterServiceStub(channel, callOptions);
        }
      };
    return ConverterServiceStub.newStub(factory, channel);
  }

  */
/**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   *//*

  public static ConverterServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConverterServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConverterServiceBlockingStub>() {
        @java.lang.Override
        public ConverterServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConverterServiceBlockingStub(channel, callOptions);
        }
      };
    return ConverterServiceBlockingStub.newStub(factory, channel);
  }

  */
/**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   *//*

  public static ConverterServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<ConverterServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<ConverterServiceFutureStub>() {
        @java.lang.Override
        public ConverterServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new ConverterServiceFutureStub(channel, callOptions);
        }
      };
    return ConverterServiceFutureStub.newStub(factory, channel);
  }

  */
/**
   *//*

  public interface AsyncService {

    */
/**
     *//*

    default void convert(Converter.ConvertRequest request,
                         io.grpc.stub.StreamObserver<Converter.ConvertResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getConvertMethod(), responseObserver);
    }
  }

  */
/**
   * Base class for the server implementation of the service ConverterService.
   *//*

  public static abstract class ConverterServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return ConverterServiceGrpc.bindService(this);
    }
  }

  */
/**
   * A stub to allow clients to do asynchronous rpc calls to service ConverterService.
   *//*

  public static final class ConverterServiceStub
      extends io.grpc.stub.AbstractAsyncStub<ConverterServiceStub> {
    private ConverterServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConverterServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConverterServiceStub(channel, callOptions);
    }

    */
/**
     *//*

    public void convert(Converter.ConvertRequest request,
                        io.grpc.stub.StreamObserver<Converter.ConvertResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getConvertMethod(), getCallOptions()), request, responseObserver);
    }
  }

  */
/**
   * A stub to allow clients to do synchronous rpc calls to service ConverterService.
   *//*

  public static final class ConverterServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<ConverterServiceBlockingStub> {
    private ConverterServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConverterServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConverterServiceBlockingStub(channel, callOptions);
    }

    */
/**
     *//*

    public Converter.ConvertResponse convert(Converter.ConvertRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getConvertMethod(), getCallOptions(), request);
    }
  }

  */
/**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service ConverterService.
   *//*

  public static final class ConverterServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<ConverterServiceFutureStub> {
    private ConverterServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ConverterServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new ConverterServiceFutureStub(channel, callOptions);
    }

    */
/**
     *//*

    public com.google.common.util.concurrent.ListenableFuture<Converter.ConvertResponse> convert(
        Converter.ConvertRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getConvertMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CONVERT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CONVERT:
          serviceImpl.convert((Converter.ConvertRequest) request,
              (io.grpc.stub.StreamObserver<Converter.ConvertResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getConvertMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              Converter.ConvertRequest,
              Converter.ConvertResponse>(
                service, METHODID_CONVERT)))
        .build();
  }

  private static abstract class ConverterServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ConverterServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return Converter.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ConverterService");
    }
  }

  private static final class ConverterServiceFileDescriptorSupplier
      extends ConverterServiceBaseDescriptorSupplier {
    ConverterServiceFileDescriptorSupplier() {}
  }

  private static final class ConverterServiceMethodDescriptorSupplier
      extends ConverterServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    ConverterServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ConverterServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ConverterServiceFileDescriptorSupplier())
              .addMethod(getConvertMethod())
              .build();
        }
      }
    }
    return result;
  }
}
*/
