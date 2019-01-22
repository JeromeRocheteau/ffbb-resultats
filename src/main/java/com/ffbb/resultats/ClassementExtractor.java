package com.ffbb.resultats;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ffbb.resultats.api.Championnat;
import com.ffbb.resultats.api.Classement;
import com.ffbb.resultats.api.Équipe;
import com.ffbb.resultats.api.Organisation;

public class ClassementExtractor extends AbstractExtractor<Classement> {
		
	private Équipe équipe;
	
	public Classement doExtract(Organisation organisation, Championnat championnat) throws Exception {
		this.équipe = new Équipe(organisation, championnat);
		this.doBind(Équipe.class, équipe.getURI(), équipe);
		return this.doExtract(équipe.getURI());
	}

	public Classement doExtract(URI uri) throws Exception {
		Document doc = this.getDocument(uri);
		Element iframe = doc.getElementById("idIframeClassements");
		String link = iframe.attr("src").substring(3);
		URI u = URI.create("http://resultats.ffbb.com/championnat/" + link);
		this.getClassement(u);
		return équipe.getClassement();
	}
	
	private void getClassement(URI uri) throws Exception {
		Document doc = this.getDocument(uri);
		Element table = doc.select("table.liste").first();
		Elements rows = table.select("tr");
		for (Element row : rows) {
			if (row.attr("class").endsWith("altern-2")) {
				Elements cols = row.select("td");
				if (cols.size() == 18) {
					Integer rang = Integer.valueOf(cols.get(0).text());
					Équipe équipe = this.getÉquipe(cols.get(1));
					if (équipe == null) {
						throw new Exception("Impossible de récupérer l'équipe " + cols.get(1) + " à partir du classement " + uri);
					}
					Integer victoires = Integer.valueOf(cols.get(4).text());
					Integer défaites = Integer.valueOf(cols.get(5).text());
					Integer pour = Integer.valueOf(cols.get(15).text());
					Integer contre = Integer.valueOf(cols.get(16).text());
					Classement classement = new Classement(équipe, rang, victoires, défaites, pour, contre);
					if (équipe != null && équipe.getURI().equals(this.équipe.getURI())) {
						this.équipe.setClassement(classement);
					}
				}
			}
		}
	}

	private Équipe getÉquipe(Element element) throws Exception {
		Element anchor = element.select("a").first();
		String name = anchor.text();
		String link = anchor.attr("href");
		link = link.substring(link.lastIndexOf('/') + 1);
		URI uri = URI.create("http://resultats.ffbb.com/championnat/equipe/" + link);
		if (this.doFind(Équipe.class, uri) == null) {
			String code = this.getCode(link);
			Organisation organisation = new OrganisationExtractor().doExtract(code);
			if (organisation == null) {
				throw new Exception("Impossible de récupérer l'organisation " + code + " pour l'équipe " + uri);
			}
			Équipe equipe = new Équipe(organisation, this.équipe.getCompétition());
			equipe.setDénomination(name);
			this.doBind(Équipe.class, equipe.getURI(), equipe);
			return equipe;
		} else {
			Équipe équipe = this.doFind(Équipe.class, uri);
			équipe.setDénomination(name);
			return équipe;
		}
	}
	
	private String getCode(String link) throws Exception {
		Pattern pattern = Pattern.compile("(.*)\\.html\\?r=(.*)&p=(.*)&d=(.*)");
		Matcher matcher = pattern.matcher(link);
		if (matcher.matches() && matcher.groupCount() == 4) {
			String code = matcher.group(1);
			return code;
		} else {
			throw new Exception();
		}
	}
	
}