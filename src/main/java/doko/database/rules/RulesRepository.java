package doko.database.rules;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface RulesRepository extends CrudRepository<Rules, Long> {

	List<Rules> findAll();

	List<Rules> findByIdIn(List<Long> ids);
}
