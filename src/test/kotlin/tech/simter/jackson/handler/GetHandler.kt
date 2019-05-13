package cn.gftaxi.webflux.dynamicdto

import tech.simter.jackson.Example
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

/**
 * @author RJ
 */
@Component
class GetHandler @Autowired constructor() : HandlerFunction<ServerResponse> {
  override fun handle(request: ServerRequest): Mono<ServerResponse> {
    return ok().contentType(APPLICATION_JSON_UTF8).syncBody(Example.instance2minutes)
  }
}