package doko.database.token;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface TokenRepository extends CrudRepository<Token, Long> {

	List<Token> findAll();

	List<Token> findByIdIn(List<Long> ids);

	List<Token> findByUserId(Long id);

	Token findOneByTokenValue(String tokenValue);
}
