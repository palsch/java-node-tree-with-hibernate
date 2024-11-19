package com.example.demo.repository;

import com.example.demo.AleAntrag;
import com.example.demo.controller.dto.AleAntragMetadataDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AleAntragRepository extends JpaRepository<AleAntrag, UUID> {

    @Query("SELECT new com.example.demo.controller.dto.AleAntragMetadataDto(a.id, a.metadaten.status, a.updatedAt, 0) FROM AleAntrag a WHERE a.metadaten.ownerUserId = :ownerUserId")
    List<AleAntragMetadataDto> findAllByOwnerUserId(String ownerUserId);


}
