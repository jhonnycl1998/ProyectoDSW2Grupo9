package cibertec.grupo9.proyectodsw2grupo9.service.impl;


import cibertec.grupo9.proyectodsw2grupo9.exception.BusinessException;
import cibertec.grupo9.proyectodsw2grupo9.exception.ResourceNotFoundException;
import cibertec.grupo9.proyectodsw2grupo9.mapper.AppMapper;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.PersonaDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.UsuarioDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Persona;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Rol;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Usuario;
import cibertec.grupo9.proyectodsw2grupo9.repository.RolRepository;
import cibertec.grupo9.proyectodsw2grupo9.repository.UsuarioRepository;
import cibertec.grupo9.proyectodsw2grupo9.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AppMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> Boolean.TRUE.equals(u.getEstado()))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioDTO obtenerPorId(Long id) {
        return mapper.toDto(buscar(id));
    }

    @Override
    @Transactional
    public UsuarioDTO registrar(UsuarioDTO dto) {
        if(usuarioRepository.existsByUsername(dto.getUsername())){
            throw new BusinessException("El nombre de usuario ya está registrado");
        }

        if(usuarioRepository.existsByPersona_Dni(dto.getPersona().getDni())){
            throw new BusinessException("El DNI ya está registrado");
        }

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(()-> new ResourceNotFoundException("Rol no encontrado con id " + dto.getIdRol()));

        Usuario usuario = mapper.toEntity(dto);
        usuario.setIdUsuario(null);

        Persona persona = mapper.toEntity(dto.getPersona());
        persona.setIdPersona(null);

        usuario.setPersona(persona);
        usuario.setRol(rol);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEstado(true);

        return mapper.toDto(usuarioRepository.save(usuario));

    }

    @Override
    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = buscar(id);

        if (!usuario.getUsername().equals(dto.getUsername())
                && usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("El nombre de usuario ya está registrado");
        }

        Persona persona = usuario.getPersona();
        PersonaDTO nuevaPersona = dto.getPersona();

        if (!persona.getDni().equals(nuevaPersona.getDni())
                && usuarioRepository.existsByPersona_Dni(nuevaPersona.getDni())) {
            throw new BusinessException("El DNI ya está registrado");
        }
        usuario.setUsername(dto.getUsername());

        persona.setNombres(nuevaPersona.getNombres());
        persona.setApellidos(nuevaPersona.getApellidos());
        persona.setDni(nuevaPersona.getDni());
        persona.setCorreo(nuevaPersona.getCorreo());
        persona.setTelefono(nuevaPersona.getTelefono());

        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rol no encontrado con id " + dto.getIdRol()));

        usuario.setRol(rol);

        // Si no se envía una contraseña nueva, se conserva la actual.
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }

        return mapper.toDto(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);

    }

    private Usuario buscar(Long id){
        return usuarioRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }
}
