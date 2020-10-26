package com.theadex.requestvalidator.repository;

import com.theadex.requestvalidator.domain.BlockedIP;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface BlockedIpRepository extends CrudRepository<BlockedIP, Long> {
    String IP_BLACK_LIST_CACHE = "ipBlackList";

    @Cacheable(IP_BLACK_LIST_CACHE)
    @Query("SELECT blockedIP.ip FROM BlockedIP blockedIP")
    Set<Inet4Address> getIpBlackList();

    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    void deleteByIp(Inet4Address ip);

    @Override
    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    <S extends BlockedIP> S save(S entity);

    @Override
    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    <S extends BlockedIP> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    void deleteById(Long aLong);

    @Override
    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    void delete(BlockedIP entity);

    @Override
    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    void deleteAll(Iterable<? extends BlockedIP> entities);

    @Override
    @Transactional
    @CacheEvict(value = IP_BLACK_LIST_CACHE, allEntries = true)
    void deleteAll();
}
