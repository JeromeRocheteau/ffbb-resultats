package com.ffbb.resultats.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import com.ffbb.resultats.api.Organisation;

public class OrganisationUpdater extends Updater<Organisation, Boolean> {

	@Override
	public String getScriptPath() {
		return "/organisation-insert.sql";
	}

	@Override
	public Boolean getResult(int count, ResultSet resultSet) throws Exception {
		if (count > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public void setParameters(PreparedStatement statement) throws Exception {
		Organisation organisation = this.getObject();
		statement.setString(1, organisation.getCode());
		statement.setString(2, organisation.getType().name());
		statement.setString(3, organisation.getFfbb());
		statement.setString(4, organisation.getNom());
		if (organisation.getSalle() == null) {
			statement.setNull(5, Types.BIGINT);
		} else {
			statement.setLong(5, organisation.getSalle().getId());			
		}
	}
	
}