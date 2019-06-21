package kz.itss.secretmessages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText txtIn;
    EditText txtKey;
    EditText txtOut;
    SeekBar sb;
    Button btn;
    String sMessage, sKey;
    String message = "";
    int iKey;

    public String encode(String message, int keyVal) {
        String output = ""; //$NON-NLS-1$

        char key = (char) keyVal;
        //char key1 = (char) keyVal;

        for (int x = 0; x < message.length(); x++) {
            //tRier++;
            char input = message.charAt(x);
            if (input >= 'А' && input <= 'Я') {
                input += key;
                if (input > 'Я')
                    input -= 32;
                if (input < 'А')
                    input += 32;
            } else if (input >= 'а' && input <= 'я') {
                input += key;
                if (input > 'я')
                    input -= 32;
                if (input < 'а')
                    input += 32;
            } else if (input >= '0' && input <= '9') {
                //key1 += input;
                input += (keyVal % 10);
                if (input > '9')
                    input -= 10;
                if (input < '0')
                    input += 10;
            } //English

            if (input >= 'A' && input <= 'Z') {
                input += key;
                if (input > 'Z')
                    input -= 26;
                if (input < 'A')
                    input += 26;
            } else if (input >= 'a' && input <= 'z') {
                input += key;
                if (input > 'z')
                    input -= 26;
                if (input < 'a')
                    input += 26;

            } else if (Character.isWhitespace(input)) {
                input = '\u0459';
            } else if (input == '\u0459') {
                input = ' ';
            } else if (input == ',') {
                input = '\u045A';
            } else if (input == '\u045A') {
                input = ',';
            }
            output += input;

        }

        return output;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtIn = (EditText) findViewById(R.id.txtIn);
        txtKey = (EditText) findViewById(R.id.txtKey);
        txtOut = (EditText) findViewById(R.id.txtOut);
        sb = (SeekBar) findViewById(R.id.sb);
        btn = (Button) findViewById(R.id.btn);
        txtIn.requestFocus();
        txtIn.selectAll();

        Intent receivedIntent = getIntent();
        String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        if (receivedText!= null)
            txtIn.setText(receivedText);

        /*Пказывает клавиатуру*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iKey >= 0) {
                    message = txtIn.getText().toString();
                }
                while (message.length() == 0) {
                    sMessage = "Введите текст для шифровки/расшифровки"; //$NON-NLS-1$
                    Toast.makeText(MainActivity.this, sMessage,
                            Toast.LENGTH_LONG).show();
                    message = "_"; //$NON-NLS-1$
                    txtIn.setText(sMessage);
                    txtIn.requestFocus();
                    txtIn.selectAll();
                }

                try {
                    iKey = Integer.parseInt(txtKey.getText().toString());

                    if ((iKey >= -25) && (iKey <= 25)) {
                        String result = encode(message, iKey);
                        txtOut.setText(result);
                        txtKey.setText(Integer.toString(iKey * -1));
                        message = txtOut.getText().toString();
                        iKey = iKey * -1;
                    } else {
                        sMessage = "Введите значение ключа  -25 до 25"; //$NON-NLS-1$
                        Toast.makeText(MainActivity.this, sMessage,
                                Toast.LENGTH_LONG).show();
                        txtKey.setText("13");
                    }

                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sMessage = "Введите значение ключа  -25 до 25"; //$NON-NLS-1$
                    Toast.makeText(MainActivity.this, sMessage,
                            Toast.LENGTH_LONG).show();
                    txtOut.setText(sMessage);
                    txtKey.requestFocus();
                    txtKey.selectAll();
                }
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int key = sb.getProgress()-25;
                String message = txtIn.getText().toString();
                String output = encode(message, key);
                txtOut.setText(output);
                txtKey.setText("" + key);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Secret Message "+
                        DateFormat.getDateTimeInstance().format(new Date()));

                shareIntent.putExtra(Intent.EXTRA_TEXT, txtOut.getText().toString());
                try {
                    startActivity(Intent.createChooser(shareIntent, "Share message…"));
                    finish();
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Error: Couldn't share.",
                            Toast.LENGTH_SHORT).show();
                }

            }
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }*/
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
