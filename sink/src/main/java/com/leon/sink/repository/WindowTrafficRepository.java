package com.leon.sink.repository;

import com.leon.sink.model.WindowTrafficData;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface WindowTrafficRepository extends CassandraRepository<WindowTrafficData> {

    @Query("SELECT * FROM traffickeyspace.window_traffic WHERE recorddate = ?0 ALLOW FILTERING")
    Iterable<WindowTrafficData> findTrafficDataByDate(String date);

}
