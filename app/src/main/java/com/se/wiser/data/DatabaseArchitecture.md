https://developer.android.com/training/data-storage/room/async-queries
https://developer.android.com/training/data-storage/room/relationships

Device     Sensor
                    Motion
                    WaterLeakage
                    Iullminance
                    Door


           OnOff
           Dimmer
           HumidityAndTemper
           Socket
           Electrical
           Shutter

//操作
1.记录设备列表，
2.记录网关信息，
    Bridge
    WIFI
    Thread
3.
4.

//启动时，检查WI-FI是否是上次记录的网络，
如果是则，根据记录的网关信息进行连接设备，
//如何鉴别设备是否还在这个网关上呢

//展示
1.查询所有设备表，获取所有设备，并观察，当设备状态发生变化时，
更新设备状态，
