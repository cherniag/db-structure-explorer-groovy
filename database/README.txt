To generate the snapshot of the database run the following command
(you'll need to download liquibase distribution for this):
    ./liquibase --driver=com.mysql.jdbc.Driver --classpath=mysql.jar --url=jdbc:mysql://cucumber.musicqubed.com:3306/cn_service_at --username=app --password=yohGu9ag --changeLogFile=init-empty-db.xml generateChangeLog
This snapshot is a file with change sets which are creating the empty database from scratch.

For the first time when you want to prepare the database for liquibase run
the following command:
    mvn dependency:unpack resources:resources compile liquibase:changelogSync -Denv=cucumber -Dliquibase.propertyProviderClass=mobi.nowtechnologies.database.jasypt.DecryptableProperties -DjasyptPassword=secret

Then, every time and again you want to update the database run
the following command:
    mvn dependency:unpack resources:resources compile liquibase:update -Denv=cucumber -Dliquibase.propertyProviderClass=mobi.nowtechnologies.database.jasypt.DecryptableProperties -DjasyptPassword=secret

To run a specific liquibase migration run the following command:
    mvn dependency:unpack resources:resources compile liquibase:update -Dliquibase.changeLogFile=[PATH_TO_CHANGELOG] -Denv=cucumber -Dliquibase.propertyProviderClass=mobi.nowtechnologies.database.jasypt.DecryptableProperties -DjasyptPassword=secret
Replace [PATH_TO_CHANGELOG] with the actual path.
More on that, could be found in liquibase documentation: http://www.liquibase.org/documentation/maven/generated/update-mojo.html

To rollback a apecific liquibase migration run the following command:
    mvn dependency:unpack resources:resources compile liquibase:rollback -Dliquibase.changeLogFile=[PATH_TO_CHANGELOG] -Denv=cucumber -Dliquibase.propertyProviderClass=mobi.nowtechnologies.database
    .jasypt.DecryptableProperties -DjasyptPassword=secret
Replace [PATH_TO_CHANGELOG] with the actual path.
More on that, could be found in liquibase documentation: http://www.liquibase.org/documentation/maven/generated/rollback-mojo.html