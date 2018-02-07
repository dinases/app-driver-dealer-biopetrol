package com.luisbar.waterdelivery.presentation.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.luisbar.waterdelivery.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigActivity extends AppCompatActivity {

    @BindView(R.id.et_ip)
    EditText etIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);

        if (getHost() != null)
            startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * It saves the ip
     * @param ip Ip
     */
    private void setFcmId(String ip) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("IP", ip);
        editor.commit();
    }

    @OnClick(R.id.b_ip)
    public void onClick() {
        if (!etIp.getText().toString().isEmpty()) {
            setFcmId(etIp.getText().toString());
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private String getHost() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        return settings.getString("IP", null);
    }

}
