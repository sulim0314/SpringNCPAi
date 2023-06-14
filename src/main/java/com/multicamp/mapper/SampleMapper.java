package com.multicamp.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SampleMapper {

	int getTableCount();
}
