FROM tomcat:9.0

# Set working directory inside container
WORKDIR /build

# Copy source code
COPY src/main/java ./src
COPY src/main/webapp ./webapp

# Create output folder for compiled classes
RUN mkdir -p /build/classes

# Compile Java files, include all libs in classpath
RUN javac -d /build/classes \
    -cp "/build/webapp/WEB-INF/lib/*" \
    $(find ./src -name "*.java")

# Create expected webapp structure for deployment
RUN mkdir -p /usr/local/tomcat/webapps/ChatApp/WEB-INF/classes && \
    cp -r /build/classes/* /usr/local/tomcat/webapps/ChatApp/WEB-INF/classes && \
    cp -r /build/webapp/* /usr/local/tomcat/webapps/ChatApp/

EXPOSE 8080