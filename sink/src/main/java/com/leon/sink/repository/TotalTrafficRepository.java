package com.leon.sink.repository;

import com.leon.sink.model.TotalTrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface TotalTrafficRepository extends CassandraRepository<TotalTrafficData> {

    @Query("SELECT * FROM traffickeyspace.total_traffic WHERE recorddate = ?0 ALLOW FILTERING")
    Iterable<TotalTrafficData> findTrafficDataByDate(String date);

}
