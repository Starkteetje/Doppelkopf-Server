package doko.database.token;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface TokenRepository extends CrudRepository<LoginToken, Long> {

    List<LoginToken> findAll();

    List<LoginToken> findByIdIn(List<Long> ids);
}
