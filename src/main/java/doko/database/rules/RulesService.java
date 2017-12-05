package doko.database.rules;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.lineup.LineUp;

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

	public Optional<Rules> getRulesOfLineUp(LineUp lineUp) {
		List<Rules> allRules = getAllRules();
		return allRules.stream()
				.filter(rules -> rules.getLineUpString().equals(lineUp.getLineUpString()))
				.sorted()
				.findFirst();
	}
}
