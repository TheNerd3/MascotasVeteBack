# Imagen base: Tomcat 10.1 con Java 17 (Temurin), lista para correr
# aplicaciones web empaquetadas como WAR.
FROM tomcat:10.1-jdk17-temurin

# Limpiar aplicaciones predeterminadas de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el WAR con context path /api/v1
COPY target/*.war "/usr/local/tomcat/webapps/api#v1.war"

# Puerto donde Tomcat va a escuchar pedidos dentro del contenedor
EXPOSE 8080

# Arrancar Tomcat en primer plano (para que el contenedor siga vivo)
CMD ["catalina.sh", "run"]
