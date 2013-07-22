package mobi.nowtechnologies.server.trackrepo.ingest;

import java.util.List;

public class DropsData {

	public class Drop {
		private String name;
		private Boolean selected = false;
		private IParser parser;
		private IParserFactory.Ingestors ingestor;
		private DropData drop;
		private IngestData ingestdata;


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getSelected() {
			return selected;
		}

		public void setSelected(Boolean selected) {
			this.selected = selected;
		}

		public IParser getParser() {
			return parser;
		}

		public void setParser(IParser parser) {
			this.parser = parser;
		}

		public DropData getDrop() {
			return drop;
		}

		public void setDrop(DropData drop) {
			this.drop = drop;
		}

		public IParserFactory.Ingestors getIngestor() {
			return ingestor;
		}

		public void setIngestor(IParserFactory.Ingestors ingestor) {
			this.ingestor = ingestor;
		}

		public IngestData getIngestdata() {
			return ingestdata;
		}

		public void setIngestdata(IngestData ingestdata) {
			this.ingestdata = ingestdata;
		}
		
	}

	private List<Drop> drops;


	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

}
