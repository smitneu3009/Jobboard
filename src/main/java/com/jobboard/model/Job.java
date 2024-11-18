package com.jobboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "Job")
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Job title is required")
    private String title;

    @Column(name = "pay_per_hour", nullable = false)
    @NotNull(message = "Pay per hour is required")
    private BigDecimal payPerHour;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPayPerHour() {
		return payPerHour;
	}

	public void setPayPerHour(BigDecimal payPerHour) {
		this.payPerHour = payPerHour;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "location", nullable = false)
    @NotBlank(message = "Location is required")
    private String location;

    @Column(name = "job_type", nullable = false)
    @NotBlank(message = "Job type is required")
    private String jobType; // FULL_TIME or PART_TIME

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank(message = "Job description is required")
    private String description;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Getters and Setters
}