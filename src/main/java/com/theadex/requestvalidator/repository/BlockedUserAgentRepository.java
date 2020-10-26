package com.theadex.requestvalidator.repository;

import com.theadex.requestvalidator.domain.BlockedUserAgent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface BlockedUserAgentRepository extends CrudRepository<BlockedUserAgent, String> {
    String USER_AGENT_BLACK_LIST_CACHE = "userAgentBlackList";

    @Cacheable(USER_AGENT_BLACK_LIST_CACHE)
    @Query("SELECT blockedUserAgent.userAgent FROM BlockedUserAgent blockedUserAgent")
    Set<String> getUserAgentBlackList();

    @Override
    @Transactional
    @CacheEvict(value = USER_AGENT_BLACK_LIST_CACHE, allEntries = true)
    <S extends BlockedUserAgent> S save(S entity);

    @Override
    @Transactional
    @CacheEvict(value = USER_AGENT_BLACK_LIST_CACHE, allEntries = true)
    <S extends BlockedUserAgent> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @Transactional
    @CacheEvict(value = USER_AGENT_BLACK_LIST_CACHE, allEntries = true)
    void deleteById(String s);

    @Override
    @Transactional
    @CacheEvict(value = USER_AGENT_BLACK_LIST_CACHE, allEntries = true)
    void delete(BlockedUserAgent entity);

    @Override
    @Transactional
    @CacheEvict(value = USER_AGENT_BLACK_LIST_CACHE, allEntries = true)
    void deleteAll(Iterable<? extends BlockedUserAgent> entities);

    @Override
    @Transactional
    @CacheEvict(value = USER_AGENT_BLACK_LIST_CACHE, allEntries = true)
    void deleteAll();
}
