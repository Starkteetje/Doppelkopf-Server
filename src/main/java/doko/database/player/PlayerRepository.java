package doko.database.player;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface PlayerRepository extends CrudRepository<Player, Long> {

	List<Player> findAll();

	Player findOne(Long id);

	List<Player> findByIdIn(List<Long> ids);

	Player findOneByName(String playerName);
}
