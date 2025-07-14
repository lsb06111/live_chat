
FROM tomcat:9.0

COPY src/main/webapp /usr/local/tomcat/webapps/ChatApp
COPY build/classes /usr/local/tomcat/webapps/ChatApp/WEB-INF/classes
COPY src/main/webapp/WEB-INF/lib /usr/local/tomcat/webapps/ChatApp/WEB-INF/lib
