package netbit.develop.food;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectActivity extends AppCompatActivity {

    Button deliverBtn,consumerBtn,donaterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        deliverBtn = (Button) findViewById(R.id.deliverButton);
        consumerBtn = (Button) findViewById(R.id.consumerButton);
        donaterBtn = (Button) findViewById(R.id.donatorButton);

        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, DeliverActivity.class);
                startActivity(intent);
            }
        });

        consumerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        donaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });
    }
}
