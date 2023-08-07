package doko.database.season;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface SeasonRepository extends CrudRepository<Season, Long> {

	List<Season> findAll();

	List<Season> findByLineUpString(String lineUpString);
}
