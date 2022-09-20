#define CONNECTION_TIMEOUT 5
#define DHTTYPE   DHT22
#define DHTPIN    14
#define DHTPIN   4
String hostname= "app_net_temperatura";
const char* ssid = "Mqespol";
const char* password = "12345678";


const char* ssid_ap = "app_net_temperatura";
const char* password_ap = "12345678";
//boolean bandera=true;
//192.168.224.29 ip BROKER CON hostpot celular RONALDO
//192.168.100.53 IP BROKER COON WIFI CASA RONALDO
const char* MQTT_BROKER_ADRESS = "192.168.224.29"; //IP HOSPOT CELULAR RONALDO ANDROID 11
const uint16_t MQTT_PORT = 1883;
const char* MQTT_CLIENT_NAME = "ESP32"; //NOMBRE DEL CLIENTE

float hump;
float temp;
unsigned int localPort=1883;
char packetBuffer[255];//buffer que almacenara la data a enviar
char replyPacket[]="hola desde el ESP32";//valor a enviar cuando se realize la conexion 
IPAddress ip(192,168,4,22);
IPAddress gateway(192,168,4,9);
IPAddress subnet(255,255,255,0);
