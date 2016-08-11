package app.com.iotdroid.config;

/**
 * Created by masfajar on 7/17/2016.
 */
public class URLFormat {

    /**
     * Last feed channel
     * @param tokenChannel
     * @param tokenId
     * @return
     */
    public static String lastFeedChannel(String tokenChannel, String tokenId) {
        return "http://agnosthings.com/"+tokenChannel+"/field/last/feed/"+tokenId+"/switch";
    }

    /**
     * Update channel
     * @param tokenChannel
     * @param value
     * @return
     */
    public static String updateChannel(String tokenChannel,String value) {
        return "http://agnosthings.com/"+tokenChannel+"/feed?push=switch="+value;
    }

    /**
     * Last feed sensor
     * @param tokenSensor
     * @param sensorId
     * @return
     */
    public static String lastFeedSensor(String tokenSensor, String sensorId) {
        return "http://agnosthings.com/"+tokenSensor+"/field/last/feed/"+sensorId+"/notif";
    }

    /**
     * Update sensor
     * @param tokenSensor
     * @param value
     * @return
     */
    public static String updateSensor(String tokenSensor, String value) {
        return "http://agnosthings.com/"+tokenSensor+"/feed?push=notif="+value;
    }

    /**
     * Method for fajarlabs.com get last data
     * @param tokenSensor
     * @param args
     * @return
     */
    public static String lastFeedFajarlabs(String tokenSensor, String args) {
        return "http://fajarlabs.com/api/last/"+tokenSensor+"/"+args;
    }

    /**
     * Method for update feed
     * @param tokenSensor
     * @param strArgs
     * @return
     */
    public static String updateFeedFajarlabs(String tokenSensor,String strArgs) {
        return "http://fajarlabs.com/api/push/"+tokenSensor+"/?"+strArgs;
    }
}
