package grpc.ghostcache

import grpc.ghostcache.SessionServiceGrpc.getServiceDescriptor
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
 * Holder for Kotlin coroutine-based client and server APIs for grpc.ghostcache.SessionService.
 */
public object SessionServiceGrpcKt {
  public const val SERVICE_NAME: String = SessionServiceGrpc.SERVICE_NAME

  @JvmStatic
  public val serviceDescriptor: ServiceDescriptor
    get() = getServiceDescriptor()

  public val getSessionMethod: MethodDescriptor<GetSessionRequest, GetSessionReply>
    @JvmStatic
    get() = SessionServiceGrpc.getGetSessionMethod()

  public val deleteSessionMethod: MethodDescriptor<DeleteSessionRequest, DeleteSessionReply>
    @JvmStatic
    get() = SessionServiceGrpc.getDeleteSessionMethod()

  public val putSessionMethod: MethodDescriptor<PutSessionRequest, PutSessionReply>
    @JvmStatic
    get() = SessionServiceGrpc.getPutSessionMethod()

  /**
   * A stub for issuing RPCs to a(n) grpc.ghostcache.SessionService service as suspending
   * coroutines.
   */
  @StubFor(SessionServiceGrpc::class)
  public class SessionServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT,
  ) : AbstractCoroutineStub<SessionServiceCoroutineStub>(channel, callOptions) {
    override fun build(channel: Channel, callOptions: CallOptions): SessionServiceCoroutineStub =
        SessionServiceCoroutineStub(channel, callOptions)

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
    public suspend fun getSession(request: GetSessionRequest, headers: Metadata = Metadata()):
        GetSessionReply = unaryRpc(
      channel,
      SessionServiceGrpc.getGetSessionMethod(),
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
    public suspend fun deleteSession(request: DeleteSessionRequest, headers: Metadata = Metadata()):
        DeleteSessionReply = unaryRpc(
      channel,
      SessionServiceGrpc.getDeleteSessionMethod(),
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
    public suspend fun putSession(request: PutSessionRequest, headers: Metadata = Metadata()):
        PutSessionReply = unaryRpc(
      channel,
      SessionServiceGrpc.getPutSessionMethod(),
      request,
      callOptions,
      headers
    )
  }

  /**
   * Skeletal implementation of the grpc.ghostcache.SessionService service based on Kotlin
   * coroutines.
   */
  public abstract class SessionServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for grpc.ghostcache.SessionService.GetSession.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException],
     * the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun getSession(request: GetSessionRequest): GetSessionReply = throw
        StatusException(UNIMPLEMENTED.withDescription("Method grpc.ghostcache.SessionService.GetSession is unimplemented"))

    /**
     * Returns the response to an RPC for grpc.ghostcache.SessionService.DeleteSession.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException],
     * the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun deleteSession(request: DeleteSessionRequest): DeleteSessionReply = throw
        StatusException(UNIMPLEMENTED.withDescription("Method grpc.ghostcache.SessionService.DeleteSession is unimplemented"))

    /**
     * Returns the response to an RPC for grpc.ghostcache.SessionService.PutSession.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [io.grpc.Status].  If this method fails with a [java.util.concurrent.CancellationException],
     * the RPC will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun putSession(request: PutSessionRequest): PutSessionReply = throw
        StatusException(UNIMPLEMENTED.withDescription("Method grpc.ghostcache.SessionService.PutSession is unimplemented"))

    final override fun bindService(): ServerServiceDefinition = builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SessionServiceGrpc.getGetSessionMethod(),
      implementation = ::getSession
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SessionServiceGrpc.getDeleteSessionMethod(),
      implementation = ::deleteSession
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = SessionServiceGrpc.getPutSessionMethod(),
      implementation = ::putSession
    )).build()
  }
}
