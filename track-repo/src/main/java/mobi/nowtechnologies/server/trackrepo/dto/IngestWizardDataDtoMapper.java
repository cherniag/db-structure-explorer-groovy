package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.enums.DropTrackType;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.DropsData;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestData;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;

import java.util.ArrayList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;


public class IngestWizardDataDtoMapper extends IngestWizardDataDto {

    public IngestWizardDataDtoMapper(IngestWizardData data) {
        this.drops = new ArrayList<DropDto>();
        this.setSuid(data.getSuid());
        this.setStatus(data.getStatus());

        DropsData dropsData = data.getDropdata();
        if(isNotNull(dropsData) &&  isNotNull(dropsData.getDrops()))
            for (DropsData.Drop drop : dropsData.getDrops())
                this.drops.add(toDrops(drop));

    }

    private DropDto toDrops(DropsData.Drop drop) {
        DropDto result = new DropDto();
        result.setDate(drop.getDrop().getDate());
        result.setName(drop.getDrop().getName());
        result.setIngestor(drop.getIngestor().name());
        result.setSelected(drop.getSelected());

        List<IngestData.Track> tracks = drop.getIngestdata() != null ? drop.getIngestdata().getData() : null;
        if(tracks != null){
            List<DropTrackDto> dropTracks = new ArrayList<DropTrackDto>();

            for(IngestData.Track track : tracks){
                DropTrackDto dropTrackDto = new DropTrackDto();

                dropTrackDto.setArtist(track.getArtist());
                dropTrackDto.setExists(track.getExists());
                dropTrackDto.setIsrc(track.getISRC());
                dropTrackDto.setProductCode(track.getProductCode());
                dropTrackDto.setIngest(track.getIngest());
                dropTrackDto.setTitle(track.getTitle());
                dropTrackDto.setType(DropTrackType.valueOf(track.getType().name()));

                dropTracks.add(dropTrackDto);
            }
            result.setTracks(dropTracks);
        }
        return result;
    }

    public static IngestWizardData map(IngestWizardDataDto dto){
        IngestWizardData data = new IngestWizardData();
        data.setSuid(dto.getSuid());
        data.setDropdata(new DropsData());
        data.getDropdata().setDrops(new ArrayList<DropsData.Drop>());

        List<DropsData.Drop> drops = data.getDropdata().getDrops();
        List<DropDto> dropDtos = dto.getDrops();
        if(dropDtos != null){
            for (DropDto dropDto : dropDtos) {
                DropsData.Drop drop = data.getDropdata().new Drop();
                drop.setName(dropDto.getName());
                drop.setSelected(dropDto.getSelected());

                List<DropTrackDto> dropTracks = dropDto.getTracks();
                if(dropTracks != null){
                    drop.setIngestdata(new IngestData());
                    List<IngestData.Track> tracks = new ArrayList<IngestData.Track>();

                    for(DropTrackDto trackDto : dropTracks){
                        IngestData.Track track = drop.getIngestdata().new Track();

                        track.setArtist(trackDto.getArtist());
                        track.setExists(trackDto.getExists());
                        track.setISRC(trackDto.getIsrc());
                        track.setProductCode(trackDto.getProductCode());
                        track.setIngest(trackDto.getIngest());
                        track.setTitle(trackDto.getTitle());
                        track.setType(trackDto.getType() != null ? DropTrack.Type.valueOf(trackDto.getType().name()) : null);

                        tracks.add(track);
                    }

                    drop.getIngestdata().setData(tracks);
                }

                drops.add(drop);
            }
        }
        return data;
    }

}
