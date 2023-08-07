package doko.database.season;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import doko.lineup.LineUp;

@Service
public class SeasonService {

	private SeasonRepository seasonRepository;

	public List<Season> getSeasonForLineUp(String lineUpString) {
		return seasonRepository.findByLineUpString(lineUpString);
	}

	public List<Season> getSeasonForLineUp(LineUp lineUp) {
		return getSeasonForLineUp(lineUp.getLineUpString());
	}

	@Autowired
	public void setSeasonRepository(SeasonRepository seasonRepository) {
		this.seasonRepository = seasonRepository;
	}
}
