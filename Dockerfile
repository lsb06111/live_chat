FROM tomcat:9.0

# Create build directory
RUN mkdir -p /build/classes

# Copy servlet and libraries
COPY src/main/java/ChatServlet.java /build/src/
COPY src/main/webapp/WEB-INF/lib /build/lib

# Compile ChatServlet.java
RUN javac -d /build/classes -cp "/build/lib/*" /build/src/ChatServlet.java

# Copy static web content
COPY src/main/webapp /usr/local/tomcat/webapps/ChatApp

# Copy compiled classes
COPY /build/classes /usr/local/tomcat/webapps/ChatApp/WEB-INF/classes

# Copy libs into WEB-INF/lib
COPY src/main/webapp/WEB-INF/lib /usr/local/tomcat/webapps/ChatApp/WEB-INF/lib
