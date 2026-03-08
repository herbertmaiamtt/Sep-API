package com.necklogic.sepapi.controller;

import com.necklogic.sepapi.dto.AlunosLoteDTO;
import com.necklogic.sepapi.dto.TurmaRequestDTO;
import com.necklogic.sepapi.dto.TurmaResponseDTO;
import com.necklogic.sepapi.model.Professor;
import com.necklogic.sepapi.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @GetMapping
    public ResponseEntity<Page<TurmaResponseDTO>> listar(@AuthenticationPrincipal Professor professor, Pageable pageable) {
        return ResponseEntity.ok(turmaService.listar(professor.getId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> buscarPorId(@PathVariable UUID id, @AuthenticationPrincipal Professor professor) {
        return ResponseEntity.ok(turmaService.buscarPorId(id, professor.getId()));
    }

    @PostMapping
    public ResponseEntity<TurmaResponseDTO> criar(@RequestBody @Valid TurmaRequestDTO dto, @AuthenticationPrincipal Professor professor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turmaService.criar(dto, professor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> atualizar(@PathVariable UUID id, @RequestBody @Valid com.necklogic.sepapi.dto.TurmaAtualizarDTO dto, @AuthenticationPrincipal Professor professor) {
        return ResponseEntity.ok(turmaService.atualizar(id, dto, professor.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id, @AuthenticationPrincipal Professor professor) {
        turmaService.deletar(id, professor.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/alunos/adicionar")
    public ResponseEntity<Void> adicionarAlunos(@PathVariable UUID id, @RequestBody @Valid AlunosLoteDTO dto, @AuthenticationPrincipal Professor professor) {
        turmaService.processarAlunosLote(id, dto, professor.getId(), true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/alunos/remover")
    public ResponseEntity<Void> removerAlunos(@PathVariable UUID id, @RequestBody @Valid AlunosLoteDTO dto, @AuthenticationPrincipal Professor professor) {
        turmaService.processarAlunosLote(id, dto, professor.getId(), false);
        return ResponseEntity.ok().build();
    }
}