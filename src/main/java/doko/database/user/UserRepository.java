package doko.database.user;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    List<User> findByIdIn(List<Long> ids);
    
    User findOne(Long id);
}

