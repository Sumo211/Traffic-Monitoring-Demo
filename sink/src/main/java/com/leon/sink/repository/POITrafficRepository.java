package com.leon.sink.repository;

import com.leon.sink.model.POITrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface POITrafficRepository extends CassandraRepository<POITrafficData> {

}
