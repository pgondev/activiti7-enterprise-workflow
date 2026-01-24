package com.enterprise.workflow.form.repository;

import com.enterprise.workflow.form.entity.FormDefinition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FormDefinition entities.
 */
@Repository
public interface FormDefinitionRepository extends JpaRepository<FormDefinition, String> {

    Optional<FormDefinition> findByKey(String key);
    
    Optional<FormDefinition> findByKeyAndVersion(String key, Integer version);
    
    @Query("SELECT f FROM FormDefinition f WHERE f.key = :key ORDER BY f.version DESC LIMIT 1")
    Optional<FormDefinition> findLatestByKey(String key);
    
    Page<FormDefinition> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<FormDefinition> findByCategory(String category, Pageable pageable);
    
    Page<FormDefinition> findByNameContainingIgnoreCaseAndCategory(
            String name, String category, Pageable pageable);
    
    List<FormDefinition> findByKeyOrderByVersionDesc(String key);
    
    @Query("SELECT f FROM FormDefinition f WHERE f.published = true AND f.key = :key ORDER BY f.version DESC LIMIT 1")
    Optional<FormDefinition> findLatestPublishedByKey(String key);
}
