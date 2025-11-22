package vn.ean.legia.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.ean.legia.model.Transaction;
import vn.ean.legia.model.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);

    boolean existsByThirdId(Long thirdId);

    Page<Transaction> findByUserAndNoteContainingIgnoreCase(User user, String note, Pageable pageable);

    Page<Transaction> findByUser(User user, Pageable pageable);
}
