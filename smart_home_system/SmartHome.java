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


//2.3 SmartDevice (abstract base class)
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

    public String getDetails(){
        return "Device ID: "+id+"\n"+
                "Name: "+name+"\n"+
                "Status: "+status+"\n"+
                "Connectivity: "+connectivity+"\n" +
                "Rated Power: "+ratedPowerWatts;
    }
}

//2.4 SmartLight extends SmartDevice
class SmartLight extends SmartDevice{
    private int brightness;
    private  LightColorMode colorMode;

    public  SmartLight(String id,
                       String name,
                       double ratedPowerWatts,
                       LocalDateTime createdAt){
        super(id, name, ratedPowerWatts, createdAt);
        this.brightness=0;
        this.colorMode=LightColorMode.COOL;
    }

    public void setBrightness(int value){
        if(value>=0 && value<=100){
            this.brightness=value;
        }else{
            throw new IllegalArgumentException("Invalid input");
        }
    }
    public  int getBrightness(){
        return brightness;
    }
    public void setColorMode(LightColorMode mode){
        this.colorMode=mode;
    }
    public LightColorMode getColorMode(){
        return colorMode;
    }
    @Override
    public String getDetails(){
        return super.getDetails()+"\n"+
                "Brightness: "+brightness+"\n"+
                "Color mode: "+colorMode;
    }
    @Override
    public double getEnergyUsageKWh(double hours) {
        double energy=super.getEnergyUsageKWh(hours)*(this.brightness/100.0);
        return energy;
    }
}

public class SmartHome{
    public static void main(String[] args){

    }
}