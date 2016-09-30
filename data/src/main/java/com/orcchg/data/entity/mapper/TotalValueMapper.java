package com.orcchg.data.entity.mapper;

import com.domain.model.TotalValue;
import com.domain.model.mapper.Mapper;
import com.orcchg.data.entity.TotalValueEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TotalValueMapper implements Mapper<TotalValueEntity, TotalValue> {

    @Inject
    TotalValueMapper() {
    }

    @Override
    public TotalValue map(TotalValueEntity object) {
        return new TotalValue.Builder(object.getValue()).build();
    }

    @Override
    public List<TotalValue> map(List<TotalValueEntity> list) {
        List<TotalValue> mapped = new ArrayList<>();
        for (TotalValueEntity entity : list) {
            mapped.add(map(entity));
        }
        return mapped;
    }
}
