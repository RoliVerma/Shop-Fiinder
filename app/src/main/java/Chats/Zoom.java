package Chats;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepth.maps.R;
import com.github.chrisbanes.photoview.PhotoView;

public class Zoom extends AppCompatActivity {

    PhotoView i1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_zoom);

//        i1=findViewById(R.id.zoom);
//
//        Bundle bundle=getIntent().getExtras();
//
//        if(bundle!=null)
//        {
//            int resid=bundle.getInt("resId");
//            i1.setImageResource(resid);
//        }SS
        i1=findViewById(R.id.zoom);
        String imgUrl=getIntent().getStringExtra("img");
        Glide.with(Zoom.this).load(imgUrl).into(i1);


    }
}
