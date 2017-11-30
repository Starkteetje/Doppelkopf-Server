package doko.database.rules;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RulesService {

	private RulesRepository rulesRepository;

	public List<Rules> getAllRules() {
		return rulesRepository.findAll();
	}

	@Autowired
	public void setRulesRepository(RulesRepository rulesRepository) {
		this.rulesRepository = rulesRepository;
	}
}
