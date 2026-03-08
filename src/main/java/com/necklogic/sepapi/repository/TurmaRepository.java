package com.necklogic.sepapi.repository;

import com.necklogic.sepapi.model.Turma;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TurmaRepository extends JpaRepository<Turma, UUID> {

    Page<Turma> findAllByProfessorId(UUID professorId, Pageable pageable);

    Optional<Turma> findByIdAndProfessorId(UUID id, UUID professorId);

}