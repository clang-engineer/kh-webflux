package com.hhkbdev.khwebflux.web.rest

import com.hhkbdev.khwebflux.repository.search.UserSearchRepository
import com.hhkbdev.khwebflux.service.UserService
import com.hhkbdev.khwebflux.service.dto.UserDTO
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.jhipster.web.util.PaginationUtil

@RestController
@RequestMapping("/api")
class PublicUserResource(
    private val userSearchRepository: UserSearchRepository,
    private val userService: UserService
) {
    companion object {
        private val ALLOWED_ORDERED_PROPERTIES = arrayOf("id", "login", "firstName", "lastName", "email", "activated", "langKey")
    }

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * {@code GET /users} : get all users with only the public informations - calling this are allowed for anyone.
     *
     * @param request a {@link ServerHttpRequest} request.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    fun getAllPublicUsers(request: ServerHttpRequest, @org.springdoc.api.annotations.ParameterObject pageable: Pageable): Mono<ResponseEntity<Flux<UserDTO>>> {
        log.debug("REST request to get all public User names")
        if (!onlyContainsAllowedProperties(pageable)) {
            return Mono.just(ResponseEntity.badRequest().build())
        }

        return userService.countManagedUsers()
            .map { PageImpl<UserDTO>(listOf(), pageable, it) }
            .map { PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), it) }
            .map { ResponseEntity.ok().headers(it).body(userService.getAllPublicUsers(pageable)) }
    }
    private fun onlyContainsAllowedProperties(pageable: Pageable) =
        pageable.sort.map(Sort.Order::getProperty).all(ALLOWED_ORDERED_PROPERTIES::contains)

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    fun getAuthorities() = userService.getAuthorities().collectList()

    /**
     * {@code SEARCH /_search/users/:query} : search for the User corresponding to the query.
     *
     * @param query the query to search.
     * @return the result of the search.
     */
    @GetMapping("/_search/users/{query}")
    fun search(@PathVariable query: String): Mono<MutableList<UserDTO>> {

        return userSearchRepository.search(query).map { UserDTO(it) }.collectList()
    }
}
