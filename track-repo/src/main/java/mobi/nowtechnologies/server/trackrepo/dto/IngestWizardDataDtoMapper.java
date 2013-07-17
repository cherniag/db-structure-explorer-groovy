package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.enums.DropTrackType;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.DropsData;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestData;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/15/13
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class IngestWizardDataDtoMapper extends IngestWizardDataDto {

    public IngestWizardDataDtoMapper(IngestWizardData data) {
        this.drops = new ArrayList<DropDto>();
        this.setSuid(data.getSuid());

        List<DropsData.Drop> drops = data.getDropdata() != null ? data.getDropdata().getDrops() : null;
        if(drops != null){
            for (DropsData.Drop drop : drops) {
                DropDto dropDto = new DropDto();
                dropDto.setDate(drop.getDrop().getDate());
                dropDto.setName(drop.getDrop().getName());
                dropDto.setSelected(drop.isSelected());

                List<IngestData.Track> tracks = drop.getIngestdata() != null ? drop.getIngestdata().getData() : null;
                if(tracks != null){
                    List<DropTrackDto> dropTracks = new ArrayList<DropTrackDto>();

                    for(IngestData.Track track : tracks){
                        DropTrackDto dropTrackDto = new DropTrackDto();

                        dropTrackDto.setArtist(track.getArtist());
                        dropTrackDto.setExists(track.isExists());
                        dropTrackDto.setIsrc(track.getISRC());
                        dropTrackDto.setProductCode(track.getProductCode());
                        dropTrackDto.setSelected(track.isIngest());
                        dropTrackDto.setTitle(track.getTitle());
                        dropTrackDto.setType(DropTrackType.valueOf(track.getType().name()));

                        dropTracks.add(dropTrackDto);
                    }

                    dropDto.setTracks(dropTracks);
                }


                this.drops.add(dropDto);
            }
        }
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
                drop.setName(drop.getDrop().getName());
                drop.setSelected(drop.isSelected());

                List<DropTrackDto> dropTracks = dropDto.getTracks();
                if(dropTracks != null){
                    drop.setIngestdata(new IngestData());
                    List<IngestData.Track> tracks = new ArrayList<IngestData.Track>();

                    for(DropTrackDto trackDto : dropTracks){
                        IngestData.Track track = drop.getIngestdata().new Track();

                        track.setArtist(trackDto.getArtist());
                        track.setExists(trackDto.isExists());
                        track.setISRC(trackDto.getIsrc());
                        track.setProductCode(trackDto.getProductCode());
                        track.setIngest(trackDto.isSelected());
                        track.setTitle(trackDto.getTitle());
                        track.setType(DropTrack.Type.valueOf(trackDto.getType().name()));

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
