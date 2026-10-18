package cibertec.grupo9.proyectodsw2grupo9.repository;

import cibertec.grupo9.proyectodsw2grupo9.model.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    boolean existsByDni(String dni);
}
