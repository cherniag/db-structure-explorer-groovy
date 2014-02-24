package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import static java.io.File.*;
import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory.Ingestors.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/application-test.xml" })
public class AbsoluteParserIT {

    @Resource(name = "trackRepo.ParserFactory")
    IParserFactory parserFactory;

    AbsoluteParser absoluteParser;
    private List<DropData> dropDatas;

    @Before
    public void setUp() throws FileNotFoundException {
        absoluteParser = (AbsoluteParser) parserFactory.getParser(ABSOLUTE);
    }

    private void verifyAsOneElementDrops() {
        assertNotNull(dropDatas);
        assertThat(dropDatas.size(), is(1));

        DropData dropData = dropDatas.get(0);
        assertNotNull(dropData);

        File expectedFolder = new File(absoluteParser.getRoot()+ separator+"201307180007"+separator+"5037128203551");

        assertThat(dropData.date, is(new Date(expectedFolder.lastModified())));
        assertThat(dropData.name, is(expectedFolder.getAbsolutePath()));
    }

    private void verifyAsEmptyDrops() {
        assertNotNull(dropDatas);
        assertThat(dropDatas.size(), is(0));
    }

    @Test
    public void shouldReturnOneElementDrops() throws Exception{
        //given
        File rootFolder = new File(absoluteParser.getRoot());

        //when
        dropDatas = absoluteParser.getDrops(rootFolder, false);

        //then
        verifyAsOneElementDrops();
    }

    @Test
    public void shouldReturnEmptyDrops() throws Exception{
        //given
        File rootFolder = new File(absoluteParser.getRoot());

        //when
        dropDatas = absoluteParser.getDrops(rootFolder, true);

        //then
        verifyAsEmptyDrops();
    }

}
