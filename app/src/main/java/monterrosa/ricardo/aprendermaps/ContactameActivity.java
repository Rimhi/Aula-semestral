package monterrosa.ricardo.aprendermaps;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ContactameActivity extends AppCompatActivity {
    TextView iragithub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactame);
        iragithub = findViewById(R.id.iraGithub);

        iragithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/Rimhi");
                Intent ir = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(ir);
            }
        });
    }
}
