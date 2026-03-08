package com.necklogic.sepapi.service;

import com.necklogic.sepapi.dto.AlunoResponseDTO;
import com.necklogic.sepapi.dto.AlunosLoteDTO;
import com.necklogic.sepapi.dto.TurmaAtualizarDTO;
import com.necklogic.sepapi.dto.TurmaRequestDTO;
import com.necklogic.sepapi.dto.TurmaResponseDTO;
import com.necklogic.sepapi.model.Aluno;
import com.necklogic.sepapi.model.Professor;
import com.necklogic.sepapi.model.Turma;
import com.necklogic.sepapi.repository.AlunoRepository;
import com.necklogic.sepapi.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    public Page<TurmaResponseDTO> listar(UUID professorId, Pageable pageable) {
        return turmaRepository.findAllByProfessorId(professorId, pageable)
                .map(this::mapToDTO);
    }

    public TurmaResponseDTO buscarPorId(UUID id, UUID professorId) {
        Turma turma = turmaRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapToDTO(turma);
    }

    public TurmaResponseDTO criar(TurmaRequestDTO dto, Professor professor) {
        Turma turma = Turma.builder()
                .nome(dto.nome())
                .professor(professor)
                .build();
        return mapToDTO(turmaRepository.save(turma));
    }

    public TurmaResponseDTO atualizar(UUID id, TurmaAtualizarDTO dto, UUID professorId) {
        Turma turma = turmaRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        turma.setNome(dto.nome());

        if (dto.alunoIds() != null) {
            List<Aluno> alunos = alunoRepository.findAllById(dto.alunoIds());
            boolean todosPertencemAoProfessor = alunos.stream()
                    .allMatch(a -> a.getProfessor().getId().equals(professorId));

            if (!todosPertencemAoProfessor) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            turma.getAlunos().clear();
            turma.getAlunos().addAll(alunos);
        }

        return mapToDTO(turmaRepository.save(turma));
    }

    public void deletar(UUID id, UUID professorId) {
        Turma turma = turmaRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        turmaRepository.delete(turma);
    }

    public void processarAlunosLote(UUID turmaId, AlunosLoteDTO dto, UUID professorId, boolean isAdicionar) {
        Turma turma = turmaRepository.findByIdAndProfessorId(turmaId, professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Aluno> alunos = alunoRepository.findAllById(dto.alunoIds());

        boolean todosPertencemAoProfessor = alunos.stream()
                .allMatch(a -> a.getProfessor().getId().equals(professorId));

        if (!todosPertencemAoProfessor || alunos.size() != dto.alunoIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (isAdicionar) {
            turma.getAlunos().addAll(alunos);
        } else {
            dto.alunoIds().forEach(id -> turma.getAlunos().removeIf(a -> a.getId().equals(id)));
        }

        turmaRepository.save(turma);
    }

    private TurmaResponseDTO mapToDTO(Turma turma) {
        List<AlunoResponseDTO> alunos = turma.getAlunos().stream()
                .map(a -> new AlunoResponseDTO(
                        a.getId(),
                        a.getNome(),
                        a.getMateria(),
                        a.isAtivo(),
                        a.getTipoCobranca(),
                        a.getSaldoCreditos()
                ))
                .collect(Collectors.toList());

        return new TurmaResponseDTO(turma.getId(), turma.getNome(), turma.getAlunos().size(), alunos);
    }
}