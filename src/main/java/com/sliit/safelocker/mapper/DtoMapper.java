package com.sliit.safelocker.mapper;

public interface DtoMapper<L,K> {


    public L mapFrom(K dto);
    public K mapTo(L object);
}
