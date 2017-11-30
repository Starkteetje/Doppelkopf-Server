package doko.database.game;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface GameRepository extends CrudRepository<Game, Long> {

	List<Game> findAll();

	List<Game> findByIdIn(List<Long> ids);
}
