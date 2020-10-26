package com.theadex.requestvalidator.convertor;

import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

@Converter
public class Inet4AddressToIntegerConvertor implements AttributeConverter<Inet4Address, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Inet4Address inet4Address) {
        if (inet4Address == null) {
            return null;
        } else {
            return ByteBuffer.wrap(inet4Address.getAddress()).getInt();
        }
    }

    @Override
    @SneakyThrows(UnknownHostException.class)
    public Inet4Address convertToEntityAttribute(Integer integer) {
        if (integer == null) {
            return null;
        } else {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(integer);
            return (Inet4Address) InetAddress.getByAddress(buffer.array());
        }
    }
}
