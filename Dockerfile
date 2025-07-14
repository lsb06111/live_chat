# Stage 1: Use a JDK to compile the servlet
FROM openjdk:17 AS builder

WORKDIR /build

# Copy source code
COPY src/main/java/ChatServlet.java src/
COPY src/main/webapp/WEB-INF/lib lib/

# Compile ChatServlet.java
RUN mkdir -p classes && \
    javac -d classes -cp "lib/*" src/ChatServlet.java


# Stage 2: Use Tomcat and deploy
FROM tomcat:9.0

# Remove default ROOT
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy static files (HTML, etc.)
COPY src/main/webapp /usr/local/tomcat/webapps/ChatApp

# Copy compiled classes and libraries
COPY --from=builder /build/classes /usr/local/tomcat/webapps/ChatApp/WEB-INF/classes
COPY --from=builder /build/lib /usr/local/tomcat/webapps/ChatApp/WEB-INF/lib