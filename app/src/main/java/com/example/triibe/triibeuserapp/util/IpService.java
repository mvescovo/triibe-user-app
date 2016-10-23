package com.example.triibe.triibeuserapp.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.triibe.triibeuserapp.data.TriibeRepository;
import com.example.triibe.triibeuserapp.trackData.Connection;
import com.example.triibe.triibeuserapp.trackData.ScreenActive;
import com.example.triibe.triibeuserapp.trackData.ScreenReceiver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Matthew on 2/09/2016.
 * The Service to track Ip Information and the screen on ans off times.
 */
public class IpService extends Service {

    //change the CHECK_INTERVAL to manage how often you check for active connections .
    public static final long CHECK_INTERVAL = 1500;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    /*********FIREBASE*********/
    private TriibeRepository mTriibeRepository;
    private String mUserId = null;
    /**************************/

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
    String currentDate;
    String dateInput;


    private Map<String, Object> totalConMap = new HashMap<>();
    private Map<String, Connection> currentConMap = new HashMap<>();
    private Map<String, Connection> previousConMap = new HashMap<>();
    Map<String, Object> connectionValues;

    BroadcastReceiver mReceiver;
    IntentFilter filter;
    private Map<String, Object> timeMap = new HashMap<>();
    Map<String, Object> timeVaules;
    ScreenActive Active;
    String timeKey;


    //Don't know what this method does but it is required to have a service
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, CHECK_INTERVAL);
        currentDate = date.format(new Date());

        /*********FIREBASE*********/
        mTriibeRepository = Globals.getInstance().getTriibeRepository();
        /**************************/

        //screen checking
        filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    netStatCall();
                    compareConnection();
                }
            });
        }
    }
    /**NETSTAT AND CONNECTION TRACKING
     * Calls the in built linux function Netstat using a command line call which returns network information,
     * we filter for Established connections and then tokenise the data and save the connections in the hashmaps.
     * */
    public void netStatCall(){
        Connection tempCon;
        String s;
        String strOutPut = "";
        Process p;
        String cmd = "netstat";
        String delims = "[ ]+";
        String[] tokens;
        try {
            p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null){
                // If the line contains the text "ESTABLISHED" - change this if you want to filter for something else.
                if( s.contains("ESTABLISHED")){
                    // split the line into tokens
                    tokens = s.split(delims);
                    // print out the foreign address (token 4)
                    strOutPut = strOutPut + tokens[0]+ " connection to " + tokens[4] + "\n";
                    Date date = new Date();
                    dateInput = df.format(date);
                    tempCon = new Connection(tokens[0],tokens[4],dateInput);
                    //add each connection to the current connections map.
                    currentConMap.put(tempCon.getIpAddrURL(),tempCon);
                }
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    /**
     * Compare the current connections to the previous connections, removing the connections that are no
     * longer in the previous connections and adding them to the total connections.
     *  */
    public void compareConnection(){
        //System.out.println("COMPARE CONNECTION CALLED");
        if (previousConMap.isEmpty()&&currentConMap.isEmpty()){
            // if both th hashmaps are empty simply end the function
            return;
        }
        if (previousConMap.isEmpty()){
            for (Map.Entry<String, Connection> entry : currentConMap.entrySet()) {
                previousConMap.put(entry.getValue().getIpAddrURL(),currentConMap.get(entry.getValue().getIpAddrURL()));
                //  System.out.println("First Adding to previous");
                //  System.out.println(entry.getValue().getIpAddrURL());
            }
        }else {
            for (Map.Entry<String, Connection> entry : currentConMap.entrySet()) {
                if (previousConMap.containsKey(entry.getValue().getIpAddrURL())){
                    // The ip is already in the previous connection map so make no changes.
                    // System.out.println(entry.getValue().getIpAddrURL());
                }else{
                    // The ip was not found in the previous connection map so we add it in.
                    previousConMap.put(entry.getValue().getIpAddrURL(),currentConMap.get(entry.getValue().getIpAddrURL()));
                }
            }
        }
        for(Iterator<Map.Entry<String, Connection>> it = previousConMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Connection> entry = it.next();
            if (currentConMap.containsKey(entry.getValue().getIpAddrURL())){
                //If the Ip Address is in both lists.
                currentConMap.remove(entry.getValue().getIpAddrURL());
            }else{
                //System.out.println("the value: "+ entry.getValue().getIpAddrURL()+"no longer in current removing adding to total");
                Date date = new Date();
                dateInput = df.format(date);
                previousConMap.get(entry.getValue().getIpAddrURL()).setEndConnection(dateInput);
                /*********FIREBASE*********/

                final String[] dataKey = new String[1];
                mTriibeRepository.getDataKey(new TriibeRepository.GetDataKeyCallback() {
                    @Override
                    public void onDataKeyLoaded(@Nullable String key) {
                        dataKey[0] = key;
                    }
                });
                connectionValues = previousConMap.get(entry.getValue().getIpAddrURL()).toMap();
                totalConMap.put("/data/Total Connections/"+mUserId+"/"+currentDate+"/"+ dataKey[0],connectionValues);
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mTriibeRepository.saveConnection(totalConMap);
                /**************************/
                it.remove();
            }
        }
    }

    /**
     * Runs when the service first starts before the on create.  Here we set up the Broadcast
     * Reciver to detect when the screen turns on and off as well ass the calls to add that data
     * to the Server.
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       if(mUserId==null){
        mUserId = intent.getStringExtra("USERID");
       }

        // Let it continue running until it is stopped.
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            System.out.println("SCREEN ON");
            /*********FIREBASE*********/
            mTriibeRepository.getTimeKey(mUserId, currentDate, new TriibeRepository.GetTimeKeyCallback() {
                @Override
                public void onTimeKeyLoaded(@Nullable String key) {
                    timeKey = key;
                }
            });
            /**************************/
            Date sDate = new Date();
            dateInput = df.format(sDate);
            Active = new ScreenActive(dateInput);
            System.out.println(Active.getStartTime());
        } else {
            System.out.println("SCREEN OFF");
            Date eDate = new Date();
            dateInput = df.format(eDate);
            Active.setStopTime(dateInput);
            System.out.println(Active.getStopTime());
            /*********FIREBASE*********/
            timeVaules = Active.toMap();
            timeMap.put("/data/Screen Time/"+mUserId+"/"+currentDate+"/"+timeKey,timeVaules);
            mTriibeRepository.saveScreenTime(timeMap);
            /**************************/
        }
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_LONG).show();
        //start sticky means that the service will not be shutdown so easily if there are more memory is being take up by other applications.
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Add all Remain connections to the DataBase
        for(Iterator<Map.Entry<String, Connection>> it = previousConMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Connection> entry = it.next();
            //System.out.println("the value: "+ entry.getValue().getIpAddrURL()+"no longer in current removing adding to total");
            Date date = new Date();
            dateInput = df.format(date);
            previousConMap.get(entry.getValue().getIpAddrURL()).setEndConnection(dateInput);
            /*********FIREBASE*********/
            final String[] dataKey = new String[1];
            mTriibeRepository.getDataKey(new TriibeRepository.GetDataKeyCallback() {
                @Override
                public void onDataKeyLoaded(@Nullable String key) {
                    dataKey[0] = key;
                }
            });
            connectionValues = previousConMap.get(entry.getValue().getIpAddrURL()).toMap();
            totalConMap.put("/data/Total Connections/"+mUserId+"/"+currentDate+"/"+dataKey,connectionValues);
            mTriibeRepository.saveConnection(totalConMap);
            /**************************/
            it.remove();
        }
        unregisterReceiver(mReceiver);
        //stop the timer so that it dosent continue to run even after you kill the service.
        mTimer.cancel();
        Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_LONG).show();
    }

}


/*CONNECTION INFORMATION - we can filter for these outputs if we want something specific.
Proto
       The protocol (tcp, udp, raw) used by the socket.

   Recv-Q
       The  count  of  bytes  not copied by the user program connected to this
       socket.

   Send-Q
       The count of bytes not acknowledged by the remote host.

   Local Address
       Address and port number of the local end of  the  socket.   Unless  the
       --numeric  (-n)  option is specified, the socket address is resolved to
       its canonical host name (FQDN), and the port number is translated  into
       the corresponding service name.

   Foreign Address
       Address  and port number of the remote end of the socket.  Analogous to
       "Local Address."

   State
       The state of the socket. Since there are no states in raw mode and usu‐
       ally  no  states  used  in UDP, this column may be left blank.Normally
        this can be one of several values:

   ESTABLISHED
          The socket has an established connection.

   SYN_SENT
          The socket is actively attempting to establish a connection.

   SYN_RECV
          A connection request has been received from the network.

   FIN_WAIT1
          The socket is closed, and the connection is shutting down.

   FIN_WAIT2
          Connection is closed, and the socket is waiting for  a  shutdown
          from the remote end.

   TIME_WAIT
          The socket is waiting after close to handle packets still in the
          network.

   CLOSE  The socket is not being used.

   CLOSE_WAIT
          The remote end has shut down, waiting for the socket to close.

   LAST_ACK
          The remote end has shut down, and the socket is closed.  Waiting
          for acknowledgement.

   LISTEN The  socket is listening for incoming connections.  Such sockets
          are not included in the output unless you specify the  --listen‐
          ing (-l) or --all (-a) option.
 */
