package grpc.ghostcache;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 **
 * DekCacheService stores wrapped DEKs (output of Key Vault `wrapKey()`).
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.56.0)",
    comments = "Source: dek.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DekCacheServiceGrpc {

  private DekCacheServiceGrpc() {}

  public static final String SERVICE_NAME = "grpc.ghostcache.DekCacheService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.ghostcache.GetDekRequest,
      grpc.ghostcache.GetDekReply> getGetDekMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetDek",
      requestType = grpc.ghostcache.GetDekRequest.class,
      responseType = grpc.ghostcache.GetDekReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.ghostcache.GetDekRequest,
      grpc.ghostcache.GetDekReply> getGetDekMethod() {
    io.grpc.MethodDescriptor<grpc.ghostcache.GetDekRequest, grpc.ghostcache.GetDekReply> getGetDekMethod;
    if ((getGetDekMethod = DekCacheServiceGrpc.getGetDekMethod) == null) {
      synchronized (DekCacheServiceGrpc.class) {
        if ((getGetDekMethod = DekCacheServiceGrpc.getGetDekMethod) == null) {
          DekCacheServiceGrpc.getGetDekMethod = getGetDekMethod =
              io.grpc.MethodDescriptor.<grpc.ghostcache.GetDekRequest, grpc.ghostcache.GetDekReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetDek"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.ghostcache.GetDekRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.ghostcache.GetDekReply.getDefaultInstance()))
              .setSchemaDescriptor(new DekCacheServiceMethodDescriptorSupplier("GetDek"))
              .build();
        }
      }
    }
    return getGetDekMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.ghostcache.PutDekRequest,
      grpc.ghostcache.PutDekReply> getPutDekMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PutDek",
      requestType = grpc.ghostcache.PutDekRequest.class,
      responseType = grpc.ghostcache.PutDekReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.ghostcache.PutDekRequest,
      grpc.ghostcache.PutDekReply> getPutDekMethod() {
    io.grpc.MethodDescriptor<grpc.ghostcache.PutDekRequest, grpc.ghostcache.PutDekReply> getPutDekMethod;
    if ((getPutDekMethod = DekCacheServiceGrpc.getPutDekMethod) == null) {
      synchronized (DekCacheServiceGrpc.class) {
        if ((getPutDekMethod = DekCacheServiceGrpc.getPutDekMethod) == null) {
          DekCacheServiceGrpc.getPutDekMethod = getPutDekMethod =
              io.grpc.MethodDescriptor.<grpc.ghostcache.PutDekRequest, grpc.ghostcache.PutDekReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PutDek"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.ghostcache.PutDekRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.ghostcache.PutDekReply.getDefaultInstance()))
              .setSchemaDescriptor(new DekCacheServiceMethodDescriptorSupplier("PutDek"))
              .build();
        }
      }
    }
    return getPutDekMethod;
  }

  private static volatile io.grpc.MethodDescriptor<grpc.ghostcache.DeleteDekRequest,
      grpc.ghostcache.DeleteDekReply> getDeleteDekMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteDek",
      requestType = grpc.ghostcache.DeleteDekRequest.class,
      responseType = grpc.ghostcache.DeleteDekReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<grpc.ghostcache.DeleteDekRequest,
      grpc.ghostcache.DeleteDekReply> getDeleteDekMethod() {
    io.grpc.MethodDescriptor<grpc.ghostcache.DeleteDekRequest, grpc.ghostcache.DeleteDekReply> getDeleteDekMethod;
    if ((getDeleteDekMethod = DekCacheServiceGrpc.getDeleteDekMethod) == null) {
      synchronized (DekCacheServiceGrpc.class) {
        if ((getDeleteDekMethod = DekCacheServiceGrpc.getDeleteDekMethod) == null) {
          DekCacheServiceGrpc.getDeleteDekMethod = getDeleteDekMethod =
              io.grpc.MethodDescriptor.<grpc.ghostcache.DeleteDekRequest, grpc.ghostcache.DeleteDekReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteDek"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.ghostcache.DeleteDekRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.ghostcache.DeleteDekReply.getDefaultInstance()))
              .setSchemaDescriptor(new DekCacheServiceMethodDescriptorSupplier("DeleteDek"))
              .build();
        }
      }
    }
    return getDeleteDekMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DekCacheServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DekCacheServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DekCacheServiceStub>() {
        @java.lang.Override
        public DekCacheServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DekCacheServiceStub(channel, callOptions);
        }
      };
    return DekCacheServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DekCacheServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DekCacheServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DekCacheServiceBlockingStub>() {
        @java.lang.Override
        public DekCacheServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DekCacheServiceBlockingStub(channel, callOptions);
        }
      };
    return DekCacheServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DekCacheServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DekCacheServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DekCacheServiceFutureStub>() {
        @java.lang.Override
        public DekCacheServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DekCacheServiceFutureStub(channel, callOptions);
        }
      };
    return DekCacheServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   **
   * DekCacheService stores wrapped DEKs (output of Key Vault `wrapKey()`).
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void getDek(grpc.ghostcache.GetDekRequest request,
        io.grpc.stub.StreamObserver<grpc.ghostcache.GetDekReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetDekMethod(), responseObserver);
    }

    /**
     */
    default void putDek(grpc.ghostcache.PutDekRequest request,
        io.grpc.stub.StreamObserver<grpc.ghostcache.PutDekReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPutDekMethod(), responseObserver);
    }

    /**
     */
    default void deleteDek(grpc.ghostcache.DeleteDekRequest request,
        io.grpc.stub.StreamObserver<grpc.ghostcache.DeleteDekReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteDekMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service DekCacheService.
   * <pre>
   **
   * DekCacheService stores wrapped DEKs (output of Key Vault `wrapKey()`).
   * </pre>
   */
  public static abstract class DekCacheServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return DekCacheServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service DekCacheService.
   * <pre>
   **
   * DekCacheService stores wrapped DEKs (output of Key Vault `wrapKey()`).
   * </pre>
   */
  public static final class DekCacheServiceStub
      extends io.grpc.stub.AbstractAsyncStub<DekCacheServiceStub> {
    private DekCacheServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DekCacheServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DekCacheServiceStub(channel, callOptions);
    }

    /**
     */
    public void getDek(grpc.ghostcache.GetDekRequest request,
        io.grpc.stub.StreamObserver<grpc.ghostcache.GetDekReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetDekMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void putDek(grpc.ghostcache.PutDekRequest request,
        io.grpc.stub.StreamObserver<grpc.ghostcache.PutDekReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPutDekMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteDek(grpc.ghostcache.DeleteDekRequest request,
        io.grpc.stub.StreamObserver<grpc.ghostcache.DeleteDekReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteDekMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service DekCacheService.
   * <pre>
   **
   * DekCacheService stores wrapped DEKs (output of Key Vault `wrapKey()`).
   * </pre>
   */
  public static final class DekCacheServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DekCacheServiceBlockingStub> {
    private DekCacheServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DekCacheServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DekCacheServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public grpc.ghostcache.GetDekReply getDek(grpc.ghostcache.GetDekRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetDekMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.ghostcache.PutDekReply putDek(grpc.ghostcache.PutDekRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPutDekMethod(), getCallOptions(), request);
    }

    /**
     */
    public grpc.ghostcache.DeleteDekReply deleteDek(grpc.ghostcache.DeleteDekRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteDekMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service DekCacheService.
   * <pre>
   **
   * DekCacheService stores wrapped DEKs (output of Key Vault `wrapKey()`).
   * </pre>
   */
  public static final class DekCacheServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<DekCacheServiceFutureStub> {
    private DekCacheServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DekCacheServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DekCacheServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.ghostcache.GetDekReply> getDek(
        grpc.ghostcache.GetDekRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetDekMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.ghostcache.PutDekReply> putDek(
        grpc.ghostcache.PutDekRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPutDekMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<grpc.ghostcache.DeleteDekReply> deleteDek(
        grpc.ghostcache.DeleteDekRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteDekMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_DEK = 0;
  private static final int METHODID_PUT_DEK = 1;
  private static final int METHODID_DELETE_DEK = 2;

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
        case METHODID_GET_DEK:
          serviceImpl.getDek((grpc.ghostcache.GetDekRequest) request,
              (io.grpc.stub.StreamObserver<grpc.ghostcache.GetDekReply>) responseObserver);
          break;
        case METHODID_PUT_DEK:
          serviceImpl.putDek((grpc.ghostcache.PutDekRequest) request,
              (io.grpc.stub.StreamObserver<grpc.ghostcache.PutDekReply>) responseObserver);
          break;
        case METHODID_DELETE_DEK:
          serviceImpl.deleteDek((grpc.ghostcache.DeleteDekRequest) request,
              (io.grpc.stub.StreamObserver<grpc.ghostcache.DeleteDekReply>) responseObserver);
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
          getGetDekMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              grpc.ghostcache.GetDekRequest,
              grpc.ghostcache.GetDekReply>(
                service, METHODID_GET_DEK)))
        .addMethod(
          getPutDekMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              grpc.ghostcache.PutDekRequest,
              grpc.ghostcache.PutDekReply>(
                service, METHODID_PUT_DEK)))
        .addMethod(
          getDeleteDekMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              grpc.ghostcache.DeleteDekRequest,
              grpc.ghostcache.DeleteDekReply>(
                service, METHODID_DELETE_DEK)))
        .build();
  }

  private static abstract class DekCacheServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DekCacheServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.ghostcache.Dek.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DekCacheService");
    }
  }

  private static final class DekCacheServiceFileDescriptorSupplier
      extends DekCacheServiceBaseDescriptorSupplier {
    DekCacheServiceFileDescriptorSupplier() {}
  }

  private static final class DekCacheServiceMethodDescriptorSupplier
      extends DekCacheServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DekCacheServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DekCacheServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DekCacheServiceFileDescriptorSupplier())
              .addMethod(getGetDekMethod())
              .addMethod(getPutDekMethod())
              .addMethod(getDeleteDekMethod())
              .build();
        }
      }
    }
    return result;
  }
}
