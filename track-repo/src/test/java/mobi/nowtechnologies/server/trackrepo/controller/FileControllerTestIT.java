package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.junit.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @author Alexander Kolpakov (akolpakov)
public class FileControllerTestIT extends AbstractTrackRepoIT {

    @Autowired
    private FileRepository fileRepository;

    @Value("${trackRepo.encode.destination}")
    private Resource publishDir;

    @Test
    public void testFile() throws Exception {
        AssetFileFactory assetFileFactory = new AssetFileFactory();
        assetFileFactory.setFileDir(publishDir.getFile());
        AssetFile assetFile = assetFileFactory.anyAssetFile();

        AssetFile save = fileRepository.save(assetFile);

        mockMvc.perform(get("/file").param("id", String.valueOf(save.getId()))).
            andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"));
    }

    @Before
    public void setUp() throws Exception {

    }
}