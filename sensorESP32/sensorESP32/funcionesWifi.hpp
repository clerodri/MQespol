//FUNCIONES BASICAS DE WIFI DE CONECTAR Y SCANEAR REDES WIFI
#include <WiFi.h> 

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
   Serial.println(WiFi.softAPIP());
   Serial.print("Nombre de mid Red esp32: ");
   Serial.println(ssid_ap);
    
   
   Serial.println("\n[*] Connecting to WiFi Network: ");
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







  
