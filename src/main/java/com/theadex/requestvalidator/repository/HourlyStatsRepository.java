package com.theadex.requestvalidator.repository;

import com.theadex.requestvalidator.domain.Customer;
import com.theadex.requestvalidator.domain.HourlyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyStatsRepository extends JpaRepository<HourlyStats, Long> {

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE HourlyStats SET invalidCount = invalidCount + 1 "
            + "where customer_id = :customerID"
            + " and date = :date and time = :time"
    )
    int incrementInvalidCountIfExist(@Param(value = "customerID") Long customerID, @Param(value = "date") LocalDate date,
                                     @Param(value = "time") LocalTime time);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE HourlyStats SET requestCount = requestCount + 1 "
            + "where customer_id = :customerID"
            + " and date = :date and time = :time"
    )
    int incrementRequestCountIfExist(@Param(value = "customerID") Long customerID, @Param(value = "date") LocalDate date,
                                     @Param(value = "time") LocalTime time);

    Optional<HourlyStats> findByCustomerAndDateAndTime(Customer customer, LocalDate date, LocalTime time);

    List<HourlyStats> findByCustomerAndDate(Customer customer, LocalDate date);

}
