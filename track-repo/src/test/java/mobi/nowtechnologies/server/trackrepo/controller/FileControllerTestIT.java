package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.factory.AssetFileFactory;
import mobi.nowtechnologies.server.trackrepo.repository.FileRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class FileControllerTestIT extends AbstractTrackRepoITTest {

    @Autowired
    private FileRepository fileRepository;

    @Value("${trackRepo.encode.destination}")
    private Resource publishDir;

    @Test
    public void testFile() throws Exception {
        mockMvc.perform(get("/file").param("id", "1")).
                andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"));
    }

    @Before
    public void setUp() throws Exception {
        AssetFileFactory assetFileFactory = new AssetFileFactory();
        assetFileFactory.setFileDir(publishDir.getFile());

        AssetFile assetFile = assetFileFactory.anyAssetFile();


        fileRepository.save(assetFile);
    }
}