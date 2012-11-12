package mobi.nowtechnologies.server.track_repo.assembler;

import java.util.LinkedList;
import java.util.List;

import mobi.nowtechnologies.server.shared.dto.FileType;
import mobi.nowtechnologies.server.track_repo.domain.AssetFile;
import mobi.nowtechnologies.server.track_repo.dto.AssetFileDto;

/**
 *
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public class FileAsm {	
	public static List<AssetFileDto> toAssetFileDtos(List<AssetFile> files) {		
		List<AssetFileDto> fileDtos = new LinkedList<AssetFileDto>();
		
		for (AssetFile track : files) {
			fileDtos.add(toAssetFileDto(track));
		}
		
		return fileDtos;
	}

	public static AssetFileDto toAssetFileDto(AssetFile file) {		
		AssetFileDto fileDto = new AssetFileDto(); 
		
		fileDto.setType(toFileType(file.getType()));
		fileDto.setMd5(file.getMd5());
		fileDto.setPath(file.getPath());
		
		return fileDto;
	}
	
	public static FileType toFileType(AssetFile.FileType fileType)
	{
		switch(fileType){
		 case DOWNLOAD:
			 return FileType.ORIGINAL_MP3;
		 case MOBILE:
			 return FileType.ORIGINAL_ACC;
		 case IMAGE:
			 return FileType.IMAGE;
		 case PREVIEW:
			 return FileType.ORIGINAL_ACC;
		}
		
		return null;
	}
}