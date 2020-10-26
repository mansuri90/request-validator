package com.theadex.requestvalidator.domain;

import com.theadex.requestvalidator.convertor.Inet4AddressToIntegerConvertor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.Inet4Address;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "ip_blacklist")
public class BlockedIP {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Convert(converter = Inet4AddressToIntegerConvertor.class)
    @Column(unique = true)
    private Inet4Address ip;

}
