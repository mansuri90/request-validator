package com.theadex.requestvalidator.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "hourly_stats",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_id", "date", "time"})})
public class HourlyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDate date;

    private LocalTime time;

    @Column(name = "request_count", nullable = false)
    private Long requestCount;

    @Column(name = "invalid_count", nullable = false)
    private Long invalidCount;

}
