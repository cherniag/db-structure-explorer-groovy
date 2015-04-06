To deploy to locally installed Tomcat put to your ${catalina.base}/conf:

conf/application.properties
conf/trackrepo-application.properties
conf/streamzine/*
conf/dev/*

1. Copy everything from conf/cherry to ${catalina.base}/conf/dev and modify to your local system all file paths, urls, logins, passwords
2. In your system properties set env=dev
