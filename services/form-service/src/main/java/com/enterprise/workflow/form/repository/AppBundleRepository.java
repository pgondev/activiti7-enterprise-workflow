package com.enterprise.workflow.form.repository;

import com.enterprise.workflow.form.entity.AppBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AppBundle entities
 */
@Repository
public interface AppBundleRepository extends JpaRepository<AppBundle, String> {

    /**
     * Find bundle by unique key
     */
    Optional<AppBundle> findByKey(String key);

    /**
     * Find bundles by status
     */
    List<AppBundle> findByStatus(AppBundle.BundleStatus status);

    /**
     * Find all bundles by category
     */
    List<AppBundle> findByCategory(String category);

    /**
     * Find bundles by key pattern (for versioning)
     */
    List<AppBundle> findByKeyStartingWith(String keyPrefix);

    /**
     * Check if bundle with key exists
     */
    boolean existsByKey(String key);

    /**
     * Find latest version of a bundle by key prefix
     * Useful for finding the latest version when multiple versions exist
     */
    @Query("SELECT b FROM AppBundle b WHERE b.key LIKE ?1% ORDER BY b.createdAt DESC")
    List<AppBundle> findLatestByKeyPrefix(String keyPrefix);

    /**
     * Find all deployed bundles
     */
    @Query("SELECT b FROM AppBundle b WHERE b.status = 'DEPLOYED' ORDER BY b.deployedAt DESC")
    List<AppBundle> findAllDeployed();
}
