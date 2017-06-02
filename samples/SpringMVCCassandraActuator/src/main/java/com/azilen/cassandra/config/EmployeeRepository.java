package com.azilen.cassandra.config;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.azilen.cassandra.entity.Employee;

public interface EmployeeRepository extends CassandraRepository<Employee> {
	@Query("SELECT*FROM employee WHERE user=?0 LIMIT ?1")
	Iterable<Employee> findByUser(String user, Integer limit);

	@Query("SELECT*FROM employee WHERE user=?0 AND id<?1 LIMIT ?2")
	Iterable<Employee> findByUserFrom(String user, UUID from, Integer limit);
}
