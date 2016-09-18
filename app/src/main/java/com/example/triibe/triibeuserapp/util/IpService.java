package com.example.triibe.triibeuserapp.util;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.triibe.triibeuserapp.trackData.Connection;
import com.example.triibe.triibeuserapp.trackData.ScreenActive;
import com.example.triibe.triibeuserapp.trackData.ScreenReceiver;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Matthew on 2/09/2016.
 */
public class IpService extends Service {

    //change the CHECK_INTERVAL to manage how often you check for active connections .
    public static final long CHECK_INTERVAL = 1500;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    /*********FIREBASE*********/
    private DatabaseReference mDatabase;
    /**************************/

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
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
    String timeKey = "";


    //dont know what this method does but it is required to have a service
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

        /*********FIREBASE*********/
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                    if(!totalConMap.isEmpty()){
                      /*  System.out.println("----------total connections so far---------");
                        for (Map.Entry<String, Object> entry : totalConMap.entrySet()) {
                            System.out.println(entry.getValue().getIpAddrURL());
                        }
                        System.out.println("------------------------------------");*/
                    }
                    if(!timeMap.isEmpty()){
                       /* System.out.println("----------total screentime ---------");
                        for (Map.Entry<String, Object> entry : timeMap.entrySet()) {
                            if(!(entry.getValue().getStopTime()==null)){
                                System.out.println(entry.getValue().getStartTime()+" - "+entry.getValue().getStopTime());
                            }
                        }
                        System.out.println("------------------------------------");*/}
                }
            });
        }
    }
    //NETSTAT AND CONNECTION TRACKING
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
                // the line contains ESTABLISHED - change this if you want to filter for somthing else.
                if( s.contains("ESTABLISHED")){
                    // split the line into tokens
                    tokens = s.split(delims);
                    // print out the foreign address (token 4)
                    //System.out.println("line: " + s);
                    strOutPut = strOutPut + tokens[0]+ " connection to " + tokens[4] + "\n";
                    Date date = new Date();
                    dateInput = df.format(date);
                    tempCon = new Connection(tokens[0],tokens[4],dateInput);
                    //add each connection to the current connections map.
                    currentConMap.put(tempCon.getIpAddrURL(),tempCon);
                }
            }
            p.waitFor();
            //System.out.println ("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            System.out.println("ERROR");
        }
    }

    //compare the current connection to the previous connections
    public void compareConnection(){

        //System.out.println("----------------Compare connection called-------------");
        if (previousConMap.isEmpty()&&currentConMap.isEmpty()){
           // System.out.println("both hashmaps are empty");
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
                   // System.out.println("The ip is already in the previous connection map");
                   // System.out.println(entry.getValue().getIpAddrURL());
                }else{
                    previousConMap.put(entry.getValue().getIpAddrURL(),currentConMap.get(entry.getValue().getIpAddrURL()));
                   // System.out.print("Adding to previous: ");
                   // System.out.println(entry.getValue().getIpAddrURL());
                }
            }
        }
        for(Iterator<Map.Entry<String, Connection>> it = previousConMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Connection> entry = it.next();
            if (currentConMap.containsKey(entry.getValue().getIpAddrURL())){
                //System.out.print("this ip is in the both lists: ");
                //System.out.println(entry.getValue().getIpAddrURL());
                currentConMap.remove(entry.getValue().getIpAddrURL());
            }else{
                //System.out.println("the value: "+ entry.getValue().getIpAddrURL()+"no longer in current removing adding to total");
                Date date = new Date();
                dateInput = df.format(date);
                previousConMap.get(entry.getValue().getIpAddrURL()).setEndConnection(dateInput);
                /*********FIREBASE*********/
                String dataKey = mDatabase.child("data").child("Total Connections").push().getKey();
                connectionValues = previousConMap.get(entry.getValue().getIpAddrURL()).toMap();
                totalConMap.put("/data/Total Connections/"+dataKey,connectionValues);
                mDatabase.updateChildren(totalConMap);
                /**************************/
                it.remove();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            System.out.println("SCREEN ON");
            /*********FIREBASE*********/
            timeKey = mDatabase.child("data").child("Screen Time").push().getKey();
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
                timeMap.put("/data/Screen Time/"+ timeKey,timeVaules);
                mDatabase.updateChildren(timeMap);
                /**************************/
        }
        Toast.makeText(this, "Tracking Started", Toast.LENGTH_LONG).show();
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
                String dataKey = mDatabase.child("data").child("Total Connections").push().getKey();
                connectionValues = previousConMap.get(entry.getValue().getIpAddrURL()).toMap();
                totalConMap.put("/data/Total Connections/"+dataKey,connectionValues);
                mDatabase.updateChildren(totalConMap);
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

/*


//CODE FROM THE MAIN ACTIVITY EXAMPLES OF THE FUNCTIONS USED TO CALL THE SERVICE


        import android.content.Context;
        import android.content.Intent;

        import android.os.PowerManager;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    PowerManager mgr;
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");

        Button b = (Button) findViewById(R.id.button);
        b.setText("start");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                if (b.getText().equals("stop")) {
                    stopService(view);
                    b.setText("start");
                    wakeLock.release();
                } else {
                    wakeLock.acquire();
                    startService(view);
                    b.setText("stop");
                }
            }
        });
    }
    // Method to start the service
    public void startService(View view) {
        startService(new Intent(getBaseContext(), IpService.class));
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), IpService.class));
    }


}*/
