package com.fourctc.tuyendungthongminh_be.repository;

import com.fourctc.tuyendungthongminh_be.entity.SavedJob;
import com.fourctc.tuyendungthongminh_be.entity.User;
import com.fourctc.tuyendungthongminh_be.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedJobRepository extends JpaRepository<SavedJob, UUID> {
    List<SavedJob> findByUser(User user);
    Optional<SavedJob> findByUserAndJob(User user, Job job);
}