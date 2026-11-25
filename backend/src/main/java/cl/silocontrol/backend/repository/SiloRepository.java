
package cl.silocontrol.backend.repository;

import cl.silocontrol.backend.model.Silo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiloRepository extends JpaRepository<Silo, String> {
}
