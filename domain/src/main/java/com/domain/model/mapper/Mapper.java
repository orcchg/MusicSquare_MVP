package com.domain.model.mapper;

import java.util.List;

public interface Mapper<From, To> {
    To map(From object);
    List<To> map(List<From> list);
}
