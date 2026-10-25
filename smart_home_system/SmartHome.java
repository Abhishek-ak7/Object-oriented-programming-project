package  smart_home_system;

import java.time.LocalDateTime;

enum DeviceStatus{
    ON,
    OFF
}
enum ConnectivityStatus{
    ONLINE,
    OFFLINE
}
enum LightColorMode{
    WARM,
    COOL,
    RGB
}

abstract class SmartDevice{
    private String id;
    private  String name;
    private  DeviceStatus status;
    private ConnectivityStatus connectivity;
    private double ratedPowerWatts;
    private LocalDateTime createdAt;

    public SmartDevice(String id,
                       String name,
                       double ratedPowerWatts,
                       LocalDateTime createdAt){
        this.id=id;
        this.name=name;
        this.status=DeviceStatus.OFF;
        this.connectivity=ConnectivityStatus.ONLINE;
        this.ratedPowerWatts=ratedPowerWatts;
        this.createdAt=createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public ConnectivityStatus getConnectivity() {
        return connectivity;
    }

    protected void ensureOnline(){
        if(this.connectivity==ConnectivityStatus.OFFLINE){
            throw new IllegalStateException("Device is offline and cannot be controlled.");
        }
    }
    protected void validateCanTurnOn(){
        if(this.status==DeviceStatus.ON){
            throw  new IllegalStateException("Device is already on");
        }
    }

    public  void turnOn(){
        ensureOnline();
        validateCanTurnOn();
        this.status=DeviceStatus.ON;
    }
    public  void turnOff(){
        ensureOnline();
        this.status=DeviceStatus.OFF;
    }
    public double getEnergyUsageKWh(double hours){
        if(hours<0){
            throw new IllegalArgumentException("Input hours is not valid");
        }
        double energy= (this.ratedPowerWatts*hours)/1000;
        return energy;
    }
}



public class SmartHome{
    public static void main(String[] args){

    }
}