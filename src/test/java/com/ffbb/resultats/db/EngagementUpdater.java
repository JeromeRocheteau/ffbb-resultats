package com.ffbb.resultats.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ffbb.resultats.api.Engagement;

public class EngagementUpdater extends Updater<Long> {

	private Engagement engagement;
	
	public EngagementUpdater(Engagement engagement) {
		this.engagement = engagement;
	}

	@Override
	public String getScriptPath() {
		return "/engagement-insert.sql";
	}

	@Override
	public Long getResult(int count, ResultSet resultSet) throws Exception {
		if (count > 0) {
			if (resultSet.next()) {
				return resultSet.getLong(1);
			} else {
				return 0L;	
			}
		} else {
			return 0L;
		}
	}

	@Override
	public void setParameters(PreparedStatement statement) throws Exception {
		statement.setLong(1, engagement.getOrganisation().getId());
		statement.setLong(2, engagement.getCompétition().getId());
	}
	
}