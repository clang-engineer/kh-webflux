package com.hhkbdev.khwebflux.repository.search

import com.hhkbdev.khwebflux.domain.User
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import reactor.core.publisher.Flux

/**
 * Spring Data Elasticsearch repository for the User entity.
 */
interface UserSearchRepository : ReactiveElasticsearchRepository<User, Long>, UserSearchRepositoryInternal

interface UserSearchRepositoryInternal {
    fun search(query: String): Flux<User>
}

class UserSearchRepositoryInternalImpl(private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate) : UserSearchRepositoryInternal {
    override fun search(query: String): Flux<User> {
        val nativeSearchQuery = NativeSearchQuery(queryStringQuery(query))
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, User::class.java)
            .map(SearchHit<User>::getContent)
    }
}
