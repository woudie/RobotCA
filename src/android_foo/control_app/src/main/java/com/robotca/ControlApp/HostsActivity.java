package com.robotca.ControlApp;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.content.Context;
import android.widget.Toast;


import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.content.Context;
import java.io.File;
import java.io.BufferedReader;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.util.jar.Manifest;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.lang.Object;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
/**
 * Fragment containing the Map screen showing the real-world position of the Robot.
 *
 */
public class HostsActivity extends Activity{

    private EditText mytb;
    boolean busy=false;
    public HostsActivity() {
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hosts);
        //requestPermissions(new String[]{android.Manifest.permission.READ_I});
        final Context c = getApplicationContext();
        mytb = (EditText) findViewById(R.id.mytb);
        final File[] extMounts = c.getExternalFilesDirs(null);
        final File sdRoot = extMounts[0];

        final Button save = (Button) findViewById( R.id.button_save);
        save.setVisibility((View.GONE));

        final Button grab = (Button) findViewById( R.id.button_grab);

        grab.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {

                //Initialize
                String sourcepath = "/etc/hosts";
                File [] extMounts = c.getExternalFilesDirs(null);
                File sdRoot=extMounts[0];
                //the first block of array '0' represents the internal storage.
                //the second block of array '1' represents the external storage.
                //and so on in case we have OTG and other mount points
                if (sdRoot==null){
                    Toast.makeText(getApplicationContext(), "Failed to mount storage..", Toast.LENGTH_LONG).show();
                    return;
                }

                //Grabbing the file
                String targetpath = sdRoot.getAbsolutePath()+"/ROSHOSTS.txt";

                //Delete file if exists
                try {
                    File _delete = new File(sdRoot.getAbsolutePath(), "ROSHOSTS.txt");
                    boolean deleted = _delete.delete();
                    if (deleted)
                        Toast.makeText(getApplicationContext(), "Temp file found and deleted..", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "No Temp file found.Moving on..", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {}

                File sourceLocation= new File (sourcepath);
                File targetLocation= new File (targetpath);
                Toast.makeText(getApplicationContext(), "Importing...", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Copyed...", Toast.LENGTH_SHORT).show();


                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error while grabbing the file...", Toast.LENGTH_LONG).show();
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

                    Toast.makeText(getApplicationContext(), "Imported...", Toast.LENGTH_SHORT).show();
                    mytb.setText(text.toString());
                }
                catch (IOException e){
                    Toast.makeText(getApplicationContext(), "Error on importing...", Toast.LENGTH_LONG).show();

                }
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {

                busy=true;
                Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT).show();


                //Delete file if exists
                try {
                    File _delete = new File(sdRoot.getAbsolutePath(), "ROSHOSTS.txt");
                    boolean deleted = _delete.delete();
                    if (deleted)
                        Log.d("ROBOTCA-rosautom", "============Temp file deleted============");
                    else
                        Log.d("ROBOTCA-rosautom", "============No temp file found.Moving on...============");
                } catch (Exception e) {
                    Log.e("ROBOTCA-rosautom","============Error while deleting the file============");
                }

                //Save temp hosts with our edits
                File savedFile = new File(sdRoot.getAbsolutePath(), "ROSHOSTS.txt");
                String tbtext = mytb.getText().toString();
                try {
                    FileOutputStream fio = new FileOutputStream(savedFile);
                    OutputStreamWriter writer = new OutputStreamWriter(fio);
                    writer.write(tbtext);
                    writer.flush();
                    fio.getFD().sync();
                    writer.close();
                    Log.d("ROBOTCA-rosautom", "============Hosts successfully saved to temp folder============");
                    Toast.makeText(getApplicationContext(), "Hosts successfully saved to temp folder", Toast.LENGTH_LONG).show();

                }catch (IOException e) {
                    Log.d("ROBOTCA-rosautom", "============Error while saving hosts to temp folder============");
                }



                //Send hosts to /etc/hosts
                String tempHosts = sdRoot.getAbsolutePath() + "/ROSHOSTS.txt";

                //Mounting system as RW at runtime
                String[] mount = {"sysrw","mount -o remount,rw /system","sysrw"};
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (String tmpCmd : mount) {
                        os.writeBytes(tmpCmd + "\n");
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                    Toast.makeText(getApplicationContext(), "System Mounted as RW.", Toast.LENGTH_LONG).show();
                    Log.d("ROBOTCA-rosautom", "============System Mounted as RW============");
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error on saving hosts.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Hosts successfully saved to system folder.", Toast.LENGTH_LONG).show();
                    Log.d("ROBOTCA-rosautom", "============Hosts successfully saved to system folder============");
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error on saving hosts.", Toast.LENGTH_LONG).show();
                    Log.e("ROBOTCA-rosautom", "============Error while saving hosts to system folder============");
                }

                //Unmounting system as RW at runtime
                String[] unmount = {"sysrw","su mount -o ro,remount /system","sysrw"};
                try {
                    Process p = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    for (String tmpCmd : mount) {
                        os.writeBytes(tmpCmd + "\n");
                    }
                    os.writeBytes("exit\n");
                    os.flush();
                    Toast.makeText(getApplicationContext(), "System Mounted as RO again.", Toast.LENGTH_LONG).show();
                    Log.d("ROBOTCA-rosautom", "============System Mounted as RO============");
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error on saving hosts.", Toast.LENGTH_LONG).show();
                    Log.e("ROBOTCA-rosautom", "============Error while mounting system as read-only============");
                }
                Toast.makeText(getApplicationContext(), "You can now exit the activity safely.", Toast.LENGTH_LONG).show();
                busy=false;
            }
        });



        mytb.addTextChangedListener(new TextWatcher() {

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



    @Override
    public void onBackPressed()
    {
        if (busy) {
            Toast.makeText(getApplicationContext(), "I AM BUSY.DONT PUSH ME!", Toast.LENGTH_LONG).show();
            return;
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
            return;
        } else {
            startActivity(new Intent(this, ControlApp.class));
        }
    }


  



}

