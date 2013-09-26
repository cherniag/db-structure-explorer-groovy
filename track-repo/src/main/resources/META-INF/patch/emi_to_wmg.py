#!/usr/bin/python

import MySQLdb

# Open a file
fo = open("emi_to_wmg.csv", "r+")
print "Name of the file: ", fo.name

# Open database connection
db = MySQLdb.connect("rage.musicqubed.com","root","Cha3t5N0w1","cn_cms" )

# prepare a cursor object using cursor() method
cursor = db.cursor()

# execute SQL query using execute() method.
cursor.execute("SELECT VERSION()")

# Fetch a single row using fetchone() method.
data = cursor.fetchone()

print "Database version : %s " % data

print "start cleaning emi clone tracks..."
sql = ("update cn_cms.Track t join cn_service.tb_media m on m.trackId = t.id and t.emi_track_id is not null set m.trackId = t.emi_track_id")
      
cursor.execute(sql)

sql = ("delete from cn_cms.Track where emi_track_id is not null")
      
cursor.execute(sql)
print "cleaning emi clone tracks is completed"

print "start cloning emi tracks..."
lines = fo.readlines()
li = 0
for line in lines:
   items = line.split(";")
   oldProductCode = items[0]
   if len(items) >= 2:
       li+=1
       newProductCode = items[1]
       artist = items[2].replace("\'","\\\'")
       title = items[3].split()[0].replace("\'","\\\'")
        
       sql_suffix = "from Track t where t.productCode='"+oldProductCode+"' and t.artist='"+artist+"' and t.title='"+title+"'";
       
       sql_select = ("SELECT "
            "id, "
            "'WARNER', "
            "ISRC, "
            "Title, "
            "Artist, "
            "ProductId, "
            "ProductCode, "
            "Genre, "
            "Copyright, "
            "YEAR, "
            "Album, "
            "Xml, "
            "IngestionDate, "
            "IngestionUpdateDate, "
            "PublishDate, "
            "Info, "
            "SubTitle, "
            "Licensed, "
            "status, "
            "resolution, "
            "itunesUrl, "
            "explicit, "
            "label, "
            "releaseDate, "
            "territoryCodes, "
            "coverFile, "
            "mediaFile, "
            "mediaType ")
       
       sql_insert = ("INSERT INTO cn_cms.Track("
                "emi_track_id, "
                "Ingestor, "
                "ISRC, "
                "Title, "
                "Artist, "
                "ProductId, "
                "ProductCode, "
                "Genre, "
                "Copyright, "
                "YEAR, "
                "Album, "
                "Xml, "
                "IngestionDate, "
                "IngestionUpdateDate, "
                "PublishDate, "
                "Info, "
                "SubTitle, "
                "Licensed, "
                "status, "
                "resolution, "
                "itunesUrl, "
                "explicit, "
                "label, "
                "releaseDate, "
                "territoryCodes, "
                "coverFile, "
                "mediaFile, "
                "mediaType) ")
                  
       sql = sql_insert + sql_select + sql_suffix
               
       cursor.execute(sql)
       
       # Print found tracks
       if cursor.rowcount > 0:
            #print "%s" % (sql)
            print '###%d emiProductCode=%s; wmgProductCode=%s; artist=%s; title=%s' % (li,oldProductCode, newProductCode, artist, title)

print "cloning emi tracks is completed"

#update tb_media           
print "start updating tb_media..."
sql = ("update cn_service.tb_media m JOIN cn_cms.Track t ON m.trackId = t.emi_track_id SET m.trackId = t.id") 
      
cursor.execute(sql)
print "updating tb_media is completed"

#update to correct publishDate
print "start updating publish date..."           
sql = ("update cn_cms.Track track set track.PublishDate='2013-09-01' where track.emi_track_id is not null")
      
cursor.execute(sql)
print "updating publish date is completed"
      
# disconnect from server
db.close()

# Close opend file
fo.close()
