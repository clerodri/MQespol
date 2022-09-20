//FUNCIONES BASICAS DE WIFI DE CONECTAR Y SCANEAR REDES WIFI
#include <WiFi.h> 
#include <WiFiUdp.h>
WiFiUDP Udp;


/*void ConnectWiFi_STA()
{
   Serial.println("");
   WiFi.mode(WIFI_STA);
   Serial.print("Conectando a la red: ");
   Serial.println(ssid);
   WiFi.begin(ssid, password);
   int timeout_counter = 0;
   while (WiFi.status() != WL_CONNECTED) 
   { 
     delay(1000);  
     Serial.print('.'); 
     timeout_counter++;
     if(timeout_counter >= CONNECTION_TIMEOUT){
            ESP.restart();
        }
        
   }
   Serial.println("");
   Serial.print("Wifi Conectado a: ");
   Serial.println(ssid);
   Serial.print("IP address:\t");
   Serial.println(WiFi.localIP());
}*/
void veamoss(boolean bandera){
  Udp.begin(localPort);
  Serial.printf("IP del ESP32: %s, Escuchando en el puerto %d... ",WiFi.softAPIP().toString().c_str(),localPort);
  while(bandera){
      int packetSize= Udp.parsePacket();
      if(packetSize)
      {
    //    Serial.print("Mensaje Recibido: %d",packetSize); 
        int len=Udp.read(packetBuffer,255);
        if(len > 0){
           
            packetBuffer[len]=0;
        }
        Serial.printf("IP DEL WIFI CELULARE ES %s ",Udp.remoteIP().toString().c_str());
        
        Udp.beginPacket(Udp.remoteIP(),Udp.remotePort());
      //  Udp.write(replyPacket);
        Udp.print(replyPacket);
        Udp.endPacket();
        bandera=false;
      }
    }
  }
void ConnectWiFi_SOFTAP()
{
 //  WiFi.config(INADDR_NONE, INADDR_NONE, INADDR_NONE, INADDR_NONE);
   WiFi.setHostname(hostname.c_str()); //define hostname
   Serial.println("");
   WiFi.mode(WIFI_AP_STA);
   
   Serial.println("\n[*] Creating ESP32 AP");
   WiFi.softAP(ssid_ap,password_ap);
   WiFi.softAPConfig(ip, gateway, subnet);
   Serial.print("AP Created IP: ");
   //Serial.println(WiFi.softAPIP());
   Serial.print("Nombre de mid Red esp32: ");
   Serial.println(ssid_ap);
//   veamoss(true);
   
   Serial.println("\n[*] Connecting to WiFi Network");
   WiFi.begin(ssid,password);
   int timeout_counter = 0;
   while (WiFi.status() != WL_CONNECTED) 
   { 
     delay(1000);  
     Serial.print('.'); 
     timeout_counter++;
     if(timeout_counter >= CONNECTION_TIMEOUT){
            ESP.restart();
        }
   }
   Serial.println("");
   Serial.print("Wifi Conectado a: ");
   Serial.println(ssid);
   Serial.print("IP address:\t");
   Serial.println(WiFi.localIP());
}



/*void scanearRedesWifi() {
  numeroDeRedes = WiFi.scanNetworks();
  Serial.print("Numero de Redes Encontradas: ");
  Serial.println(numeroDeRedes);

  for (int i = 0; i < numeroDeRedes; i++) {
    
    Serial.print("Nombre de Red: ");
    Serial.println(WiFi.SSID(i));

    Serial.print("Fuerza de Señal: ");
    Serial.println(WiFi.RSSI(i));

    Serial.print("MAC Address: ");
    Serial.println(WiFi.BSSIDstr(i));
    Serial.println("---------------------------");
  }
}*/
