package grpc.ghostcache

import grpc.ghostcache.DekCacheServiceGrpc.getServiceDescriptor
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Holder for Kotlin coroutine-based client and server APIs for grpc.ghostcache.DekCacheService.
 */
public object DekCacheServiceGrpcKt {
  public const val SERVICE_NAME: String = DekCacheServiceGrpc.SERVICE_NAME

  @JvmStatic
  public val serviceDescriptor: ServiceDescriptor
    get() = getServiceDescriptor()

  public val getDekMethod: MethodDescriptor<GetDekRequest, GetDekReply>
    @JvmStatic
    get() = DekCacheServiceGrpc.getGetDekMethod()

  public val putDekMethod: MethodDescriptor<PutDekRequest, PutDekReply>
    @JvmStatic
    get() = DekCacheServiceGrpc.getPutDekMethod()

  public val deleteDekMethod: MethodDescriptor<DeleteDekRequest, DeleteDekReply>
    @JvmStatic
    get() = DekCacheServiceGrpc.getDeleteDekMethod()

  /**
   * A stub for issuing RPCs to a(n) grpc.ghostcache.DekCacheService service as suspending
   * coroutines.
   */
  @StubFor(DekCacheServiceGrpc::class)
  public class DekCacheServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT,
  ) : AbstractCoroutineStub<DekCacheServiceCoroutineStub>(channel, callOptions) {
    override fun build(channel: Channel, callOptions: CallOptions): DekCacheServiceCoroutineStub =
        DekCacheServiceCoroutineStub(channel, callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][io.grpc.Status].  If the RPC completes with another status, a
     * corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun getDek(request: GetDekRequest, headers: Metadata = Metadata()): GetDekReply =
        unaryRpc(
      channel,
      DekCacheServiceGrpc.getGetDekMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][io.grpc.Status].  If the RPC completes with another status, a
     * corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun putDek(request: PutDekRequest, headers: Metadata = Metadata()): PutDekReply =
        unaryRpc(
      channel,
      DekCacheServiceGrpc.getPutDekMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][io.grpc.Status].  If the RPC completes with another status, a
     * corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun deleteDek(request: DeleteDekRequest, headers: Metadata = Metadata()):
        DeleteDekReply = unaryRpc(
      channel,
      DekCacheServiceGrpc.getDeleteDekMethod(),
      request,
      callOptions,
      headers
    )
  }

  /**
   * Skeletal implementation of the grpc.ghostcache.DekCacheService service based on Kotlin
   * coroutines.
   */
  public abstract class DekCacheServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for grpc.ghostcache.DekCacheService.GetDek.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException],
     * the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun getDek(request: GetDekRequest): GetDekReply = throw
        StatusException(UNIMPLEMENTED.withDescription("Method grpc.ghostcache.DekCacheService.GetDek is unimplemented"))

    /**
     * Returns the response to an RPC for grpc.ghostcache.DekCacheService.PutDek.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException],
     * the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun putDek(request: PutDekRequest): PutDekReply = throw
        StatusException(UNIMPLEMENTED.withDescription("Method grpc.ghostcache.DekCacheService.PutDek is unimplemented"))

    /**
     * Returns the response to an RPC for grpc.ghostcache.DekCacheService.DeleteDek.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException],
     * the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun deleteDek(request: DeleteDekRequest): DeleteDekReply = throw
        StatusException(UNIMPLEMENTED.withDescription("Method grpc.ghostcache.DekCacheService.DeleteDek is unimplemented"))

    final override fun bindService(): ServerServiceDefinition = builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = DekCacheServiceGrpc.getGetDekMethod(),
      implementation = ::getDek
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = DekCacheServiceGrpc.getPutDekMethod(),
      implementation = ::putDek
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = DekCacheServiceGrpc.getDeleteDekMethod(),
      implementation = ::deleteDek
    )).build()
  }
}
