package com.theadex.requestvalidator.cacheserializer;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.theadex.requestvalidator.domain.Customer;

import java.io.IOException;

public class CustomerSerializer implements StreamSerializer<Customer> {

    @Override
    public void write(ObjectDataOutput out, Customer customer) throws IOException {
        out.writeLong(customer.getId());
        out.writeUTF(customer.getName());
        out.writeBoolean(customer.isActive());
    }

    @Override
    public Customer read(ObjectDataInput in) throws IOException {
        return Customer.builder()
                .id(in.readLong())
                .name(in.readUTF())
                .active(in.readBoolean())
                .build();
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public void destroy() {
        //there is no resource to clear
    }
}
