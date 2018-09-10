package doko.database.round;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface RoundRepository extends CrudRepository<Round, Long> {

	List<Round> findAll();

	List<Round> findByIdIn(List<Long> ids);

	List<Round> findByUniqueGameId(String gameId);
}
