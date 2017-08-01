package com.robotca.ControlApp;

import android.os.Bundle;
import android.view.View;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.content.Context;
import android.widget.Toast;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import android.widget.Button;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
/**
 * Activity showing device's hosts.Root access is required
 *
 */
public class HostsActivity extends Activity{

    boolean busy=false;

    // Log tag String
    private static final String TAG = "HostsActivity";

    public HostsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.hosts);

        //INITIALIZE
        //cant be public for some reason.maybe its needed for the protected qualifier
        final EditText HostsTB = (EditText) findViewById(R.id.HostsTB);
        final Button save = (Button) findViewById( R.id.button_save);
        final Context c = getApplicationContext();
        final File[] extMounts = c.getExternalFilesDirs(null);
        final File sdRoot = extMounts[0];
        //the first block of array '0' represents the internal storage.
        //the second block of array '1' represents the external storage.
        //and so on in case we have OTG and other mount points

        //final Button grab = (Button) findViewById( R.id.button_grab);
        String sourcepath = "/etc/hosts";


        save.setVisibility((View.GONE));

        //SuperSU handle
        Process p;
        try {
           p = Runtime.getRuntime().exec("su");
        } catch (Exception e) {
            Log.e(TAG, "Could not get Root Access.", e);
            startActivity(new Intent(this, ControlApp.class));
            printToast("Could not get Root Access", Toast.LENGTH_LONG);
        }

        // grab.setOnClickListener(new View.OnClickListener() {
        //  public void onClick (View v) {

        if (sdRoot == null) {
            printToast("Failed to mount storage...", Toast.LENGTH_LONG);
            return;
        }

        //Grabbing the file
        String targetpath = sdRoot.getAbsolutePath() + "/ROSHOSTS.txt";

        //Delete file if exists
        try {
            File _delete = new File(sdRoot.getAbsolutePath(), "ROSHOSTS.txt");
            boolean deleted = _delete.delete();
            if (deleted)
                printToast("Temp file found and deleted..", Toast.LENGTH_SHORT);
            else
                printToast("No Temp file found.Moving on..", Toast.LENGTH_SHORT);

        } catch (Exception e) {
            //do nothing
        }
        File sourceLocation = new File(sourcepath);
        File targetLocation = new File(targetpath);
        printToast("Importing...", Toast.LENGTH_SHORT);
        try {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            printToast("Copyed...", Toast.LENGTH_SHORT);


        } catch (Exception e) {
            printToast("Error while grabbing the file...", Toast.LENGTH_LONG);
        }

        //Copying importing to textbox
        StringBuilder text = new StringBuilder();
        try {

            BufferedReader br = new BufferedReader(new FileReader(targetpath));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            printToast("Imported...", Toast.LENGTH_SHORT);
            HostsTB.setText(text.toString());
        } catch (IOException e) {
            printToast("Error on importing...", Toast.LENGTH_LONG);

        }
        // }
        //});


        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                busy = true;
                printToast("Saving...", Toast.LENGTH_SHORT);


                //Delete file if exists
                try {
                    File _delete = new File(sdRoot.getAbsolutePath(), "ROSHOSTS.txt");
                    boolean deleted = _delete.delete();
                    if (deleted)
                        Log.d("ROBOTCA-rosautom", "============Temp file deleted============");
                    else
                        Log.d("ROBOTCA-rosautom", "============No temp file found.Moving on...============");
                } catch (Exception e) {
                    Log.e("ROBOTCA-rosautom", "============Error while deleting the file============");
                }

                //Save temp hosts with our edits
                File savedFile = new File(sdRoot.getAbsolutePath(), "ROSHOSTS.txt");
                String tbtext = HostsTB.getText().toString();
                try {
                    FileOutputStream fio = new FileOutputStream(savedFile);
                    OutputStreamWriter writer = new OutputStreamWriter(fio);
                    writer.write(tbtext);
                    writer.flush();
                    fio.getFD().sync();
                    writer.close();
                    Log.d("ROBOTCA-rosautom", "============Hosts successfully saved to temp folder============");
                    printToast("Hosts successfully saved to temp folder", Toast.LENGTH_LONG);

                } catch (IOException e) {
                    Log.d("ROBOTCA-rosautom", "============Error while saving hosts to temp folder============");
                }


                //Send hosts to /etc/hosts
                String tempHosts = sdRoot.getAbsolutePath() + "/ROSHOSTS.txt";

                //Mounting system as RW at runtime
                String[] mount = {"sysrw", "mount -o remount,rw /system", "sysrw"};
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (String tmpCmd : mount) {
                        os.writeBytes(tmpCmd + "\n");
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                    printToast("System Mounted as RW.", Toast.LENGTH_LONG);
                    Log.d("ROBOTCA-rosautom", "============System Mounted as RW============");
                } catch (IOException e) {
                    printToast("Error on saving hosts.", Toast.LENGTH_LONG);
                    Log.e("ROBOTCA-rosautom", "============Error while mounting system============");
                }
                //Copying file
                String[] deviceCommands = {"sysrw", "cat " + tempHosts + " > /etc/hosts ", "sysrw"};
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (String tmpCmd : deviceCommands) {
                        os.writeBytes(tmpCmd + "\n");
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                    printToast("Hosts successfully saved to system folder.", Toast.LENGTH_LONG);
                    Log.d("ROBOTCA-rosautom", "============Hosts successfully saved to system folder============");
                } catch (IOException e) {
                    printToast("Error on saving hosts.", Toast.LENGTH_LONG);
                    Log.e("ROBOTCA-rosautom", "============Error while saving hosts to system folder============");
                }

                //Unmounting system as RW at runtime
                String[] unmount = {"sysrw", "su mount -o ro,remount /system", "sysrw"};
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (String tmpCmd : unmount) {
                        os.writeBytes(tmpCmd + "\n");
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                    printToast("System Mounted as RO again.", Toast.LENGTH_LONG);
                    Log.d("ROBOTCA-rosautom", "============System Mounted as RO============");
                } catch (IOException e) {
                    printToast("Error on saving hosts.", Toast.LENGTH_LONG);
                    Log.e("ROBOTCA-rosautom", "============Error while mounting system as read-only============");
                }
                printToast("You can now exit the activity safely.", Toast.LENGTH_LONG);
                busy = false;
            }
        });


        HostsTB.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                save.setVisibility((View.VISIBLE));
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //has to be implented inside listener
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //has to be implented inside listener
            }
        });


    }

    private void printToast(String s,int dur) {
        Toast.makeText(getApplicationContext(), s, dur).show();
    }

    @Override
    public void onBackPressed()
    {
        if (busy) {
            Toast.makeText(getApplicationContext(), "I AM BUSY.DON'T PUSH ME!", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, ControlApp.class));
        }
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (busy) {
            Toast.makeText(getApplicationContext(), "I AM BUSY.DONT PUSH ME!", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, ControlApp.class));
        }
    }


  



}

