package com.theadex.requestvalidator.repository;

import com.theadex.requestvalidator.domain.Customer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    String CUSTOMERS_CACHE = "customers";

    @Cacheable(value = CUSTOMERS_CACHE, key = "#id")
    Optional<Customer> findById(Long id);

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS_CACHE, key = "#entity.id", condition = "#entity.id!=null")
    <S extends Customer> S save(S entity);

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS_CACHE, allEntries = true)
    <S extends Customer> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS_CACHE, key = "#id", condition = "#id!=null")
    void deleteById(Long id);

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS_CACHE, key = "#entity.id", condition = "#entity.id!=null")
    void delete(Customer entity);

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS_CACHE, allEntries = true)
    void deleteAll(Iterable<? extends Customer> entities);

    @Override
    @Transactional
    @CacheEvict(value = CUSTOMERS_CACHE, allEntries = true)
    void deleteAll();
}
