package com.consignment.consignmentapplication.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.consignment.consignmentapplication.dao.Consignment;
import com.consignment.consignmentapplication.dao.ConsignmentRepository;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@PreAuthorize("hasRole('ROLE_USER')")
@RestController
public class ConsignmentController {

	@Autowired
	ConsignmentRepository repo;

	@GetMapping("/consignments")
	public List<Consignment> getAllConsignment(){
		return (List<Consignment>) repo.findAll();
	}

	@PostMapping("/consignment")
	public Consignment saveConsignment(@RequestBody Consignment consignment) {
		return repo.save(consignment);
	}

	@PutMapping("/consignment/{id}")
	public Consignment updateConsignment(@PathVariable Integer id, @RequestBody Consignment consignment) {
		Consignment existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Consignment not found " + id));

		existing.setConsignmentStatus(consignment.getConsignmentStatus()!=null ? consignment.getConsignmentStatus() : existing.getConsignmentStatus());
		existing.setConsignmentCategory(consignment.getConsignmentCategory()!=null ? consignment.getConsignmentCategory() : existing.getConsignmentCategory());
		existing.setDeliveryPartner(consignment.getDeliveryPartner()!=null ? consignment.getDeliveryPartner() : existing.getDeliveryPartner());
		
		repo.save(existing);
		return existing;
	}

	@GetMapping("/consignment/{id}")
	public Consignment getConsignmentById(@PathVariable Integer id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Consignment not found " + id));
	}

	@DeleteMapping("/consignment/{id}")
	public String deleteConsignment(@PathVariable Integer id) {
		repo.deleteById(id);
		return "Consignment deleted " + id;
	}
}