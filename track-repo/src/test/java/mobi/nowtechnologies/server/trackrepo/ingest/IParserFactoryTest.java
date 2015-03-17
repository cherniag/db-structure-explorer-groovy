/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.trackrepo.ingest;

import org.junit.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IParserFactoryTest {

    IParserFactory iParserFactory = new IParserFactory();

    @Test
    public void shouldReturnSONYAsSONY_DDEXIngestorName() {
        //given
        Ingestor ingestor = Ingestor.SONY_DDEX;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("SONY"));
    }

    @Test
    public void shouldReturnWARNERAsWARNER_OLDIngestorName() {
        //given
        Ingestor ingestor = Ingestor.WARNER_OLD;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("WARNER"));
    }

    @Test
    public void shouldReturnUNIVERSALAsUNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13IngestorName() {
        //given
        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("UNIVERSAL"));
    }

    @Test
    public void shouldReturnSONYAsSONYIngestorName() {
        //given
        Ingestor ingestor = Ingestor.SONY;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("SONY"));
    }

    @Test
    public void shouldReturnUNIVERSALAsUNIVERSALIngestorName() {
        //given
        Ingestor ingestor = Ingestor.UNIVERSAL;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("UNIVERSAL"));
    }

    @Test
    public void shouldReturnFUGAAsFUGAIngestorName() {
        //given
        Ingestor ingestor = Ingestor.FUGA;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("FUGA"));
    }

    @Test
    public void shouldReturnIODAAsIODAIngestorName() {
        //given
        Ingestor ingestor = Ingestor.IODA;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("IODA"));
    }

    @Test
    public void shouldReturnCIAsCIIngestorName() {
        //given
        Ingestor ingestor = Ingestor.CI;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("CI"));
    }

    @Test
    public void shouldReturnMANUALAsMANUALIngestorName() {
        //given
        Ingestor ingestor = Ingestor.MANUAL;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("MANUAL"));
    }

    @Test
    public void shouldReturnWARNERAsWARNERIngestorName() {
        //given
        Ingestor ingestor = Ingestor.WARNER;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("WARNER"));
    }

    @Test
    public void shouldReturnMOSAsMOSIngestorName() {
        //given
        Ingestor ingestor = Ingestor.MOS;

        //when
        String ingestorName = iParserFactory.getName(ingestor);

        //then
        assertThat(ingestorName, is("MOS"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionOnNullIngestor() {
        iParserFactory.getName(null);
    }
}