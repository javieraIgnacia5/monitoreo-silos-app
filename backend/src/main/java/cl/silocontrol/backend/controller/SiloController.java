
package cl.silocontrol.backend.controller;

import cl.silocontrol.backend.model.Silo;
import cl.silocontrol.backend.repository.SiloRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/silos")
public class SiloController {

    private final SiloRepository repository;

    public SiloController(SiloRepository repository) {
        this.repository = repository;
    }

    // Obtener todos los silos
    @GetMapping
    public List<Silo> listar() {
        return repository.findAll();
    }

    // Buscar un silo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Silo> buscar(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un silo
    @PostMapping
    public ResponseEntity<Silo> crear(@RequestBody Silo silo) {
        if (silo.getId() == null || silo.getId().isBlank()
                || repository.existsById(silo.getId())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(201).body(repository.save(silo));
    }

    // Actualizar un silo
    @PutMapping("/{id}")
    public ResponseEntity<Silo> actualizar(
            @PathVariable String id,
            @RequestBody Silo datos) {

        return repository.findById(id).map(silo -> {
            silo.setName(datos.getName());
            silo.setGrain(datos.getGrain());
            silo.setFarm(datos.getFarm());
            silo.setCapacityTon(datos.getCapacityTon());
            silo.setCurrentTon(datos.getCurrentTon());
            silo.setUpdatedAt(datos.getUpdatedAt());
            silo.setLocationAddress(datos.getLocationAddress());
            silo.setLatitude(datos.getLatitude());
            silo.setLongitude(datos.getLongitude());

            return ResponseEntity.ok(repository.save(silo));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminar un silo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
