insert into Track (id, Artist, Title, ISRC, IngestionDate, Status, Ingestor) values (1, "Test Artist", "Test Title", "Test ISCR", "2011-03-04", "NONE", "Test Ingestor")
insert into AssetFile (id, path, type, TrackId) values (1, "c:/HOME/ChartsNow/v3.5-Impetuous_Zebra/dev/server/track-repo/src/test/resources/publish/image/APPCAST_cover.png", 2, 1)