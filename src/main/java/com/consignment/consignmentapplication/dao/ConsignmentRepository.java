package com.consignment.consignmentapplication.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsignmentRepository extends CrudRepository<Consignment, Integer> {

}
