package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDtoMapper;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;
import mobi.nowtechnologies.server.trackrepo.service.IngestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IngestTracksWizardController extends AbstractCommonController {

	protected static final Log LOG = LogFactory.getLog(IngestTracksWizardController.class);
	private IngestService ingestService;

	public void setIngestService(IngestService ingestService) {
		this.ingestService = ingestService;
	}

    @RequestMapping(value = "/drops", method = RequestMethod.GET)
    public @ResponseBody IngestWizardDataDto getDrops() throws Exception {

		return new IngestWizardDataDtoMapper(ingestService.getDrops(null));
	}

    @RequestMapping(value = "/drops/select", method = RequestMethod.POST)
	protected @ResponseBody IngestWizardDataDto selectDrops(@ModelAttribute(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO) IngestWizardDataDto dto)
			throws Exception {

		IngestWizardData data = IngestWizardDataDtoMapper.map(dto);

		return new IngestWizardDataDtoMapper(ingestService.selectDrops(data));
	}

    @RequestMapping(value = "/drops/tracks/select", method = RequestMethod.POST)
	protected @ResponseBody IngestWizardDataDto selectDropTracks(@ModelAttribute(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO) IngestWizardDataDto dto)
			throws Exception {

		IngestWizardData data = IngestWizardDataDtoMapper.map(dto);

		return new IngestWizardDataDtoMapper(ingestService.selectDropTracks(data));
	}

    @RequestMapping(value = "/drops/commit", method = RequestMethod.POST)
	protected @ResponseBody Boolean commitDrops(@ModelAttribute(IngestWizardDataDto.INGEST_WIZARD_DATA_DTO) IngestWizardDataDto dto)
			throws Exception {

		IngestWizardData data = IngestWizardDataDtoMapper.map(dto);

		return ingestService.commitDrops(data);
	}
}