//Constantes 
#define CONNECTION_TIMEOUT 15
#define gasPin 35
String hostname= "app_net_gas";
const char* ssid = "Mqespol";
const char* password = "12345678";

const char* ssid_ap = "app_net_gas";
const char* password_ap = "12345678";

const char* MQTT_BROKER_ADRESS = "192.168.224.29";
const char* MQTT_CLIENT_NAME = "esp31";
const uint16_t MQTT_PORT = 1883;
IPAddress ip(192, 168, 4, 2);
IPAddress gateway(192, 168, 4, 9);
IPAddress subnet(255, 255, 255, 0);
String gas;
const float RL=1.0;  //1k omhm module
const float RO_CLEAN_AIR_RATIO = 9.86;
#define GAS_CO   1
float COCurve[3] = {2.3,0.72,-0.34}; //puntos de las curvas

float RS;
float RO=0.03;
float ratio;
float gas_volt;
float gas_analog;
float gas_value=0;
